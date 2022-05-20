package tritechgemini.detect;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Really simple background subtraction class
 * @author dg50
 *
 */
public class BackgroundSub {

	private int[] background;
	
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
	
	/**
	 * update time constant in frames. should be about 1/20.
	 * but it's stored as pos integer, so will be about 20.  
	 */
	private int updateConst = 20; 

	/**
	 * Remove background from an image record. 
	 * @param geminiRecord Image record
	 * @param updateFirst  update the background measurement before subtraction
	 * @return clone of the input record with updated background. 
	 */
	public GeminiImageRecordI removeBackground(GeminiImageRecordI geminiRecord, boolean updateFirst) {
		byte[] data = geminiRecord.getImageData();
		byte[] newData = removeBackground(data, updateFirst);
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
	public byte[] removeBackground(byte[] data, boolean updateFirst) {
		if (updateFirst) {
			calcBackground(data);
		}
		else {
			checkArray(data.length);
		}
		byte[] cleanData = new byte[data.length];
		int val;
		for (int i = 0; i < data.length; i++) {
			val =  Math.max(Byte.toUnsignedInt(data[i])-background[i]/removalFactor,0);
			cleanData[i] = (byte) (val & 0xFF);
		}
		return cleanData;
	}
	
	public int[] calcBackground(byte[] data) {
		checkArray(data.length);
		for (int i = 0; i < data.length; i++) {
			background[i] += ((Byte.toUnsignedInt(data[i])*internalScale-background[i]) / updateConst);
		}
		return background;
	}
	
	private void checkArray(int len) {
		if (background == null || background.length != len) {
			background = new int[len];
		}
	}
	
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
	
	/**
	 * 
	 * Get the scaling factor for background removal. 1. is remove the background as is, 1.1 would remove a
	 * bit more than the background, etc. 
	 * @return removal scale
	 */
	public double getRemovalScale() {
		return (double) internalScale / (double) removalFactor;
	}
}
