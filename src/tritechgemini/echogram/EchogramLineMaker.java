package tritechgemini.echogram;

import tritechgemini.imagedata.GeminiImageRecordI;

public interface EchogramLineMaker {

	public EchogramLine getEchogramLine(GeminiImageRecordI imageRecord, EchoLineDef echoLineDef);
	
	public EchogramLine makeEchogramLine(GeminiImageRecordI imageRecord, EchoLineDef echoLineDef);
	
}
