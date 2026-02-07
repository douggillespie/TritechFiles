# TritechFiles
Tritech image data handling and file i/o 

This repo contains classes which can unpack Tritech Gemini ECD and GLF files, and also ARIS data files.

[Javadoc](https://douggillespie.github.io/TritechFiles/index.html)

The package can be exported as a Java library and the functions for reading files and converting to 
fan images called from [Matlab](https://github.com/douggillespie/TritechFiles/tree/main/matlab) 
and [Python](https://github.com/douggillespie/TritechFiles/tree/main/python).

Note that these functions are written in pure Java. Tritech do make a C++ SDK available which 
may be more stable and can handle the full file format. What I've done here is a bit of a 'hack',
may only work with the files I have collected, and may not be at all future proof.

Motivation is primarily to be able to efficiently read data into PAMGuard (www.pamguard.org) for 
display. This package is deliberately kept separate from all PAMGuard structures so that the classes
can easily be called from other languages, e.g. Matlab. 

Also included are classes which can take the rectangular data frames from sonar images and render 
them into a fan shaped matrix of data for display. It is quite likely that this does NOT use the
same transformations going from rectangle to fan as the Tritech SeaTec and Gemini software use.   
