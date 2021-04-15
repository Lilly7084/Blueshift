# TEC Redshift Disk Specification (Revision 002)
[The original specification](https://github.com/Rami-Sabbagh/TEC-Redshift-Disk-Specification) was written by Rami Sabbagh. I decided to fork this project and clean up the layout a little bit, so someone looking to implement this in software can go about it in a more linear fashion.

One discovery I made  is that the uncompressed solution data is **exactly the same** as the .SOLUTION format used by the game, which explains the redundant `PB039` in the header, and might help me figure out the purpose of some of those unknown bytes and ints.

# Reading the image file
The solution data is stored directly on the image using a method called *steganography*. To read out the image, implement this code in your preferred language:

```
Allocate a bit stream with [width * height * 3] slots
For each bit plane in the image, from LSB to MSB:
	For each pixel in the image (Left to right, then top to bottom):
		Add lowest bit of red channel to bit stream
		Add lowest bit of green channel to bit stream
		Add lowest bit of blue channel to bit stream
```

Please note that with very large solutions, the game may [store data in higher bit planes.](https://www.reddit.com/r/exapunks/comments/dumqv1/if_youre_curious_a_game_cartridge_filled_to_the/) It's recommended that your reader/writer program supports this mode.

You should now have a bit stream, which with normal disk images should contain 279,000 bits. Next, you need to convert this bit stream into a byte stream *(or add those bits to the byte stream immediately)*, LSB first:

```
| 1 2 4 8 16 32 64 128 | 1 2 4 8 16 32 64 128 | ...
  ^                    --->
First bit         Stream flow
```

# Data types
The .SOLUTION file format uses multiple different data types:
- (Byte) Pretty self-explanatory, just a single byte
- (Boolean) 1 byte, True = 0x01, False = 0x00
- (Int) 32-bit little-endian integer
- (String) Complex data type used to store ASCII or UTF-8 text
  - (Int) Length of string data
  - (Byte[]) Arbitrary, non-terminated ASCII or UTF-8 data
- (Table{Object}) Complex data type used to store arrays of objects
  - (Int) Number of entries
  - (TableEntry{Object}[]) For each entry:
    - (Int) Entry index, starting at 0
	- (Object) Entry data

# Raw disk image data
The data on the disk image itself consists of the following:
- (Int) Length of the compressed solution data
- (Int) 16-bit [Fletcher's checksum](https://en.wikipedia.org/wiki/Fletcher's_checksum) of the compressed solution data, stored in a 32-bit Int, for some reason
- (Byte[]) Compressed solution data
- Any data you might find after this point is irrelevant garbage data. Just don't use it - If you're reading the disk in real time, you should just stop at this point.

The solution data is compressed using Zlib / DEFLATE, to save disk space.

# Decompressed image data
As mentioned at the start, the decompressed image data is just a .SOLUTION file. I'd imagine that, since Zachtronics already made a file format just to hold user code, they decided to not make **another** format just to load onto the disks, and instead just loaded a compressed copy of a .SOLUTION file onto the disk.

The .SOLUTION file format consists of a **header** and one or more **agents**, stuck together with no delimiters or padding.

**Note**: Due to a recent discovery (see `HeaderBreakdown.txt`) regarding the headers of these files, I've been forced to revise the *Data types* and *Header data* sections.

## Header data
- (Int) Unknown purpose - Most likely used as file magic, always `FE 03 00 00`
- (String) Level ID - For Redshift disks, this is always `PB039`
- (String) Solution name - Usually written on the disk image
- (Int) Unknown purpose - Most likely used as padding, always `00 00 00 00`
- (Int) Auxiliary statistics - Line count [1]
- (Table{Int}) Statistics - Cycles, line count, activity [1]
- (Int) Agent count - How many EXAs you start the solution with

[1] For campaign levels, set the auxiliary statistics value to 0, and populate the statistics table. For battle and sandbox levels, set the auxiliary statistics value to the solution's line count, and insert but **do not** populate the statistics table.

## Agent data
- (Byte) Unknown purpose - Most likely used as line return, always `0A`
- (String) Agent / EXA name
- (String) Agent / EXA code [1]
- (Byte) Editor view mode
  - 0 = Default view, maximized
  - 1 = Minimized, don't show code or registers
  - 2 = Follow current instruction
- (Boolean) Is M-bus local by default?
- (Boolean[]) Default sprite [2]

[1] The text here is exactly the same as what you see in the game's editor, encoded using Unix line endings, `\n - 0x0A`

[2] Array of 100 Booleans, read left to right, then top to bottom - True = White pixel, False = Black pixel. This data is also present on campaign and battle levels, even though those levels lack the mechanics necessary for such sprite data to be of any use.

# Outro
That's what we've discoverd about the TEC disks format so far, use it for good please ;)
