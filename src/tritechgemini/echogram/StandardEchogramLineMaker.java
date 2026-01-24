package tritechgemini.echogram;

import tritechgemini.fileio.MultiFileCatalog;
import tritechgemini.imagedata.GeminiImageRecordI;

public class StandardEchogramLineMaker implements EchogramLineMaker {

	private MultiFileCatalog multiFileCatalog;
	private EchoLineStore echoStore;

	/**
	 * Standard creator of Echogram lines. 
	 * @param multiFileCatalog file catalogue. Will probably be null in real time ops, needed in Viewer. 
	 */
	public StandardEchogramLineMaker(MultiFileCatalog multiFileCatalog) {
		this.multiFileCatalog = multiFileCatalog;
	}

	/**
	 * Get an echogram line, creating it if necessary
	 * @param imageRecord
	 * @param echoLineDef
	 * @return
	 */
	public EchogramLine getEchogramLine(GeminiImageRecordI imageRecord, EchoLineDef echoLineDef) {
		echoStore = imageRecord.getEchoLineStore();
		if (echoStore == null) {
			return null;
		}
		EchogramLine echoLine = echoStore.getEchoLine(echoLineDef);
		if (echoLine == null) {
			echoLine = makeEchogramLine(imageRecord, echoLineDef);
			if (echoLine != null) {
				echoStore.setEchoLine(echoLine);
			}
		}
		return echoLine;
	}
	
	/**
	 * Make an echogram line. Generally you shouldn't call this, but instead 
	 * use getEchogramLine which will first attempt to return a stored line. 
	 * @param imageRecord
	 * @param echoLineDef
	 * @return
	 */
	public EchogramLine makeEchogramLine(GeminiImageRecordI imageRecord, EchoLineDef echoLineDef) {
		boolean hasIm = imageRecord.isFullyLoaded();
		if (hasIm == false && multiFileCatalog != null) {
			if (multiFileCatalog.loadFully(imageRecord) == false) {
				return null;
			}
		}
		short[] shortData = imageRecord.getShortImageData();
		if (shortData == null) {
			return null;
		}
		int nRange = imageRecord.getnRange();
		int nBearing = imageRecord.getnBeam();
		short[] echoData = new short[nRange];
		int bb0 = checkbinRange(echoLineDef.bearingBin1, nBearing);
		int bb1 = checkbinRange(echoLineDef.bearingBin2, nBearing);
		for (int ir = 0; ir < nRange; ir++) {
			int s0 = nBearing*ir + bb0;
			int s1 = nBearing*ir + bb1;
			echoData[ir] = pickValue(shortData, s0, s1, echoLineDef);
		}
		
		return new EchogramLine(imageRecord, echoLineDef, echoData);
	}

	/**
	 * Pick a value for the raw data for the echo line based on options in echolinedef
	 * @param shortData all raw data, int16 format
	 * @param s0 first bin in all data
	 * @param s1 last bin in all data. 
	 * @param echoLineDef other options
	 * @return value to use in Echogram
	 */
	private short pickValue(short[] shortData, int s0, int s1, EchoLineDef echoLineDef) {
		/*
		 *  that's the range of the data. For now take the max in that, but will
		 *  consider other options (mean, mean of max few values, etc. in future) 
		 */
		short val = 0;
		for (int i = s0; i < s1; i++) {
			if (shortData[i] > val) {
				val = shortData[i];
			}
		}
		return val;
	}

	/**
	 * Check range of bearing bins. 
	 * @param bearingBin1
	 * @param nBearing
	 * @return
	 */
	private int checkbinRange(int bearingBin, int nBearing) {
		return Math.max(0, Math.min(bearingBin, nBearing));
	}
	
}
