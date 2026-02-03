package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.echogram.EchoLineStore;

public interface GeminiImageRecordI extends GeminiRecordI, Cloneable, Serializable {

	/**
	 * Get decompressed image data. 
	 * @return decompressed image data in a single array
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
	 * @return Record number in file
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
	public GeminiImageRecordI clone();

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
