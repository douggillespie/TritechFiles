package tritechgemini.imagedata;

/**
 * Interface for Gemini data record which can support data read from ECD or GLF files. 
 * @author dg50
 *
 */
public interface GeminiRecord {

	/**
	 * 
	 * @return Image time in milliseconds UTC. 
	 */
	public long getImageTime();
	
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
	 * Specific type of sonar<br>
	 * 720is=1<br>
	 * 720ik=2<br>
	 * 720im=3<br>
	 * 1200ik=4<br>
	 * @return type of sonar
	 */
	public int getSonarPlatform();
	
	/**
	 * 
	 * @return the index (0 if only one sonar, 0,1,etc for multiple sonars)
	 */
	public int getSonarIndex();
	
	/**
	 * 
	 * @return the sonar unique id,
	 */
	public int getDeviceId();
}
