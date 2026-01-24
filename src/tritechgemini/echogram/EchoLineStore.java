package tritechgemini.echogram;

import java.util.HashMap;

/**
 * A store for Echolines added to each image record. 
 */
public class EchoLineStore {

	private HashMap<EchoLineDef, EchogramLine> echoLines;
	
	public EchoLineStore() {
	}
	
	synchronized public void clearStore() {
		if (echoLines == null) {
			return;
		}
		echoLines.clear();
	}
	/**
	 * Put an echo line in the store
	 * @param echoLine
	 */
	synchronized public void setEchoLine(EchogramLine echoLine) {
		if (echoLines == null) {
			echoLines = new HashMap<EchoLineDef, EchogramLine>();
		}
		echoLines.put(echoLine.getEchoLineDef(), echoLine);
	}
	
	/**
	 * Get an echo line from the store. 
	 * @param echoLineDef
	 * @return
	 */
	synchronized public EchogramLine getEchoLine(EchoLineDef echoLineDef) {
		if (echoLines == null) {
			return null;
		}
		return echoLines.get(echoLineDef);
	}

}
