package tritechgemini.echogram;

import tritechgemini.imagedata.SonarImageRecordI;

public class EchogramLine {

	private SonarImageRecordI geminiRecord;
	private EchoLineDef echoLineDef;
	private short[] data;

	public EchogramLine(SonarImageRecordI geminiRecord, EchoLineDef echoLineDef, short[] data) {
		this.geminiRecord = geminiRecord;
		this.echoLineDef = echoLineDef;
		this.data = data;
	}

	/**
	 * @return the geminiRecord
	 */
	public SonarImageRecordI getGeminiRecord() {
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

	public void setGeminiRecord(SonarImageRecordI imageRecord) {
		this.geminiRecord = imageRecord;		
	}

}
