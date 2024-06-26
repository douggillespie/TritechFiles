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
	 * Get the decompressed image data in an array of shorts
	 * this allows correction of problems caused by Java not understanding
	 * unsigned values, so what should be large values (>=128) are negative. 
	 * <br>This will return an array of values between 0 and 255
	 * @return Decompressed raw data in short format. 
	 */
	public short[] getShortImageData() {
		byte[] byteData = getImageData();
		if (byteData == null) {
			return null;
		}
		int n = byteData.length;
		short[] shortData = new short[n];
		for (int i = 0; i < n; i++) {
			shortData[i] = (short) Byte.toUnsignedInt(byteData[i]);
		}
		return shortData;
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
	public void freeImageData() {
		imageData = null;
//		bearingTable = null;
		isFullyRead = false;
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

	@Override
	public int getBearingIndex(double bearing) {
		if (bearingTable == null) {
			return -1;
		}
		double dist = 1000;
		int bestInd = -1;
		for (int i = 0; i < bearingTable.length; i++) {
			if (Math.abs(bearing-bearingTable[i]) < dist) {
				dist = Math.abs(bearing-bearingTable[i]);
				bestInd = i;
			}
		}
		return bestInd;
	}

	@Override
	public int getRangeIndex(double range) {
		int nR = getnRange();
		double mR = getMaxRange();
		int rangeInd = (int) Math.round(range/mR*nR);
		rangeInd = Math.max(0,  rangeInd);
		rangeInd = Math.min(nR-1, rangeInd);
		return rangeInd;
	}

}
