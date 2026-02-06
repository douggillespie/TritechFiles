package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.echogram.EchoLineStore;

/**
 * Base interface for sonar image records from all manufacturers. 
 * <br>All manufacturers so far have the same basic data in each image which 
 * consists of a bearing table, and a big array of amplitudes vs bearing and range. 
 * <br>Tritech and ARIS both use one byte unsigned data. To keep memory usage low, 
 * this is read in as a Java byte array. Note that Java does not do unsigned bytes, 
 * so large amplitude values come out negative. Users should therefore use the 
 * getShortImageData() function to access the image as int16 data. 
 * <br>The byte[] getImageData() function remains public, but if future support
 * for other manufactures, which perhaps have >1 byte data requires it, this will 
 * have to go private, or even disappear, leaving only the generic short[] function. 
 */
public interface SonarImageRecordI extends SonarRecordI, Cloneable, Serializable {

	/**
	 * Get image data array. This is a one dimensional array of all data, that 
	 * goes across bearings, then range. Needs reshaping to create a 2D array.
	 * <br>Be very careful if you use the output of this function since data will
	 * be unsigned byte, so you need to use the Byte.toUnsignedInt(byte x) function
	 * to get an unsigned value. For most purposes, you're better off getting 
	 * the short[] array, which will have already been converted.  
	 * @return  image data in a single array
	 */
	public byte[] getImageData();
	
	/**
	 * Set the image data.
	 * @param imageDataMust be an array of getnBeam() x getnRange()
	 */
	public void setImageData(byte[] imageData);
	
	/**
	 * Get the decompressed image data in an array of shorts
	 * this allows correction of problems caused by Java not understanding
	 * unsigned values, so what should be large values (>=128) are negative. 
	 * <br>This will return an array of values between 0 and 255
	 * @return Decompressed raw data in short format. 
	 */
	public short[] getShortImageData();
	
	/**
	 * 
	 * @return List of beam angles in radians. 
	 */
	public double[] getBearingTable();
	
	/**
	 * 
	 * @return The number of range bins
	 */
	public int getnRange();
	
	/**
	 * 
	 * @return The maximum range for this frame in metres. 
	 */
	public double getMaxRange();
	
	/**
	 * 
	 * @return the total number of beams
	 */
	public int getnBeam();
	
	/**
	 * Path to the image file
	 * @return path name of file
	 */
	public String getFilePath();
	
	/**
	 * 
	 * @return Record number in file (zero indexed)
	 */
	public int getRecordNumber();
	
	/**
	 * Set the record number
	 * @param recordNumber
	 */
	public void setRecordNumber(int recordNumber);
	
	
	/**
	 * Generic type of sonar<br>
	 * Imager = 0<br>
	 * Profiler = 1<br>
	 * @return The type of sonar
	 */
	public int getSonarType();
	
	/**
	 * Get the speed of sound in m/s
	 * @return speed of sound
	 */
	public double getSoS();
	
	/**
	 * Get if chirp was on 0 off, 1 on, -1 unknown
	 * @return true if chirp mode
	 */
	public int getChirp();
	
	/**
	 * 
	 * @return the gain for that frame
	 */
	public int getGain();
	
	/**
	 * Is record fully loaded ?
	 * @return true if record is fully loaded. 
	 */
	public boolean isFullyLoaded();

	/**
	 * Free the data to save memory. 
	 */
	public void freeImageData();

	/**
	 * Record how long it took to load the record in nanoseconds. 
	 * @param load time in nanoseconds. 
	 */
	public void setLoadTime(long nanos);
	
	/**
	 * Get the record load time in nanoseconds. 
	 * @return
	 */
	public long getLoadTime();

	/**
	 * Clone in interface which is properly overridden in inherited classes
	 * @return cloned record. 
	 */
	public SonarImageRecordI clone();

	/**
	 * Get the index from the bearing table of the bearing closest 
	 * to the given bearing. 
	 * @param bearing in radians
	 * @return index of closest bearing. 
	 */
	public int getBearingIndex(double bearing);

	
	/**
	 * Get the index of the range bin closest to the given range
	 * @param range range in metres
	 * @return index of closest range bin. 
	 */
	public int getRangeIndex(double range);
	
	/**
	 * Get storage for echogram lines. 
	 * @return
	 */
	public EchoLineStore getEchoLineStore();
	
}
