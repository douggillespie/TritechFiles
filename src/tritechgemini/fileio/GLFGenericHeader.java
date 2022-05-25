package tritechgemini.fileio;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;

/**
 * Generic header structure for .dat file records. 
 * @author dg50
 *
 */
public class GLFGenericHeader {

	public byte m_idChar;
	public int m_version;
	public int m_length;
	public double m_timestamp;
	public int m_dataType;
	public int tm_deviceId;
	public int m_node_ID;
	public int m_spare;


	public int read(DataInput dis) throws CatalogException {
		try {
			m_idChar = dis.readByte();
			m_version = dis.readUnsignedByte();
			//		if (glfImage.m_version == DE || glfImage.m_idChar != 42) {
			//			return false;
			//		}
			m_length = dis.readInt();
			m_timestamp = dis.readDouble();
			m_dataType = dis.readUnsignedByte(); // getting a datatype 3, which is not image data. FFS. 
			tm_deviceId = dis.readUnsignedShort();
			m_node_ID = dis.readUnsignedShort();
			m_spare = dis.readShort();
		}
		catch (EOFException eof) {
			return 0;
		}
		catch (IOException ioEx) {
			throw (new CatalogException(ioEx.getMessage()));
		}
		return 21;
	}
	
	public GLFGenericHeader create(DataInput dis) throws CatalogException {
		GLFGenericHeader header = new GLFGenericHeader();
		header.read(dis);
		return header;
	}
	
	public int getByteSize() {
		return 21;
	}
}
