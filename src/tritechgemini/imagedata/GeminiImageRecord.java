package tritechgemini.imagedata;

abstract public class GeminiImageRecord implements GeminiImageRecordI {

	
	public GeminiImageRecord(String filePath, int filePos, int recordIndex) {
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
	 */
	public long recordTimeMillis;
	
	/**
	 * Index in file
	 */
	public int recordIndex;
	
	/**
	 * Bearing table
	 */
	public double[] bearingTable;
	
	/**
	 * Full file path for this record
	 */
	private String filePath;

	private long recordLoadNanos;
	

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

}
