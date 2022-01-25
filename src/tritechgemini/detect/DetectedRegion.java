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
	
	private int minBearingBin, maxBearingBin;
	
	private int minRangeBin, maxRangeBin;

	private int totalValue;
	
	private int maxValue;

	public DetectedRegion(GeminiImageRecordI geminiRecord, int pointIndex) {
		this.geminiRecord = geminiRecord;
		pointIndexes = new ArrayList<Integer>();
		pointIndexes.add(pointIndex);
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
		maxBearingBin = maxRangeBin = 0;
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
	 * @return the minBearingBin
	 */
	public int getMinBearingBin() {
		return minBearingBin;
	}

	/**
	 * @return the maxBearingBin
	 */
	public int getMaxBearingBin() {
		return maxBearingBin;
	}

	/**
	 * @return the minimum bearing in radians
	 */
	public double getMinBearing() {
		return geminiRecord.getBearingTable()[minBearingBin];
	}

	/**
	 * @return the maximum bearing in radians
	 */
	public double getMaxBearing() {
		return geminiRecord.getBearingTable()[maxBearingBin];
	}

	/**
	 * @return the minRangeBin
	 */
	public int getMinRangeBin() {
		return minRangeBin;
	}

	/**
	 * @return the maxRangeBin
	 */
	public int getMaxRangeBin() {
		return maxRangeBin;
	}

	/**
	 * @return the minimum range in metres
	 */
	public double getMinRange() {
		return geminiRecord.getMaxRange() * (double) minRangeBin / (double) geminiRecord.getnRange();
	}

	/**
	 * @return the maximum range in metres
	 */
	public double getMaxRange() {
		return geminiRecord.getMaxRange() * (double) maxRangeBin / (double) geminiRecord.getnRange();
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
}
