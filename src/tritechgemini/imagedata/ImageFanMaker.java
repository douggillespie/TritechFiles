package tritechgemini.imagedata;


/**
 * Functions to convert an array or raw sonar data into a fan shaped image. Well,
 * Not actually an image, but an array of numbers that can be put into an image. <br>
 * If image data specified, it can be processed data, e.g. after some sort of background subtraction,
 * otherwise it will be taken as the raw image data from within the data record
 * @author Doug Gillespie
 *
 */
abstract public class ImageFanMaker {

	/**
	 * Create fan data from a GeminiRecord with default sizes, probably a 
	 * width equal to the number of beams and a height scaled accordingly. 
	 * @param sonarRecord
	 * @return Fan image data
	 */
	public FanImageData createFanData(SonarImageRecordI sonarRecord) {
		return createFanData(sonarRecord, getDefaultXbins(sonarRecord));
	}
	/**
	 * Create fan data from a GeminiRecord with default sizes, probably a 
	 * width equal to the number of beams and a height scaled accordingly. 
	 * <br> This function is most useful if you've modified the image data from
	 * a sonar record in some way (e.g. background subtraction) and want an image 
	 * of the modified data. 
	 * @param sonarRecord Gemini record
	 * @param raw image data
	 * @return Fan image data
	 */
	public FanImageData createFanData(SonarImageRecordI sonarRecord, byte[] imageData) {
		return createFanData(sonarRecord, getDefaultXbins(sonarRecord), imageData);
	}
	
	/**
	 * Create fan data using the raw data from the record and the given number
	 * of x pixels. Number of y pixels calculated automatically.  
	 * @param sonarRecord  Gemini record
	 * @param nXbins number of x pixels
	 * @return Fan image data
	 */
	public FanImageData createFanData(SonarImageRecordI sonarRecord, int nXbins) {
		return createFanData(sonarRecord, nXbins, sonarRecord.getImageData());
	}

	/**
	 * Create fan image with the given width. height will be scaled according
	 * to the range of the bearing table 
	 * @param sonarRecord Gemini data record
	 * @param nPixX number of X pixels
	 * @param raw image data
	 * @return Fan image data
	 */
	public FanImageData createFanData(SonarImageRecordI sonarRecord, int nPixX, byte[] imageData) {
		double[] bearingTable = sonarRecord.getBearingTable();
		if (bearingTable == null || bearingTable.length == 0) {
			return null;
		}
		double b1 = Math.abs(bearingTable[0]);
		int nPixY = (int) Math.ceil(nPixX/(2.*Math.sin(b1)));
		return createFanData(sonarRecord, nPixX, nPixY, imageData);
	}
	
	/**
	 * n width. height will be scaled according
	 * to the range of the bearing table 
	 * @param sonarRecord Gemini data record
	 * @param nPixX number of X pixels
	 * @param nPixY number of Y pixels
	 * @return Fan image data
	 */
	public FanImageData createFanData(SonarImageRecordI sonarRecord, int nPixX, int nPixY) {
		return createFanData(sonarRecord, nPixX, nPixY, sonarRecord.getImageData());
	}

	/**
	 * n width. height will be scaled according
	 * to the range of the bearing table 
	 * @param sonarRecord Gemini data record
	 * @param nPixX number of X pixels
	 * @param nPixY number of Y pixels
	 * @param raw image data
	 * @return Fan image data
	 */
	public abstract FanImageData createFanData(SonarImageRecordI sonarRecord, int nPixX, int nPixY, byte[] imageData);
	
	/**
	 * Get the default number of X bins. Default default is the number of beams
	 * but this may be overridden by methods which want a tighter image. 
	 * @param sonarRecord Gemini data record
	 * @return default image width
	 */
	public int getDefaultXbins(SonarImageRecordI sonarRecord) {
		return sonarRecord.getnBeam();
	}

	/**
	 * Most image makers will contain a load of lookup tables to get data in the right place
	 * this will clear the tables so that they can be rebuilt on the next call to createFanData
	 */
	public abstract void clearTables();
}
