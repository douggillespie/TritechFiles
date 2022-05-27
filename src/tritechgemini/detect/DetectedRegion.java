package tritechgemini.detect;

import java.util.ArrayList;
import java.util.Collections;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * A connected region of points found by the detector. 
 * Point data consist of a list of index pointers back into the original record. 
 * @author dg50
 *
 */
public class DetectedRegion extends RegionDetector {
	
	private GeminiImageRecordI geminiRecord;
	
	private ArrayList<Integer> pointIndexes;
	
	/*
	 * Bins are only used when detecting. Only the abs values are stored in database. 
	 */
	private int minBearingBin, maxBearingBin;
	
	private int minRangeBin, maxRangeBin;
	
	private double minBearing, maxBearing;
	
	private double minRange, maxRange;

	private int totalValue;
	
	private int maxValue;
	
	private int nPoints;
	
	private int sonarId;

	/**
	 * During detection
	 * @param geminiRecord
	 * @param pointIndex
	 */
	public DetectedRegion(GeminiImageRecordI geminiRecord, int pointIndex) {
		this.geminiRecord = geminiRecord;
		pointIndexes = new ArrayList<Integer>();
		pointIndexes.add(pointIndex);
		sonarId = geminiRecord.getDeviceId();
	}
	
	/**
	 * With viewer
	 * @param sonarId
	 * @param minB
	 * @param maxB
	 * @param minR
	 * @param maxR
	 * @param meanV
	 * @param totV
	 * @param maxV
	 */
	public DetectedRegion(int sonarId, double minB, double maxB, double minR, double maxR, int meanV, int totV,
			int maxV) {
		this.sonarId = sonarId;
		this.minBearing = minB;
		this.maxBearing = maxB;
		this.minRange = minR;
		this.maxRange = maxR;
		this.totalValue = totV;
		this.maxValue = maxV;
		this.nPoints = Math.round(totalValue/meanV);
	}

	/**
	 * Add a point to the growing region 
	 * @param pointIndex
	 */
	public void addPoint(int pointIndex) {
		pointIndexes.add(pointIndex);
	}

	/**
	 * Called when the image stops growing to measure a few parameters 
	 * about the image, such as min and max bearings and ranges. 
	 */
	public void completeRegion() {
		Collections.sort(pointIndexes);
		byte[] data = geminiRecord.getImageData();
		int nBearing = geminiRecord.getnBeam();
		int nRange = geminiRecord.getnRange();
		minBearingBin = nBearing-1;
		minRangeBin = nRange;
		maxBearingBin = 0;
		maxRangeBin = 0;
		totalValue = maxValue = 0;
		for (int i = 0; i < pointIndexes.size(); i++) {
			int point = pointIndexes.get(i); 
			int val = Byte.toUnsignedInt(data[point]);
			totalValue += val;
			maxValue = Math.max(maxValue, val);
			int bearingBin = point % nBearing;
			int rangeBin = point / nBearing;
			minBearingBin = Math.min(minBearingBin, bearingBin);
			maxBearingBin = Math.max(maxBearingBin, bearingBin);
			minRangeBin = Math.min(minRangeBin, rangeBin);
			maxRangeBin = Math.max(maxRangeBin, rangeBin);
		}
		nPoints = pointIndexes.size();
		double[] bearingTable = geminiRecord.getBearingTable();
		minBearing = bearingTable[minBearingBin];
		maxBearing = bearingTable[maxBearingBin];
		minRange = geminiRecord.getMaxRange() * (double) minRangeBin / (double) geminiRecord.getnRange();
		maxRange = geminiRecord.getMaxRange() * (double) maxRangeBin / (double) geminiRecord.getnRange();
	}

	/**
	 * @return the geminiRecord
	 */
	public GeminiImageRecordI getGeminiRecord() {
		return geminiRecord;
	}

	/**
	 * @return the pointIndexes
	 */
	public ArrayList<Integer> getPointIndexes() {
		return pointIndexes;
	}

	/**
	 * @return the minimum bearing in radians
	 */
	public double getMinBearing() {
		return minBearing;
	}

	/**
	 * @return the maximum bearing in radians
	 */
	public double getMaxBearing() {
		return maxBearing;
	}

	/**
	 * @return the minimum range in metres
	 */
	public double getMinRange() {
		return minRange;
	}

	/**
	 * @return the maximum range in metres
	 */
	public double getMaxRange() {
		return maxRange;
	}

	/**
	 * The sum of all pixes in the detected region
	 * @return the totalValue
	 */
	public int getTotalValue() {
		return totalValue;
	}
	
	/**
	 * Average intensity in the region. 
	 * @return
	 */
	public int getAverageValue() {
		return totalValue / pointIndexes.size();
	}
	
	/**
	 * Get the total number of pixels making up the region. 
	 * @return
	 */
	public int getRegionSize() {
		return pointIndexes.size();
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}
}
