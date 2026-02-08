# TritechFiles

Tritech image data handling and file i/o

This repo contains classes which can unpack [Tritech
Gemini](https://www.tritech.co.uk/products/gemini) ECD and GLF files,
and also [ARIS sonar](http://www.soundmetrics.com/products/aris-sonars)
data files.

[Javadoc](https://douggillespie.github.io/TritechFiles/index.html)

Motivation is primarily to be able to efficiently read data into
PAMGuard (www.pamguard.org) for use in the PAMGuard sonar processing
modules in
[TritechAcquisitionPlugin](https://github.com/douggillespie/TritechAcquisitionPlugin).

The package can be exported as a Java library and the functions for
reading files and converting to fan images called from
[Matlab](https://github.com/douggillespie/TritechFiles/tree/main/matlab),
[R](https://github.com/douggillespie/TritechFiles/tree/main/R), and
[Python](https://github.com/douggillespie/TritechFiles/tree/main/python).
A built jar file should be available in the latest release.

The code makes catalogues of the files which are stored in index files
alongside the original data files. This allows random access to
individual records in each file for rapid data browsing. In addition,
the code can read glf files, which are a zipped archive of 2 or 3 other
files, without unzipping. Removing the need to unzip each file before it
is used, again providing very significant speed improvements (and disk
space savings) when browsing large datasets. The first time a folder of
files is accessed, it may take a while to make the catalogues, but after
that, the stored catalogues are rapidly loaded.

Note that these functions are written in pure Java. The code can handle
all data we've found so far in Tritech and ARIS sonar data files, but
may not handle all data types, for instance if you have Tritech Genesis
software reading and recording data from additional instruments. If so,
you will have to add to the appropriate file reader, or use the [Tritech
C++ SDK](https://www.tritech.co.uk/support/software/sdk) which should
handle the full file format.

Also included are classes which can take the rectangular data frames
from sonar images and render them into a fan shaped matrix of data for
display. It is quite likely that this does not use the same
transformations going from rectangle to fan as the Tritech SeaTec and
Gemini software use.
