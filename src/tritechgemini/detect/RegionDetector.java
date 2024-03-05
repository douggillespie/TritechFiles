package tritechgemini.detect;

import java.util.ArrayList;

import tritechgemini.imagedata.GeminiImageRecordI;

public interface RegionDetector {

	/**
	 * @param minObjectSize the minObjectSize to set
	 */
	void setMinObjectSize(double minObjectSize);

	/**
	 * @param maxObjectSize the maxObjectSize to set
	 */
	void setMaxObjectSize(double maxObjectSize);

	/**
	 * Search for regions using the raw data within the record
	 * @param rawGeminiRecord Raw data record
	 * @param denoisedRecord Data with background removed. 
	 * @param thHigh higher threshold 
	 * @param thLow lower threshold
	 * @return 
	 */
	ArrayList<DetectedRegion> detectRegions(GeminiImageRecordI rawGeminiRecord, GeminiImageRecordI denoisedRecord, int thHigh, int thLow, int nConnect);

//	/**
//	 * Search for regions using a different set of raw data, which should probably be a noise reduced version of 
//	 * what was in the original record. Must be the same dimension as that in the original. 
//	 * @param geminiRecord
//	 * @param recordData
//	 * @param thHigh
//	 * @param thLow
//	 * @return
//	 */
//	ArrayList<DetectedRegion> detectRegions(GeminiImageRecordI geminiRecord, byte[] recordData, int thHigh, int thLow,
//			int nConnect);

}