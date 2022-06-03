package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.fileio.GLFGenericHeader;

abstract public class GeminiImageRecord extends PublicMessageHeader implements GeminiImageRecordI, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	public GeminiImageRecord(GLFGenericHeader glfGenericHeader, String filePath, int filePos, int recordIndex) {
		super(glfGenericHeader);
		this.filePath = filePath;
		this.filePos = filePos;
		this.recordIndex = recordIndex;
	}
	
	/**
	 * Position in file
	 */
	public int filePos;
	
	/**
	 * Flag to say image has been read fully. It's quite possible that there
	 * is a file catalogue which just has the basic info (time and device id) 
	 * without the full image and bearing table data to save on memory. 
	 */
	public boolean isFullyRead;
	
	/**
	 * Millisecond time
	 * Don't store this since I only want to store the raw time. ms
	 * will need to take into account a time zone, so best apply this 
	 * at point of need, not when generating data that will be catalogued or
	 * we'll end up with values where we don't know whether or not a correction
	 * can been applied. 
	 */
//	public long recordTimeMillis;
	
	/**
	 * Index in file
	 */
	public int recordIndex;
	
	/**
	 * Bearing table
	 */
	public double[] bearingTable;

	/**
	 * Uncompressed image data. 
	 */
	protected byte[] imageData;
	
	/**
	 * Full file path for this record
	 */
	private String filePath;

	/**
	 * time taken to load the record from file in nanoseconds. 
	 */
	private long recordLoadNanos;
	

	/**
	 * Get uncompressed imageData.
	 */
	public byte[] getImageData() {
		return imageData;
	}
	
	
	/**
	 * Set the uncompressed image data. 
	 * @param imageData
	 */
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}


	/**
	 * 
	 * @return the number of beams
	 */
	public int getNBeam() {
		return bearingTable == null ? 0 : bearingTable.length;
	}
	

	@Override
	public double[] getBearingTable() {
		return bearingTable;
	}

	@Override
	public String getFilePath() {
		return filePath;
	}

	@Override
	public int getRecordNumber() {
		return recordIndex;
	}

	@Override
	public boolean isFullyLoaded() {
		return isFullyRead;
	}


	@Override
	public void setLoadTime(long nanos) {
		recordLoadNanos = nanos;
	}


	@Override
	public long getLoadTime() {
		return recordLoadNanos;
	}

	@Override
	public GeminiImageRecord clone() {
		try {
			return (GeminiImageRecord) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
