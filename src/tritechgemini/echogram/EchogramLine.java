package tritechgemini.echogram;

import tritechgemini.imagedata.GeminiImageRecordI;

public class EchogramLine {

	private GeminiImageRecordI geminiRecord;
	private EchoLineDef echoLineDef;
	private short[] data;

	public EchogramLine(GeminiImageRecordI geminiRecord, EchoLineDef echoLineDef, short[] data) {
		this.geminiRecord = geminiRecord;
		this.echoLineDef = echoLineDef;
		this.data = data;
	}

	/**
	 * @return the geminiRecord
	 */
	public GeminiImageRecordI getGeminiRecord() {
		return geminiRecord;
	}

	/**
	 * @return the echoLineDef
	 */
	public EchoLineDef getEchoLineDef() {
		return echoLineDef;
	}

	/**
	 * @return the data
	 */
	public short[] getData() {
		return data;
	}

	public void setGeminiRecord(GeminiImageRecordI imageRecord) {
		this.geminiRecord = imageRecord;		
	}

}
