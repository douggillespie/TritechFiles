package tritechgemini.imagedata;

public interface GeminiImageRecordI extends GeminiRecordI {

	/**
	 * Get decompressed image data. 
	 * @return decompressed image data in a single array
	 */
	public byte[] getImageData();
	
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
	
}
