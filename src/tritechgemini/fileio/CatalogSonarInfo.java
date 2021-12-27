package tritechgemini.fileio;

import java.io.Serializable;

/**
 * information about an individual sonar in a catalog. 
 * i.e. one of thee for single sonar, 2 for dual, etc. 
 * @author dg50
 *
 */
public class CatalogSonarInfo implements Cloneable, Serializable {

	public static final long serialVersionUID = 1L;

	private int sonarId;
	
	private int sonarIndex;
	
	private int nFrames;

	private int firstFrame;

	/**
	 * 
	 * @param sonarIndex index of sonar (likely to be 1 or 2, not 0 based)
	 * @param sonarId Sonar id - the id on the actual device. 
	 * @param firstFrame index of first frame in file. Should be 0 or 1. 
	 */
	public CatalogSonarInfo(int sonarIndex, int sonarId, int firstFrame) {
		super();
		this.sonarIndex = sonarIndex;
		this.sonarId = sonarId;
		this.firstFrame = firstFrame;
	}

	public int getSonarId() {
		return sonarId;
	}

	public int getSonarIndex() {
		return sonarIndex;
	}

	public int getnFrames() {
		return nFrames;
	}

	/**
	 * Increase frame count by one
	 * @return total number of frames. 
	 */
	public int addFrame() {
		return ++nFrames;
	}
	
	/**
	 * Add a countof n frames to the total. 
	 * @param nToAdd
	 * @return
	 */
	public int addFrameCount(int nToAdd) {
		nFrames += nToAdd;
		return nFrames;
	}

	@Override
	protected CatalogSonarInfo clone() {
		try {
			return (CatalogSonarInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return String.format("Sonar index %d No. %d with %d records starting index %d", sonarIndex, sonarId, nFrames, firstFrame);
	}
	
}
