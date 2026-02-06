package tritechgemini.imagedata;

/**
 * Data for a fan shaped image. Note that this is only the data, not any type of actual
 * image. It's in int16 (short)format, even though the maximum value should still be the
 * max value from the sonars, which is 255, this allows setting all pixels outside the sonar
 * image to be set to -1, which can then be made transparent when the data are finally converted
 * into a buffered image. 
 * @author dg50
 *
 */
public class FanImageData implements Cloneable {

	private short[][] imageValues;
	
	private SonarImageRecordI geminiRecord;

	private double metresPerPixX, metresPerPixY;
	
	public FanImageData(SonarImageRecordI geminiRecord, short[][] imageValues, double metresPerPixX,
			double metresPerPixY) {
		super();
		this.geminiRecord = geminiRecord;
		this.imageValues = imageValues;
		this.metresPerPixX = metresPerPixX;
		this.metresPerPixY = metresPerPixY;
	}

	/**
	 * 
	 * @return the image data 0:255 within the sonar image, -1 in the rest of the rectangle. 
	 */
	public short[][] getImageValues() {
		return imageValues;
	}

	/**
	 * Image scale
	 * @return image scale in X direction in m/pix. 
	 */
	public double getMetresPerPixX() {
		return metresPerPixX;
	}

	/**
	 * Image scale
	 * @return image scale in Y direction in m/pix. 
	 */
	public double getMetresPerPixY() {
		return metresPerPixY;
	}

	/**
	 * Original gemini record that created these data. 
	 * @return Gemini data record
	 */
	public SonarImageRecordI getGeminiRecord() {
		return geminiRecord;
	}

	/**
	 * Deep hard clone of the image data
	 */
	@Override
	public FanImageData clone() {
		try {
			FanImageData cloned = (FanImageData) super.clone();
			// deep hard clone
			cloned.imageValues = cloned.imageValues.clone();
			int n = cloned.imageValues.length;
			for (int i = 0; i < n; i++) {
				cloned.imageValues[i] = cloned.imageValues[i].clone();
			}
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this;
		}
	}
}
