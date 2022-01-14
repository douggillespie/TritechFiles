package tritechgemini.fileio;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

public class GeminiAcousticZoom  {

	public GeminiAcousticZoom(File ecdFile, int recordType, int recordVersion) {
//		super(ecdFile, recordType, recordVersion);
	}

	public boolean readDataFile(DataInput dis) throws IOException {
		
		int m_acousticVer = dis.readUnsignedShort();
		int m_headType = dis.readUnsignedShort();
		int m_chirp = dis.readInt();
		int m_azID = dis.readShort();
		int m_Active = dis.readUnsignedByte();
		if (m_Active ==1) {
			double m_range = dis.readDouble(); 
			// and other variables if the acoustic zoom is active, but it never is
			// so no useful information here!
		}
		
		
//		int skipCount = this.moveToEnd(dis);
//		if (skipCount > 2) {
//			System.out.printf("Skipped %d bytes to find end of GeminiAcousticZoom\n", skipCount);
//		}
		return true;
	}

}
