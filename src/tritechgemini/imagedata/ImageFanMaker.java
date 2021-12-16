package tritechgemini.imagedata;


/**
 * Functions to convert an array or raw sonar data into a fan shaped image. Well,
 * Not actually an image, but an array of numbers that can be put into an image. 
 * @author dg50
 *
 */
abstract public class ImageFanMaker {

	/**
	 * Create fan data from a GeminiRecord with default sizes, probably a 
	 * width equal to the number of beams and a height scaled accordingly. 
	 * @param geminiRecord
	 * @return Fan image data
	 */
	public FanImageData createFanData(GeminiImageRecordI geminiRecord) {
		return createFanData(geminiRecord, getDefaultXbins(geminiRecord));
	}
	
	/**
	 * Get the default number of X bins. Default default is the number of beams
	 * but this may be overridden by methods which want a tighter image. 
	 * @param geminiRecord Gemini data record
	 * @return default image width
	 */
	public int getDefaultXbins(GeminiImageRecordI geminiRecord) {
		return geminiRecord.getnBeam();
	}
	
	/**
	 * Create fan image with the given width. height will be scaled according
	 * to the range of the bearing table 
	 * @param geminiRecord Gemini data record
	 * @param nPixX number of X pixels
	 * @return Fan image data
	 */
	public FanImageData createFanData(GeminiImageRecordI geminiRecord, int nPixX) {
		double[] bearingTable = geminiRecord.getBearingTable();
		if (bearingTable == null) {
			return null;
		}
		double b1 = Math.abs(bearingTable[0]);
		int nPixY = (int) Math.ceil(nPixX/(2.*Math.sin(b1)));
		return createFanData(geminiRecord, nPixX, nPixY);
	}
	
	/**
	 * n width. height will be scaled according
	 * to the range of the bearing table 
	 * @param geminiRecord Gemini data record
	 * @param nPixX number of X pixels
	 * @param nPixY number of Y pixels
	 * @return Fan image data
	 */
	public abstract FanImageData createFanData(GeminiImageRecordI geminiRecord, int nPixX, int nPixY);
	
	/**
	 * Most image makers will contain a load of lookup tables to get data in the right place
	 * this will clear the tables so that they can be rebuilt on the next call to createFanData
	 */
	public abstract void clearTables();
}
