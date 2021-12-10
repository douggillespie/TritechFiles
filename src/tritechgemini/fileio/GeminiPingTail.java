package tritechgemini.fileio;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

public class GeminiPingTail  {
	
	int headerBlank;
	int headerType;
	int count;
	int elSz;
	double tideX, tideY;
	int m_softwareGainRamp;
	int m_rangeCompUsed;
	int m_gainType;
	int m_txSourceLevel;

	public GeminiPingTail(File ecdFile, int recordType, int recordVersion) {
//		super(ecdFile, recordType, recordVersion);
	}

	public boolean readDataFile(DataInput dis) throws IOException {
		headerBlank = dis.readInt();
		headerType = dis.readInt(); // type 52 - not listed in the documentation. 
		int headerBytes = dis.readInt();
		int elCount = dis.readInt(); // seems to be the same as the number of ranges. 763, so if we read that 
		// many 4 byte words, we get 3052 more byts, which is close to the end of the packet. 
		int elSz = dis.readInt(); // is 4, which is correct. 
		m_softwareGainRamp = dis.readUnsignedShort();
		m_rangeCompUsed = dis.readUnsignedShort();
		m_gainType = dis.readUnsignedShort();
		m_txSourceLevel = dis.readShort();
//		double tideX = dis.readDouble(); // doesn't make sense
//		double tideY = dis.readDouble();
//		int m_usSonarSubType = dis.readShort();
		int exWords = 9;
		short[] tests = new short[exWords]; // headerBytes is 34, we're skipping 26 here, so that leaves 8 which must be elCount and elSz. So some sense. 
		for (int i = 0; i < tests.length; i++) {
			tests[i] = dis.readShort();
		}
		int[] m_Scale = new int[elCount];
		int[] m_lineInfo = new int[elCount];
		for (int i = 0; i < elCount; i++) {
			m_Scale[i] = dis.readUnsignedShort();
			m_lineInfo[i] = dis.readUnsignedShort();
		}
//		int skipCount = this.moveToEnd(dis); // generally around 3092 bytes - so a lot of data being skipped. 
//		if (skipCount != 2) {
//			System.out.printf("Skipped %d bytes to find end of GeminiPingTail type %d\n", skipCount, headerType);
//		}
		return true;
	}

}
