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
	 * Minimum range bin (probably always 0)
	 */
	public int minRangeBin;
	
	/**
	 * Maximum range bin (actually one more than the index of the bin)
	 * so that nBins = maxBin-minBin
	 */
	public int maxRangeBin;
	
	/**
	 * minimum beam bin (probably always 0);
	 */
	public int minBeamBin;
	
	/**
	 * Maximum beam bin (generally bearingTable.leength)
	 */
	public int maxBeamBin;
	
	/**
	 * Full file path for this record
	 */
	private String filePath;
	
	/**
	 * Speed of sound
	 */
	public double speedOfSound;
	
	@Override
	public long getRecordTime() {
		return recordTimeMillis;
	}

	/**
	 * 
	 * @return the number of beams
	 */
	public int getNBeam() {
		return bearingTable == null ? 0 : bearingTable.length;
	}
	
	/**
	 * 
	 * @return the number of range bins.
	 */
	@Override
	public int getnRange() {
		return maxRangeBin-minRangeBin;
	}

	@Override
	public double[] getBearingTable() {
		return bearingTable;
	}

	@Override
	public int getnBeam() {
		return maxBeamBin-minBeamBin;
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
	public double getSoS() {
		return speedOfSound;
	}

	@Override
	public boolean isFullyLoaded() {
		return isFullyRead;
	}

}
