package tritechgemini.imagedata;

/**
 * Interface for Gemini data record which can support data read from ECD or GLF files. 
 * @author dg50
 *
 */
public interface GeminiRecordI {

	/**
	 * 
	 * @return Image time in milliseconds UTC. 
	 */
	public long getRecordTime();
	
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
