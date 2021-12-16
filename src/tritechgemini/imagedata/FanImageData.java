package tritechgemini.imagedata;

/**
 * Data for a fan shaped image. 
 * @author dg50
 *
 */
public class FanImageData {

	private short[][] imageValues;
	
	private double metresPerPixX, metresPerPixY;
	
	public FanImageData(GeminiImageRecordI geminiRecord, short[][] imageValues, double metresPerPixX,
			double metresPerPixY) {
		super();
		this.geminiRecord = geminiRecord;
		this.imageValues = imageValues;
		this.metresPerPixX = metresPerPixX;
		this.metresPerPixY = metresPerPixY;
	}

	public short[][] getImageValues() {
		return imageValues;
	}

	public double getMetresPerPixX() {
		return metresPerPixX;
	}

	public double getMetresPerPixY() {
		return metresPerPixY;
	}

	public GeminiImageRecordI getGeminiRecord() {
		return geminiRecord;
	}

	private GeminiImageRecordI geminiRecord;
}
