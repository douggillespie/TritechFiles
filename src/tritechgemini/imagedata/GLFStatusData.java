package tritechgemini.imagedata;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import tritechgemini.fileio.CatalogException;
import tritechgemini.fileio.GLFGenericHeader;
import tritechgemini.fileio.LittleEndianDataInputStream;
import tritechgemini.fileio.LittleEndianDataOutputStream;

/**
 * This works reading from the latest GLF files and seems to get reasonable values. 
 * @author Doug Gillespie
 *
 */
public class GLFStatusData extends PublicMessageHeader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * bit in m_shutdownStatus indicating out of water
	 */
	public static final int OUT_OF_WATER = 0x4;
	public static final int OUT_OF_WATERSHUTDOWN = 0x2;
	public static final int OVER_TEMPERATURE = 0x1;
	
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

	/**
	 * Source of data. file name or software. 
	 */
	public String source;
	
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
	
	public boolean readCGemStatus(DataInput dis) throws CatalogException {
		try {
			m_bfVersion = dis.readShort();
			m_deviceID = dis.readShort();
			dis.skipBytes(4);
			m_sonarAltIp = dis.readInt();
			m_surfaceIp = dis.readInt();
			m_flags = dis.readShort();
//			m_
					
		} catch (IOException e) {
			throw new CatalogException(e.getMessage());
		}

		return true;
	}
	
	public boolean writeStatusData(LittleEndianDataOutputStream outputStream, boolean isOnline) throws CatalogException {
		try {
			/*
			 * if (isOnline == false) {
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

			 */

			 if (isOnline == false) {
				 outputStream.writeShort(0);
			 }
			 outputStream.writeShort(m_bfVersion);
			 outputStream.writeShort(m_daVer);
			 outputStream.writeShort(m_flags);
			 outputStream.writeShort(m_deviceID);
			 outputStream.writeByte(m_xdSelected);
//			 outputStream.writeByte(reserved);
			 outputStream.writeDouble(m_vgaT1);
			 outputStream.writeDouble(m_vgaT2);
			 outputStream.writeDouble(m_vgaT3);
			 outputStream.writeDouble(m_vgaT4);
			 outputStream.writeDouble(m_psuT);
			 outputStream.writeDouble(m_dieT);
			 outputStream.writeDouble(m_txT);
			 /*
			  * 
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
			  */
			 outputStream.writeDouble(m_afe0TopTemp);
			 outputStream.writeDouble(m_afe0BotTemp);
			 outputStream.writeDouble(m_afe1TopTemp);
			 outputStream.writeDouble(m_afe1BotTemp);
			 outputStream.writeDouble(m_afe2TopTemp);
			 outputStream.writeDouble(m_afe2BotTemp);
			 outputStream.writeDouble(m_afe3TopTemp);
			 outputStream.writeDouble(m_afe3BotTemp);
			 outputStream.writeShort(m_linkType);
			 outputStream.writeDouble(m_uplinkSpeedMbps);
			 outputStream.writeDouble(m_downlinkSpeedMbps);
			 outputStream.writeShort(m_linkQuality);
			 /*
			  * 
			m_packetCount = dis.readInt();
			m_recvErrorCount = dis.readInt();
			m_resentPacketCount = dis.readInt();
			m_droppedPacketCount = dis.readInt();
			m_unknownPacketCount = dis.readInt();
			m_lostLineCount = dis.readInt();
			m_generalCount = dis.readInt();
			  */
			 outputStream.writeInt(m_packetCount);
			 outputStream.writeInt(m_recvErrorCount);
			 outputStream.writeInt(m_resentPacketCount);
			 outputStream.writeInt(m_droppedPacketCount);
			 outputStream.writeInt(m_unknownPacketCount);
			 outputStream.writeInt(m_lostLineCount);
			 outputStream.writeInt(m_generalCount);

			 /*
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
			  */
			 outputStream.writeInt(m_sonarAltIp);
			 outputStream.writeInt(m_surfaceIp);
			 outputStream.writeInt(m_subNetMask);
			 outputStream.writeShort(m_macAddress1);
			 outputStream.writeShort(m_macAddress2);
			 outputStream.writeShort(m_macAddress3);
			 outputStream.writeInt(m_BOOTSTSRegister);
			 outputStream.writeInt(m_BOOTSTSRegisterDA);
			 outputStream.writeLong(m_fpgaTime);
			 outputStream.writeShort(m_dipSwitch);
			 outputStream.writeShort(m_shutdownStatus);
			 outputStream.writeByte(m_networkAdaptorFound ? 1 : 0);

			 /*
			  * if (isOnline) {
				m_subSeaInternalTemp = dis.readDouble();
				m_subSeaCpuTemp = dis.readDouble();
				m_uiFrame = dis.readInt();
			}
			  */
			 if (isOnline) {
				 outputStream.writeDouble(m_subSeaInternalTemp);
				 outputStream.writeDouble(m_subSeaCpuTemp);
				 outputStream.writeInt(m_uiFrame);
			 }
			
		} catch (IOException e) {
			throw new CatalogException(e.getMessage());
		}
		return true;
	}
	
	/**
	 * Get the link type. 
	 * @return 0 = Ethernet, 1 = VDSL
	 */
	public int getLinkType() {
		return m_linkType & 0x1;
	}
	
	/**
	 * Get the link speed which should be 10,100 or 1000Mbps 
	 * @return link speed in Mbps
	 */
	public int getLinkSpeed() {
		int spd = (m_linkType >> 8) & 0x3;
		return (int) Math.pow(10, spd+1);
	}
	
	/**
	 * Get the 720im/Micron linkType
	 * @return 0 = no link (broadcast, 1 = Ethernet, 2 = Serial RS232, 3 = RS485  
	 */
	public int getMicronLinkType() {
		int typ = (m_linkType >> 10) & 0x3;
		return typ;
	}

	public GLFStatusData(GLFGenericHeader genericHeader, String source) {
		super(genericHeader);
		this.source = source;
	}
	
	/**
	 * Is it in an alarm state we shouldn't ignore. 
	 * @param ignoreOOW
	 * @return
	 */
	public boolean isAlarm(boolean ignoreOOW) {
		if (ignoreOOW) {
			return ((m_shutdownStatus & (OUT_OF_WATERSHUTDOWN | OVER_TEMPERATURE)) != 0);
		}
		else {
			return (m_shutdownStatus != 0);
		}
	}
	
	/**
	 * Out of water flag (0x4) in m_shutdownStatus is set.
	 * @return true if sonar is flagged as out of water. 
	 */
	public boolean isOutOfWater() {
		return ((m_shutdownStatus & OUT_OF_WATER) != 0);
//		return (m_shutdownStatus != 0);
	}
	
	/**
	 * Is outofwater shutdown set
	 * @return
	 */
	public boolean isOutOfWaterShutdown() {
		return ((m_shutdownStatus & OUT_OF_WATERSHUTDOWN) != 0);
//		return (m_shutdownStatus != 0);
	}
	
	/**
	 * is overtemp alarm bit set. 
	 * @return
	 */
	public boolean isOverTemp() {
		return ((m_shutdownStatus & OVER_TEMPERATURE) != 0);
	}
	
	/**
	 * return error string for shutdown. 
	 * @return null if no shutdown code, a string otherwise. 
	 */
	public String getShutdownError() {
		if (m_shutdownStatus == 0) {
			return null;
		}
		String err = "";
		if ((m_shutdownStatus & OUT_OF_WATER) != 0) {
			if (err.length()>0) {
				err += ",";
			}
			err += "Out of water";
		}
		if ((m_shutdownStatus & OUT_OF_WATERSHUTDOWN) != 0) {
			if (err.length()>0) {
				err += ",";
			}
			err += "Out of water shutdown";
		}
		if ((m_shutdownStatus & OVER_TEMPERATURE) != 0) {
			if (err.length()>0) {
				err += ",";
			}
			err += "Over temperature";
		}
		
		return err;
			
	}

	@Override
	public String toString() {
//		String str = String.format("Flags 0x%X 0x%X Temps 1:15: %3.1f, %3.1f, %3.1f, %3.1f, %3.1f, %3.1f, %3.1f, %3.1f, %3.1f", m_flags, m_shutdownStatus,
//				m_vgaT1, m_vgaT2, m_vgaT3, m_vgaT4, m_psuT, m_dieT, m_afe0TopTemp, m_afe0BotTemp, m_afe1TopTemp);
		String str = String.format("Flags flags: 0x%X shutdownsttauts: 0x%X, 0x%X, %d ", m_flags, m_shutdownStatus, m_xdSelected, (int) reserved);
		return str;	
	}


}
