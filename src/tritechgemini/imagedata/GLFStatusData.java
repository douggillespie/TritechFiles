package tritechgemini.imagedata;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;

import tritechgemini.fileio.CatalogException;
import tritechgemini.fileio.GLFGenericHeader;
import tritechgemini.fileio.LittleEndianDataInputStream;

/**
 * This works reading from the latest GLF files and seems to get reasonable values. 
 * @author dg50
 *
 */
public class GLFStatusData extends PublicMessageHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public short m_bfVersion;
	public short m_daVer;
	public short m_deviceID;
	public byte reserved;
	public double m_vgaT1;
	public double m_vgaT2;
	public double m_vgaT3;
	public double m_vgaT4;
	public double m_psuT;
	public double m_dieT;
	public double m_txT;
	
	public double m_afe0TopTemp;
	public double m_afe0BotTemp;
	public double m_afe1TopTemp;
	public double m_afe1BotTemp;
	public double m_afe2TopTemp;
	public double m_afe2BotTemp;
	public double m_afe3TopTemp;
	public double m_afe3BotTemp;
	
	public short m_linkType;
	public double m_uplinkSpeedMbps;
	public double m_downlinkSpeedMbps;
	public short m_linkQuality;
	public int m_packetCount;
	public int m_recvErrorCount;
	public int m_resentPacketCount;
	public int m_droppedPacketCount;
	public int m_unknownPacketCount;
	public int m_lostLineCount;
	public int m_generalCount;
	
	public int m_sonarAltIp;
	public int m_surfaceIp;
	public int m_subNetMask;
	public short m_macAddress1;
	public short m_macAddress2;
	public short m_macAddress3;
	public int m_BOOTSTSRegister;
	public int m_BOOTSTSRegisterDA;
	public long m_fpgaTime;
	public short m_dipSwitch;
	public short m_shutdownStatus;
	public boolean m_networkAdaptorFound;
	public double m_subSeaInternalTemp;
	public double m_subSeaCpuTemp;

	public short m_flags;

	public byte m_xdSelected;

	public int m_uiFrame;
	
	// variable list copied from the C CGemStatusPacket.
//	public short m_firmwareVer;
//	public short m_sonarId;
//	public int   m_sonarFixIp;
//	public int   m_sonarAltIp;
//	public int   m_surfaceIp;
//	public short m_flags;
//	public short m_vccInt;
//	public short m_vccAux;
//	public short m_dcVolt;
//	public short m_dieTemp;
//	public short m_dipSwitch; /*Only for Mk2*/
//	public short m_vga1aTemp;
//	public short m_vga1bTemp;
//	public short m_vga2aTemp;
//	public short m_vga2bTemp;
//	public short m_psu1Temp;
//	public short m_psu2Temp;
//	public int   m_currentTimestampL;
//	public int   m_currentTimestampH;
//	public short m_transducerFrequency;
//	public int   m_subnetMask;
//	public short m_TX1Temp;
//	public short m_TX2Temp;
//	public short m_TX3Temp;
//	public int   m_BOOTSTSRegister;
//	public short m_shutdownStatus;
//	public short m_dieOverTemp;
//	public short m_vga1aShutdownTemp;
//	public short m_vga1bShutdownTemp;
//	public short m_vga2aShutdownTemp;
//	public short m_vga2bShutdownTemp;
//	public short m_psu1ShutdownTemp;
//	public short m_psu2ShutdownTemp;
//	public short m_TX1ShutdownTemp;
//	public short m_TX2ShutdownTemp;
//	public short m_TX3ShutdownTemp;
//	public short m_linkType;
//	public short m_VDSLDownstreamSpeed1;
//	public short m_VDSLDownstreamSpeed2;
//	public short m_macAddress1;
//	public short m_macAddress2;
//	public short m_macAddress3;
//	public short m_VDSLUpstreamSpeed1;
//	public short m_VDSLUpstreamSpeed2;
//	
	public boolean read(DataInput dis, boolean isOnline) throws CatalogException {
		
//		try {
//			m_firmwareVer = dis.readShort();
//			int m_daVer = dis.readShort();
//			m_sonarId = dis.readShort();
//			byte reserved = dis.readByte();
//			dis.skipBytes(4); // seems to be needed in the file. 
////			reserved = dis.readByte();
//			m_vgaT1 = dis.readDouble();
//			m_vgaT2 = dis.readDouble();
//			m_vgaT3 = dis.readDouble();
//			m_vgaT4 = dis.readDouble();
//			m_psuT = dis.readDouble();
//			m_dieT = dis.readDouble();
//			m_txT = dis.readDouble();
//
//			m_afe0TopTemp = dis.readDouble();
//			m_afe0BotTemp = dis.readDouble();
//			m_afe1TopTemp = dis.readDouble();
//			m_afe1BotTemp = dis.readDouble();
//			m_afe2TopTemp = dis.readDouble();
//			m_afe2BotTemp = dis.readDouble();
//			m_afe3TopTemp = dis.readDouble();
//			m_afe3BotTemp = dis.readDouble();
//			m_linkType = dis.readShort();
//			m_uplinkSpeedMbps = dis.readDouble();
//			m_downlinkSpeedMbps = dis.readDouble();
//			m_linkQuality = dis.readShort();
//			
//			m_packetCount = dis.readInt();
//			m_recvErrorCount = dis.readInt();
//			m_resentPacketCount = dis.readInt();
//
//			m_droppedPacketCount = dis.readInt();
//			m_unknownPacketCount = dis.readInt();
//			m_lostLineCount = dis.readInt();
//			m_generalCount = dis.readInt();
//			m_sonarAltIp = dis.readInt();
//			m_surfaceIp = dis.readInt();
//			m_subNetMask = dis.readInt();
//			m_macAddress1 = dis.readShort();
//			m_macAddress2 = dis.readShort();
//			m_macAddress3 = dis.readShort();
//			m_BOOTSTSRegister = dis.readInt();
//			m_BOOTSTSRegisterDA = dis.readInt();
//			m_fpgaTime = dis.readLong();
//			m_dipSwitch = dis.readShort();
//			m_shutdownStatus = dis.readShort();
//			m_networkAdaptorFound = dis.readByte() != 0;
////			byte[] data = new byte[20];
////			dis.readFully(data);
////					
////			dis.skipBytes(2);
//			// these two are in the documentation, but clearly don't exist in the data. 
////			m_subSeaInternalTemp = dis.readDouble();
////			m_subSeaCpuTemp = dis.readDouble();
//			
//			
//			
//		} catch (IOException e) {
//			throw new CatalogException(e.getMessage());
//		}
		// old variables ...
		try {
			if (isOnline == false) {
				int something1 = dis.readShort();
			}
			m_bfVersion = dis.readShort();
			m_daVer = dis.readShort();
			m_flags = dis.readShort();
			m_deviceID = dis.readShort();
			m_xdSelected = dis.readByte();
//			reserved = dis.readByte();
			m_vgaT1 = dis.readDouble();
			m_vgaT2 = dis.readDouble();
			m_vgaT3 = dis.readDouble();
			m_vgaT4 = dis.readDouble();
			m_psuT = dis.readDouble();
			m_dieT = dis.readDouble();
			m_txT = dis.readDouble();

			m_afe0TopTemp = dis.readDouble();
			m_afe0BotTemp = dis.readDouble();
			m_afe1TopTemp = dis.readDouble();
			m_afe1BotTemp = dis.readDouble();
			m_afe2TopTemp = dis.readDouble();
			m_afe2BotTemp = dis.readDouble();
			m_afe3TopTemp = dis.readDouble();
			m_afe3BotTemp = dis.readDouble();
			m_linkType = dis.readShort();
			m_uplinkSpeedMbps = dis.readDouble();
			m_downlinkSpeedMbps = dis.readDouble();
			m_linkQuality = dis.readShort();
			
			m_packetCount = dis.readInt();
			m_recvErrorCount = dis.readInt();
			m_resentPacketCount = dis.readInt();
			m_droppedPacketCount = dis.readInt();
			m_unknownPacketCount = dis.readInt();
			m_lostLineCount = dis.readInt();
			m_generalCount = dis.readInt();
			
			m_sonarAltIp = (dis.readInt());
			m_surfaceIp = dis.readInt();
			m_subNetMask = dis.readInt();
			m_macAddress1 = dis.readShort();
			m_macAddress2 = dis.readShort();
			m_macAddress3 = dis.readShort();
			m_BOOTSTSRegister = dis.readInt();
			m_BOOTSTSRegisterDA = dis.readInt();
			m_fpgaTime = dis.readLong();
			m_dipSwitch = dis.readShort();
			m_shutdownStatus = dis.readShort();
			m_networkAdaptorFound = dis.readByte() != 0;
//			byte[] data = new byte[20];
//			dis.readFully(data);
//					
//			dis.skipBytes(2);
			// these two are in the documentation, but clearly don't exist in the data. 
			if (isOnline) {
				m_subSeaInternalTemp = dis.readDouble();
				m_subSeaCpuTemp = dis.readDouble();
				m_uiFrame = dis.readInt();
			}
			
			
			
		} catch (IOException e) {
			throw new CatalogException(e.getMessage());
		}
		
		return true;
	}

	public GLFStatusData(GLFGenericHeader genericHeader) {
		super(genericHeader);
	}


}
