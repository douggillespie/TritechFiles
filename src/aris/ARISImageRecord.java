package aris;

import java.util.Arrays;

import tritechgemini.echogram.EchoLineStore;
import tritechgemini.imagedata.GeminiImageRecordI;

public class ARISImageRecord implements GeminiImageRecordI {

	private static final long serialVersionUID = 1L;
	
	private ARISFileHeader fileHeader;
	private ARISFrameHeader frameHeader;
	private byte[] imageData;
	private short[] shortImageData;
	private double[] bearingTable;
	private long loadTime;
	private int extraRanges;

	private transient EchoLineStore echoLineStore;

	public ARISImageRecord(ARISFileHeader fileHeader, ARISFrameHeader frameHeader, double[] bearingTable) {
		this.fileHeader = fileHeader;
		this.frameHeader = frameHeader;
		this.bearingTable = bearingTable;
	}

	/**
	 * @return the fileHeader
	 */
	public ARISFileHeader getFileHeader() {
		return fileHeader;
	}

	/**
	 * @return the frameHeader
	 */
	public ARISFrameHeader getFrameHeader() {
		return frameHeader;
	}

	@Override
	public long getRecordTime() {
		/**
		 * ARIS time is microsecs since 1970. Same epoch, so simply
		 * divide by 1000;
		 */
		long arisT = frameHeader.getFrameTime();
		return arisT/1000;
	}

	@Override
	public int getSonarPlatform() {
		return frameHeader.getTheSystemType();
	}

	@Override
	public int getSonarIndex() {
		return 0;
	}

	@Override
	public int getDeviceId() {
		return fileHeader.getSN();
	}

	@Override
	public byte[] getImageData() {
		return imageData;
	}

	@Override
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
		shortImageData = null;
	}

	@Override
	public short[] getShortImageData() {
		if (shortImageData != null) {
			return shortImageData;
		}
		if (imageData == null) {
			return null;
		}
		shortImageData = new short[imageData.length];
		for (int i = 0; i < imageData.length; i++) {
			shortImageData[i] = (short) Byte.toUnsignedInt(imageData[i]);
		}
		return shortImageData;
	}

	@Override
	public double[] getBearingTable() {
		return bearingTable;
	}

	@Override
	public int getnRange() {
		return frameHeader.getSamplesPerBeam() + extraRanges;
	}

	@Override
	public double getMaxRange() {
		return frameHeader.getWindowLength() + frameHeader.getWindowStart();
	}

	@Override
	public int getnBeam() {
		return bearingTable.length;
	}

	@Override
	public String getFilePath() {
		return fileHeader.getFileName();
	}

	@Override
	public int getRecordNumber() {
		return frameHeader.getFrameIndex();
	}

	@Override
	public int getSonarType() {
		return frameHeader.getTheSystemType();
	}

	@Override
	public double getSoS() {
		return frameHeader.getSoundSpeed();
	}

	@Override
	public int getChirp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGain() {
		return fileHeader.getReceiverGain();
	}

	@Override
	public boolean isFullyLoaded() {
		return imageData != null;
	}

	@Override
	public void freeImageData() {
		imageData = null;
		shortImageData = null;
	}

	@Override
	public void setLoadTime(long nanos) {
		this.loadTime = nanos;
	}

	@Override
	public long getLoadTime() {
		return loadTime;
	}

	@Override
	public ARISImageRecord clone() {
//		ARISImageRecord aClone = new ARISImageRecord(fileHeader, frameHeader, bearingTable);
//		aClone.setImageData(getImageData());
		ARISImageRecord aClone = null;
		try {
			aClone = (ARISImageRecord) super.clone();
			if (imageData != null) {
				aClone.setImageData(Arrays.copyOf(imageData, imageData.length));
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aClone;
	}

	@Override
	public int getBearingIndex(double bearing) {
		for (int i = 0; i < bearingTable.length; i++) {
			if (bearingTable[i] == bearing) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getRangeIndex(double range) {
		int ind = (int) Math.round(range * getnRange() / getMaxRange());
		return ind;
	}

	public void setExtraRanges(int extras) {
		extraRanges = extras;
	}

	@Override
	public EchoLineStore getEchoLineStore() {
		if (echoLineStore == null) {
			echoLineStore = new EchoLineStore();
		}
		return echoLineStore;
	}

	@Override
	public void setRecordNumber(int recordNumber) {
		// TODO Auto-generated method stub
		
	}

}
