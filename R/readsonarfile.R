# read a Tritech sonar data file and plot a frame in R
# install.packages('rJava')
library(rJava)
library(plot3D)

# initialise Java and add the jar library
.jinit()
jarFile <- 'C:\\Users\\dg50\\source\\repos\\TritechFiles\\TritechFilesV0102.jar';
.jaddClassPath(jarFile) 

sampleFile <- 'C:/ProjectData/Meygen2022/AAM/GLFData/Drive23/11-15/log_2022-11-15-181117.glf';
sampleFolder <- 'C:/ProjectData/Meygen2022/AAM/GLFData/Drive23/11-15/';
# Create the multifile catalog for the single file, or folder. 
mfc <- .jnew("tritechgemini.fileio.MultiFileCatalog")
# print out all the methods in the catalog class to see they are there 
.jmethods(mfc) 
# catalog a single file, or folder of files. 
b <- J(mfc, "catalogFiles", sampleFolder)
nRec <- J(mfc, "getTotalRecords")
print(paste('total number of records is', nRec))

# now create the FanPicksFromData object used to transform images.
fanMaker <- .jnew("tritechgemini.imagedata.FanPicksFromData", as.integer(4))

# read in the first record.If looping through records, you'd start your loop here 
aRec <- J(mfc, "getRecord", as.integer(0))
# get the raw data out
raw <- J(aRec, "getShortImageData")
nBeam <- J(aRec, "getnBeam")
nRange <- J(aRec, "getnRange")
# data come in as a 1D array. Reshape it to 2D. 
raw2 <- array(raw, dim=c(nBeam, nRange))
plot.new()
image2D(raw2, main="Raw image", xlab="Beam", ylab="Range")

# now make a fan image from the record we read in to be 500 pixels wide (quite small)
fanImage <- J(fanMaker, "createFanData", aRec, as.integer(500))
imageData <- J(fanImage, "getImageValues")
# now free the image data in the record, or you'll run out of memory
# after a few thousand records.
J(aRec, "freeImageData")
# imageData comes back as a 2D array of Java objects and needs to be converted
# This works as found at https://stackoverflow.com/questions/17556910/accessing-data-from-java-object-in-rjava
rImage <- do.call(rbind, lapply(imageData, .jevalArray))
# get the image dimensions
imageDim = dim(rImage);
# create the scales, should be the same in x and y
xScale <- J(fanImage, "getMetresPerPixX")
xs = (seq(1, imageDim[1])-imageDim[1]/2) *xScale
ys = seq(1, imageDim[2]) * xScale;
# plot it
plot.new()
image2D(rImage, x = xs, y = ys, main="Fan Image", xlab="(m)", ylab="(m)")
