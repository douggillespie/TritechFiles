package tritechgemini.imagedata;

import java.io.Serializable;

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
	
}
