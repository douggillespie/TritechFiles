package aris;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;

/**
 * ARIS File Header. 
 * See https://github.com/SoundMetrics/aris-file-sdk/blob/master/type-definitions/C/FileHeader.h
 * @author dg50
 *
 */
public class ARISFileHeader extends ARISHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int DATASIZE = 1024;
	
	// File format version DDF_05 = 0x05464444
    private int version;

    // Total frames in file
    // Note: Writers should populate; readers should calculate the number of frames from file size & beams*samples.
    private int frameCount;

    // Initial recorded frame rate
    // OBSOLETE: See frame header instead.
    private int frameRate;

    // Non-zero if HF, zero if LF
    // OBSOLETE: See frame header instead.
    private int highResolution;

    // ARIS 3000 = 128/64, ARIS 1800 = 96/48, ARIS 1200 = 48
    // Note: Writers should populate; readers should see frame header instead.
    private int numRawBeams;

    // 1/Sample Period
    // OBSOLETE: See frame header instead.
    float sampleRate;

    // Number of range samples in each beam
    // Note: Writers should populate; readers should see frame header instead.
    private int samplesPerChannel;

    // Relative gain in dB:  0 - 40
    // OBSOLETE: See frame header instead.
    private int receiverGain;

    // Image window start range in meters (code [0..31] in DIDSON)
    // OBSOLETE: See frame header instead.
    float windowStart;

    // Image window length in meters  (code [0..3] in DIDSON)
    // OBSOLETE: See frame header instead.
    float windowLength;

    // Non-zero = lens down (DIDSON) or lens up (ARIS), zero = opposite
    // OBSOLETE: See frame header instead.
    private int reverse;

    // Sonar serial number
    private int SN;

    // Date that file was recorded
    private static final int DATELENGTH = 32;
    private String strDate;

    // User input to identify file in 256 characters
    private static final int HEADIDLEN = 256;
    private String strHeaderID;

    // User-defined integer quantity
    private int userID1;

    // User-defined integer quantity
    private int userID2;

    // User-defined integer quantity
    private int userID3;

    // User-defined integer quantity
    private int userID4;

    // First frame number from source file (for DIDSON snippet files)
    private int startFrame;

    // Last frame number from source file (for DIDSON snippet files)
    private int endFrame;

    // Non-zero indicates time lapse recording
    private int timeLapse;

    // Number of frames/seconds between recorded frames
    private int recordInterval;

    // Frames or seconds interval
    private int radioSeconds;

    // Record every Nth frame
    private int frameInterval;

    // See DDF_04 file format document
    // OBSOLETE: Obsolete.
    private int flags;

    // See DDF_04 file format document
    private int auxFlags;

    // Sound velocity in water
    // OBSOLETE: See frame header instead.
    private int sspd;

    // See DDF_04 file format document
    private int flags3D;

    // DIDSON software version that recorded the file
    private int softwareVersion;

    // Water temperature code:  0 = 5-15C, 1 = 15-25C, 2 = 25-35C
    private int waterTemp;

    // Salinity code:  0 = fresh, 1 = brackish, 2 = salt
    private int salinity;

    // Added for ARIS but not used
    private int pulseLength;

    // Added for ARIS but not used
    private int txMode;

    // Reserved for future use
    private int versionFGPA;

    // Reserved for future use
    private int versionPSuC;

    // Frame index of frame used for thumbnail image of file
    private int thumbnailFI;

    // Total file size in bytes
    // OBSOLETE: Do not use; query your filesystem instead.
    private long fileSize;

    // Reserved for future use
    // OBSOLETE: Obsolete; not used.
    private long optionalHeaderSize;

    // Reserved for future use
    // OBSOLETE: Obsolete; not used.
    private long optionalTailSize;

    // DIDSON_ADJUSTED_VERSION_MINOR
    // OBSOLETE: Obsolete.
    private int versionMinor;

    // Non-zero if telephoto lens (large lens, hi-res lens, big lens) is present
    // OBSOLETE: See frame header instead.
    private int largeLens;

	private String fileName;

    // Padding to fill out to 1024 bytes
    public static final int PADDING = 568;
	
	public ARISFileHeader(String fileName) {
		this.fileName = fileName;
	}
	
	public void readHeader(DataInput dis) throws IOException {
		version = dis.readInt();
		frameCount = dis.readInt();
		frameRate = dis.readInt();
		highResolution = dis.readInt();
		numRawBeams = dis.readInt();
		sampleRate = dis.readFloat();
		samplesPerChannel = dis.readInt();
		receiverGain = dis.readInt();
		windowStart = dis.readFloat();
		windowLength = dis.readFloat();
		reverse = dis.readInt();
		SN = dis.readInt();
		strDate = readString(dis, DATELENGTH);
		strHeaderID = readString(dis, HEADIDLEN);
		userID1 = dis.readInt();
		userID2 = dis.readInt();
		userID3 = dis.readInt();
		userID4 = dis.readInt();
		startFrame = dis.readInt();
		endFrame = dis.readInt();
		timeLapse = dis.readInt();
		recordInterval = dis.readInt();
		radioSeconds = dis.readInt();
		frameInterval = dis.readInt();
		flags = dis.readInt();
		auxFlags = dis.readInt();
		sspd = dis.readInt();
		flags3D = dis.readInt();
		softwareVersion = dis.readInt();
		waterTemp = dis.readInt();
		salinity = dis.readInt();
		pulseLength = dis.readInt();
		txMode = dis.readInt();
		versionFGPA = dis.readInt();
		versionPSuC = dis.readInt();
		thumbnailFI = dis.readInt();
		fileSize = dis.readLong();
		optionalHeaderSize = dis.readLong();
		optionalTailSize = dis.readLong();
		versionMinor = dis.readInt();
		largeLens = dis.readInt();
		dis.skipBytes(PADDING);
	}

	/**
	 * @return the datasize
	 */
	public static int getDatasize() {
		return DATASIZE;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the frameCount
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * @return the frameRate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @return the highResolution
	 */
	public int getHighResolution() {
		return highResolution;
	}

	/**
	 * @return the numRawBeams
	 */
	public int getNumRawBeams() {
		return numRawBeams;
	}

	/**
	 * @return the sampleRate
	 */
	public float getSampleRate() {
		return sampleRate;
	}

	/**
	 * @return the samplesPerChannel
	 */
	public int getSamplesPerChannel() {
		return samplesPerChannel;
	}

	/**
	 * @return the receiverGain
	 */
	public int getReceiverGain() {
		return receiverGain;
	}

	/**
	 * @return the windowStart
	 */
	public float getWindowStart() {
		return windowStart;
	}

	/**
	 * @return the windowLength
	 */
	public float getWindowLength() {
		return windowLength;
	}

	/**
	 * @return the reverse
	 */
	public int getReverse() {
		return reverse;
	}

	/**
	 * @return the sN
	 */
	public int getSN() {
		return SN;
	}

	/**
	 * @return the datelength
	 */
	public static int getDatelength() {
		return DATELENGTH;
	}

	/**
	 * @return the strDate
	 */
	public String getStrDate() {
		return strDate;
	}

	/**
	 * @return the headidlen
	 */
	public static int getHeadidlen() {
		return HEADIDLEN;
	}

	/**
	 * @return the strHeaderID
	 */
	public String getStrHeaderID() {
		return strHeaderID;
	}

	/**
	 * @return the userID1
	 */
	public int getUserID1() {
		return userID1;
	}

	/**
	 * @return the userID2
	 */
	public int getUserID2() {
		return userID2;
	}

	/**
	 * @return the userID3
	 */
	public int getUserID3() {
		return userID3;
	}

	/**
	 * @return the userID4
	 */
	public int getUserID4() {
		return userID4;
	}

	/**
	 * @return the startFrame
	 */
	public int getStartFrame() {
		return startFrame;
	}

	/**
	 * @return the endFrame
	 */
	public int getEndFrame() {
		return endFrame;
	}

	/**
	 * @return the timeLapse
	 */
	public int getTimeLapse() {
		return timeLapse;
	}

	/**
	 * @return the recordInterval
	 */
	public int getRecordInterval() {
		return recordInterval;
	}

	/**
	 * @return the radioSeconds
	 */
	public int getRadioSeconds() {
		return radioSeconds;
	}

	/**
	 * @return the frameInterval
	 */
	public int getFrameInterval() {
		return frameInterval;
	}

	/**
	 * @return the flags
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @return the auxFlags
	 */
	public int getAuxFlags() {
		return auxFlags;
	}

	/**
	 * @return the sspd
	 */
	public int getSspd() {
		return sspd;
	}

	/**
	 * @return the flags3D
	 */
	public int getFlags3D() {
		return flags3D;
	}

	/**
	 * @return the softwareVersion
	 */
	public int getSoftwareVersion() {
		return softwareVersion;
	}

	/**
	 * @return the waterTemp
	 */
	public int getWaterTemp() {
		return waterTemp;
	}

	/**
	 * @return the salinity
	 */
	public int getSalinity() {
		return salinity;
	}

	/**
	 * @return the pulseLength
	 */
	public int getPulseLength() {
		return pulseLength;
	}

	/**
	 * @return the txMode
	 */
	public int getTxMode() {
		return txMode;
	}

	/**
	 * @return the versionFGPA
	 */
	public int getVersionFGPA() {
		return versionFGPA;
	}

	/**
	 * @return the versionPSuC
	 */
	public int getVersionPSuC() {
		return versionPSuC;
	}

	/**
	 * @return the thumbnailFI
	 */
	public int getThumbnailFI() {
		return thumbnailFI;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return the optionalHeaderSize
	 */
	public long getOptionalHeaderSize() {
		return optionalHeaderSize;
	}

	/**
	 * @return the optionalTailSize
	 */
	public long getOptionalTailSize() {
		return optionalTailSize;
	}

	/**
	 * @return the versionMinor
	 */
	public int getVersionMinor() {
		return versionMinor;
	}

	/**
	 * @return the largeLens
	 */
	public int getLargeLens() {
		return largeLens;
	}

	/**
	 * @return the padding
	 */
	public static int getPadding() {
		return PADDING;
	}

	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
