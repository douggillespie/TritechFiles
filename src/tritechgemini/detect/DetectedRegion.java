package tritechgemini.detect;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * A connected region of points found by the detector. 
 * Point data consist of a list of index pointers back into the original record. 
 * @author dg50
 *
 */
public class DetectedRegion extends TwoThresholdDetector {
	
	private GeminiImageRecordI geminiRecord;
	
	private ArrayList<Integer> pointIndexes;
	
	/*
	 * Bins are only used when detecting. Only the abs values are stored in database. 
	 */
	private int minBearingBin, maxBearingBin;
	
	private int minRangeBin, maxRangeBin;
	
	private double minBearing, maxBearing;
	
	private double minRange, maxRange;
	
	private double peakRange, peakBearing;

	private int totalValue;
	
	private int maxValue;
	
	private int maxIndex;
	
	private int nPoints;

	private int sonarId;
	
	// object diagonal size in metres. 
	private double objectSize;

	private long timeMilliseconds;
	
	private double peakX = -999, peakY = -999;
	
	/**
	 * Percentage of pixels within the wedge which were over threshold
	 */
	private double occupancy;

	/**
	 * @return the occupancy
	 */
	public double getOccupancy() {
		return occupancy;
	}

	/**
	 * @return the sonarId
	 */
	public int getSonarId() {
		return sonarId;
	}

	/**
	 * @return the timeMilliseconds
	 */
	public long getTimeMilliseconds() {
		return timeMilliseconds;
	}

	/**
	 * The biggest diagonal measurement of length and breadth. 
	 * @return the objectSize in metres. 
	 */
	public double getObjectSize() {
		return objectSize;
	}

	/**
	 * the number of pixels which went into the region  
	 * @return the nPoints
	 */
	public int getnPoints() {
		return nPoints;
	}


	/**
	 * During detection
	 * @param geminiRecord
	 * @param pointIndex
	 */
	public DetectedRegion(GeminiImageRecordI geminiRecord, int pointIndex) {
		this.geminiRecord = geminiRecord;
		this.timeMilliseconds = geminiRecord.getRecordTime();
		pointIndexes = new ArrayList<Integer>();
		pointIndexes.add(pointIndex);
		sonarId = geminiRecord.getDeviceId();
	}
	
	/**
	 * With viewer
	 * @param timeMilliseconds
	 * @param sonarId
	 * @param minB
	 * @param maxB
	 * @param minR
	 * @param maxR
	 * @param meanV
	 * @param totV
	 * @param maxV
	 */
	public DetectedRegion(long timeMilliseconds, int sonarId, double minB, double maxB, double peakB, 
			double minR, double maxR, double peakR, double objectSize, int meanV, int totV,
			int maxV, double occupancy) {
		this.timeMilliseconds = timeMilliseconds;
		this.sonarId = sonarId;
		this.minBearing = minB;
		this.maxBearing = maxB;
		this.peakBearing = peakB;
		this.minRange = minR;
		this.maxRange = maxR;
		this.peakRange = peakR;
		this.objectSize = objectSize;
		this.totalValue = totV;
		this.maxValue = maxV;
		this.occupancy = occupancy;
		this.nPoints = Math.round((totalValue+1)/(meanV+1));
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
		int peakRangeBin = 0, peakBearingBin = 0;
		for (int i = 0; i < pointIndexes.size(); i++) {
			int point = pointIndexes.get(i); 
			int val = Byte.toUnsignedInt(data[point]);
			int bearingBin = point % nBearing;
			int rangeBin = point / nBearing;
			totalValue += val;
			if (val > maxValue) {
				maxValue = val;
				peakRangeBin = rangeBin;
				peakBearingBin = bearingBin;
			}
			minBearingBin = Math.min(minBearingBin, bearingBin);
			maxBearingBin = Math.max(maxBearingBin, bearingBin);
			minRangeBin = Math.min(minRangeBin, rangeBin);
			maxRangeBin = Math.max(maxRangeBin, rangeBin);
		}
		nPoints = pointIndexes.size();
		double[] bearingTable = geminiRecord.getBearingTable();
		// reverse these since the tabel is backwards. 
		maxBearing = bearingTable[minBearingBin];
		minBearing = bearingTable[maxBearingBin];
		minRange = geminiRecord.getMaxRange() * (double) minRangeBin / (double) geminiRecord.getnRange();
		maxRange = geminiRecord.getMaxRange() * (double) maxRangeBin / (double) geminiRecord.getnRange();
		peakRange = geminiRecord.getMaxRange() * (double) peakRangeBin / (double) geminiRecord.getnRange();
		peakBearing = bearingTable[peakBearingBin];
		/*
		 * now work out a single size value for the object in metres. This is basically
		 * the diagonal measurment across the shape.   
		 */
		double x1 = minRange*Math.sin(minBearing);
		double y1 = minRange*Math.cos(minBearing);
		double x2 = maxRange*Math.sin(maxBearing);
		double y2 = maxRange*Math.cos(maxBearing);
		objectSize = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
		
		double area = (maxBearingBin-minBearingBin+1)*(maxRangeBin-minRangeBin+1);
		occupancy = 100.*nPoints/area;
//		double sz2 = (maxBearing-minBearing)*maxRange;
//		if (objectSize > 3 && minRange > 56) {
//			sz2 = (maxBearing-minBearing)*maxRange;
//			
//		}
		
		geminiRecord = null; // get rid of this or we run out of memory. 
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
		return (int) (totalValue / nPoints);
	}
	
	/**
	 * Get the total number of pixels making up the region. 
	 * @return number of pixels in region
	 */
	public int getRegionSize() {
		if (pointIndexes != null) {
			return pointIndexes.size();
		}
		return nPoints;
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @return the peakRange
	 */
	public double getPeakRange() {
		return peakRange;
	}

	/**
	 * @return the peakBearing
	 */
	public double getPeakBearing() {
		return peakBearing;
	}
	
	/**
	 * Get X coordinate of peak
	 * @return
	 */
	public double getPeakX() {
		if (peakX == -999) {
			calcPeakXY();
		}
		return peakX;
	}

	/**
	 * Get Y coordinate of peak
	 * @return
	 */
	public double getPeakY() {
		if (peakY == -999) {
			calcPeakXY();
		}
		return peakY;
	}

	/**
	 * note that these are using the sonar coordinate frame which has x -ve. 
	 * A hangover from the Tritech data where this made everything OK
	 * due to the flip. 
	 */
	private void calcPeakXY() {
		peakX = peakRange * Math.sin(peakBearing);
		peakY = peakRange * Math.cos(peakBearing);		
	}
	
	public void merge(DetectedRegion other) {
		minBearing = Math.min(minBearing, other.minBearing);
		maxBearing = Math.max(maxBearing, other.maxBearing);
		minRange = Math.min(minRange,  other.minRange);
		maxRange = Math.max(maxRange, other.maxRange);
		if (other.maxValue > this.maxValue) {
			this.maxValue = other.maxValue;
			this.peakBearing = other.peakBearing;
			this.peakRange = other.peakRange;
		}
		this.totalValue = this.totalValue+other.totalValue;
		double w1 = (double) this.nPoints / (double) (this.nPoints+other.nPoints);
		double w2 = 1-w1;
		this.occupancy = this.occupancy*w1 + other.occupancy*w2;
	}
	
	/**
	 * check to see if another region overlaps this one in bearing or range. 
	 * @param other
	 * @return true if overlapping
	 */
	public boolean overlaps(DetectedRegion other) {
		if (this.getMinRange() > other.getMaxRange()) {
			return false;
		}
		if (this.getMaxRange() < other.getMinRange()) {
			return false;
		}
		if (this.getMinBearing() > other.getMaxBearing()) {
			return false;
		}
		if (this.getMaxBearing() < other.getMinBearing()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String str = String.format("Region: %3.1f-%3.1f deg, %3.1f-%3.1fm",
				Math.toDegrees(minBearing), Math.toDegrees(maxBearing), minRange, maxRange);
		return str;
	}
}
