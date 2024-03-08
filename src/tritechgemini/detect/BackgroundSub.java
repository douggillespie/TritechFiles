package tritechgemini.detect;

import java.util.Arrays;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Really simple background subtraction class
 * @author dg50
 *
 */
public class BackgroundSub {

	private int[] background;
	
	private int[] variance;
	
	/**
	 * Store number of bins in background so we can handle resizing sensibly. 
	 */
	private int backgroundNRange, backgroundNBearing;
	
	/**
	 * Trying to do everything with fixed point maths, so we  
	 * need to scale up the data, might even be best to use float, or small values never
	 * get to contribute to the background data. So long as this is >> the updateConst, then 
	 * even a value of 1 will contribute. 
	 */
	private final int internalScale = 128;
	
	/**
	 * We may want to remove some factor times the background, in which case
	 * removalScale is slightly < than 1*internalScale. 
	 */
	private int removalFactor = internalScale;
	
	private int stdRemovalFactor = 0;

	/**
	 * update time constant in frames. should be about 1/20.
	 * but it's stored as pos integer, so will be about 20.  
	 */
	private int updateConst = 20; 

	/**
	 * Flag to say variance should also be calculated. 
	 */
	private boolean calculateVariance;

	/**
	 * Remove background from an image record. 
	 * @param geminiRecord Image record
	 * @param updateFirst  update the background measurement before subtraction
	 * @return clone of the input record with updated background. 
	 */
	public GeminiImageRecordI removeBackground(GeminiImageRecordI geminiRecord, boolean updateFirst) {
		byte[] data = geminiRecord.getImageData();
		byte[] newData = removeBackground(data, geminiRecord.getnBeam(), geminiRecord.getnRange(), updateFirst);
		GeminiImageRecordI newRecord = geminiRecord.clone();
		newRecord.setImageData(newData);
		return newRecord;
	}
	
	/**
	 * Remove background from raw image data
	 * @param data raw image data 
	 * @param updateFirst update the background measurement before subtraction
	 * @return new array of data with background subtracted. 
	 */
	public byte[] removeBackground(byte[] data, int nBearing, int nRange, boolean updateFirst) {
		if (updateFirst) {
			calcBackground(data, nBearing, nRange);
		}
		else {
			checkArray(nBearing, nRange);
		}
		byte[] cleanData = new byte[data.length];
		int val;
		if (stdRemovalFactor > 0) {
			for (int i = 0; i < data.length; i++) {
				int std = (int) Math.sqrt(variance[i]/stdRemovalFactor);
				val =  Math.max(Byte.toUnsignedInt(data[i])-background[i]/removalFactor - std,0);
				cleanData[i] = (byte) (val & 0xFF);
			}
		}
		else {
			for (int i = 0; i < data.length; i++) {
				val =  Math.max(Byte.toUnsignedInt(data[i])-background[i]/removalFactor,0);
				cleanData[i] = (byte) (val & 0xFF);
			}
		}
		
		return cleanData;
	}
	
	public int[] calcBackground(byte[] data, int nBearing, int nRange) {
		checkArray(nBearing, nRange);
		if (data == null) {
			return background;
		}
		/*
		 *  a bit daft having two loops here, but only want to
		 *  convert the byte data once and don't want an if within
		 *  every loop.  
		 */
		int dataPoint;
		if (calculateVariance) {
			int var;
			for (int i = 0; i < data.length; i++) {
				dataPoint = Byte.toUnsignedInt(data[i])*internalScale;
				var = dataPoint-background[i];
				var *= var;
				background[i] += ((dataPoint-background[i]) / updateConst);
				variance[i] += ((var-variance[i]) / updateConst);
			}
		}
		else {
			for (int i = 0; i < data.length; i++) {
				dataPoint = Byte.toUnsignedInt(data[i])*internalScale;
				background[i] += ((dataPoint-background[i]) / updateConst);
			}
		}
		return background;
	}
		
	/**
	 * Check array dimensions. Only calculate the variance if
	 * flagged to do so in calculateVariance
	 * @param nBearing
	 * @param nRange
	 */
	private void checkArray(int nBearing, int nRange) {
		/**
		 * If there was no background, or if the number of bearing bins has changed at all
		 * or if the number of range bins has changed by > 2, then total rebuild. 
		 */
		if (background == null || backgroundNBearing != nBearing || Math.abs(nRange-backgroundNRange) > 20) {
			background = new int[nBearing*nRange];
			backgroundNBearing = nBearing;
			backgroundNRange = nRange;
		}
		if (calculateVariance && variance == null && background != null) {
			variance = new int[background.length];
		}
		// here we at least know we have the same number of bearings
		if (nRange < backgroundNRange) {
			// leave it ! The background is a bit big, but the data will 
			// probably grow back to that size in a frame or two. 
			return;
		}
		if (nRange > backgroundNRange) {
			// assume high correlation within a bin and copy 
			// data from the last range into the new ranges. 
			int extraRanges = nRange - backgroundNRange;
			background = Arrays.copyOf(background, nRange*nBearing);
			int oldEnd = backgroundNBearing*backgroundNRange;
			for (int i = 0; i < extraRanges; i++) {
				for (int b = 0; b < nBearing; b++) {
					background[oldEnd + i* nBearing + b] = background[oldEnd - nBearing + b];
				}
			}
			if (calculateVariance) {
				variance = Arrays.copyOf(variance, nRange*nBearing);
				for (int i = 0; i < extraRanges; i++) {
					for (int b = 0; b < nBearing; b++) {
						variance[oldEnd + i* nBearing + b] = variance[oldEnd - nBearing + b];
					}
				}
			}
			backgroundNBearing = nBearing;
			backgroundNRange = nRange;
		}
	}
	
	/**
	 * Get the current background. 
	 * <br>Note that this 
	 * may be greater in dimension than the current image
	 * size, but should never be smaller
	 * @return background converted to byte. 
	 */
	public byte[] getBackground() {
		if (background == null) {
			return null;
		}
		byte[] data = new byte[background.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (Math.max(background[i]/internalScale,0) & 0xFF);
		}
		return data;
		
	}
	
	/**
	 * Get the variance of the data. 
	 * <br>Note that this 
	 * may be greater in dimension than the current image
	 * size, but should never be smaller
	 * @return variance
	 */
	public int[] getVariance() {
		if (variance == null || calculateVariance == false) {
			return null;
		}
		int scale = internalScale*internalScale;
		int[] data = new int[variance.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = variance[i]/scale;
		}
		return data;
	}
	
	/**
	 * Get the standard deviation of the data in short integers.
	 * <br>Note that this 
	 * may be greater in dimension than the current image
	 * size, but should never be smaller 
	 * @return the standard deviation of the data. 
	 */
	public short[] getSTDI() {
		if (variance == null || calculateVariance == false) {
			return null;
		}
		short[] data = new short[variance.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (short) (Math.sqrt(variance[i])/internalScale);
		}
		return data;
	}
	/**
	 * Get the standard deviation of the data.
	 * <br>Note that this 
	 * may be greater in dimension than the current image
	 * size, but should never be smaller 
	 * @return the standard deviation of the data. 
	 */
	public byte[] getSTD() {
		if (variance == null || calculateVariance == false) {
			return null;
		}
		int scale = internalScale*internalScale;
		byte[] data = new byte[variance.length];
		for (int i = 0; i < data.length; i++) {
			int pt = (int) Math.sqrt(variance[i])/internalScale;
			data[i] = (byte) (pt & 0xFF);
		}
		return data;
	}

	/**
	 * Background scale is 1/the update fraction, i.e. a big 
	 * number updates slowly. 
	 * @return the backgroundScale
	 */
	public int getTimeConstant() {
		return updateConst;
	}

	/**
	 * Background scale is 1/the update fraction, i.e. a big 
	 * number updates slowly. 
	 * @param backgroundScale the backgroundScale to set
	 */
	public void setTimeConstant(int timeConstant) {
		this.updateConst = Math.max(1, timeConstant);
	}
	
	/**
	 * Set a scaling factor for background removal. 1. is remove the background as is, 1.1 would remove a
	 * bit more than the background, etc. 
	 * @param removalScale
	 */
	public void setRemovalScale(double removalScale) {
		this.removalFactor = (int) Math.round(internalScale / removalScale);
	}

	public void setRemovalScale(double backgroundScale, double backgroundSTDs) {
		this.removalFactor = (int) Math.round(internalScale / backgroundScale);
		this.calculateVariance = backgroundSTDs > 0;
		this.stdRemovalFactor = (int) Math.round(Math.pow(internalScale / backgroundSTDs,2));
	}
	
	/**
	 * 
	 * Get the scaling factor for background removal. 1. is remove the background as is, 1.1 would remove a
	 * bit more than the background, etc. 
	 * @return removal scale
	 */
	public double getRemovalScale() {
		return (double) internalScale / (double) removalFactor;
	}

	/**
	 * Check whether or not calculating the variance (and STD) in the levels as
	 * the means. Only worth doing this if you want these stats for your detector 
	 * @return the calculateVariance
	 */
	public boolean isCalculateVariance() {
		return calculateVariance;
	}

	/**
	 * Say whether or not to calculate the variance (and STD) in the levels as
	 * the means. Only worth doing this if you want these stats for your detector 
	 * @param calculateVariance the calculateVariance to set
	 */
	public void setCalculateVariance(boolean calculateVariance) {
		this.calculateVariance = calculateVariance;
	}
}
