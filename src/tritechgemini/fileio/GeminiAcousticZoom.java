package tritechgemini.fileio;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class GeminiAcousticZoom  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int m_acousticVer;
	public int m_headType;
	public int m_chirp;
	public short m_azID;
	public int m_Active;

	public GeminiAcousticZoom() {
//		super(ecdFile, recordType, recordVersion);
	}

	public boolean readDataFile(DataInput dis) throws IOException {
		
		m_acousticVer = dis.readUnsignedShort();
		m_headType = dis.readUnsignedShort();
		m_chirp = dis.readInt();
		m_azID = dis.readShort();
		m_Active = dis.readUnsignedByte();
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
