package tritechgemini.fileio;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Generic header structure for .dat file records. 
 * @author Doug Gillespie
 *
 */
public class GLFGenericHeader implements Serializable {
	
	/**
	 * SVID. Added May 2023. Wasn't in earlier versions which has caused
	 * no end of trouble with serialised data maps. This  
	 */
	private static final long serialVersionUID = -5260692970291352675L;
	
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
	
	/**
	 * Write the header data to a data output stream.
	 * @param dos This should always be a LittleEndian output stream. 
	 * @throws IOException
	 */
	public void write(LittleEndianDataOutputStream dos) throws IOException {
		dos.writeByte(m_idChar);
		dos.writeByte(m_version);
		dos.writeInt(m_length);
		dos.writeDouble(m_timestamp);
		dos.writeByte(m_dataType);
		dos.writeShort(tm_deviceId);
		dos.writeShort(m_node_ID);
		dos.writeShort(m_spare);
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
