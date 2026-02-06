package tritechgemini.echogram;

import tritechgemini.imagedata.SonarImageRecordI;

public interface EchogramLineMaker {

	public EchogramLine getEchogramLine(SonarImageRecordI imageRecord, EchoLineDef echoLineDef);
	
	public EchogramLine makeEchogramLine(SonarImageRecordI imageRecord, EchoLineDef echoLineDef);
	
}
