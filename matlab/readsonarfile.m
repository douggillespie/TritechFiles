% Example of reading a Tritech or ARIS sonar file in Matlab using the
% TritechFiles Java library from https://github.com/douggillespie/TritechFiles
% see the Javadoc at
% file:///C:/Users/dg50/source/repos/TritechFiles/docs/index.html for the
% API and function call details. The main classes you'll be wanting to use
% to read data are MultiFileCatalog to efficiently get access to all
% records in a file or folder of files, and SonarImageRecord and 
% it's device specific subclasses. 
% FanPicksFromData can be used to convert raw data to a fan image. 

% find the jar file, check it's path and add full path to Matlab java path
jarFile = 'C:\\Users\\dg50\\source\\repos\\TritechFiles\\TritechFilesV0102.jar';
% jarFile = which(jarFileName)
% not very important, but will get a waring if the jar is loaded a second time
warning('off') 
javaaddpath(jarFile);

% the MultiFileCatalog can work with a single file, or a folder of files.
% For this example, we'll just use a single file
sampleFile = "C:\ProjectData\Meygen2022\AAM\GLFData\Drive23\11-15\log_2022-11-15-181117.glf";

mfc = tritechgemini.fileio.MultiFileCatalog;
mfc.catalogFiles(sampleFile);

% see which sonars are present in the data
sonars = mfc.getSonarIDs;
% Make a fan maker for each sonar. It's not good to share them since they
% contain a lookup table that has to be recreated whenever the data
% dimension changes
for i = 1:numel(sonars)
    fanMaker{i} = tritechgemini.imagedata.FanPicksFromData(4);
end
% loop through the fist 6 records, plotting each
figure(1)
clf
figure(2)
clf
for i = 1:6
    aRec = mfc.getRecord(i-1);
    % get the rectangular image data.
    raw = aRec.getShortImageData; 
    % that comes in as a 1D array, so reshape and transpose
    raw = reshape(raw, aRec.getnBeam, aRec.getnRange)';
    figure(1)
    subplot(3,2,i)
    imagesc(raw)
    axis xy
    xlabel('Beam');
    ylabel('Range')

    for f = 1:numel(sonars) % find the fan maker
        if aRec.getDeviceId() == sonars(f)
            fm = fanMaker{f};
        end
    end
    % make the fan image
    fanImage = fm.createFanData(aRec, 1000);
    % get the values
    image = fanImage.getImageValues;
    image = image';  % tranpose
    % get the scales (should be the same)
    xS = fanImage.getMetresPerPixX;
    yS = fanImage.getMetresPerPixY;
    % make scales in meters to match the data. 
    xBins = ([1:size(image,2)]-size(image,2)/2)*xS;
    yBins = [1:size(image,1)]*yS;
    % plot the image and flip to correct axis
    figure(2);
    subplot(3,2,i)
    imagesc(xBins, yBins,image)
    axis xy
    axis equal
    xlabel('Metres');
    ylabel('(m)')
end
