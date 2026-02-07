# TritechFiles
Tritech image data handling and file i/o 

This repo contains classes which can unpack [Tritech Gemini](https://www.tritech.co.uk/products/gemini)
ECD and GLF files, and also [ARIS sonar](http://www.soundmetrics.com/products/aris-sonars) data files.

[Javadoc](https://douggillespie.github.io/TritechFiles/index.html)

The package can be exported as a Java library and the functions for reading files and converting to 
fan images called from [Matlab](https://github.com/douggillespie/TritechFiles/tree/main/matlab),
[R](https://github.com/douggillespie/TritechFiles/tree/main/R), 
and [Python](https://github.com/douggillespie/TritechFiles/tree/main/python).

The code makes catalogues of the files which are stored in index files alongside the original data 
files. In addition, the code can read glf files, which are a zipped archive of 2 or 3 othr files, without
unzipping. This allows rapid random access to any record in any file. The first time a folder of files is 
accessed, it may take a while to make the catalogues, but after that, the stored catalogues are rapidly 
loaded. 

Note that these functions are written in pure Java. Tritech do make a C++ SDK available which 
may be more stable and can handle the full file format. What I've done here is a bit of a 'hack',
may only work with the files I have collected, and may not be at all future proof.

Motivation is primarily to be able to efficiently read data into PAMGuard (www.pamguard.org) for 
use in the PAMGuard sonar processing modules in [TritechAcquisitionPlugin](https://github.com/douggillespie/TritechAcquisitionPlugin). 

Also included are classes which can take the rectangular data frames from sonar images and render 
them into a fan shaped matrix of data for display. It is quite likely that this does NOT use the
same transformations going from rectangle to fan as the Tritech SeaTec and Gemini software use.   
