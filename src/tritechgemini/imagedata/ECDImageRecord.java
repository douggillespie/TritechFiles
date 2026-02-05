package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.fileio.GeminiAcousticZoom;
import tritechgemini.fileio.GeminiFileCatalog;
import tritechgemini.fileio.GeminiPingTail;

public class ECDImageRecord extends GeminiImageRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int TYPE_SENSOR_RECORD = 1;
	public static final int TYPE_TARGET_RECORD = 2;
	public static final int TYPE_TARGET_IMAGE_RECORD = 3;
	public static final int TYPE_PING_TAIL_RECORD = 4;
	public static final int TYPE_ACOUSTIC_ZOOM_RECORD = 5;

	public static final int VER_SENSOR_RECORD = 0xEFEF;
	public static final int VER_TARGET_RECORD = 0xDFDF;
	public static final int VER_TARGET_IMAGE_RECORD = 0xCFCF;
	public static final int VER_PING_TAIL_RECORD = 0xBFBF;
	public static final int VER_ACOUSTIC_ZOOM_RECORD = 0xAFAF;

	public static final int END_TAG = 0xDEDE;
	
	public static final int HALF_END_TAG = 0xDE;
	
	public short m_version;
	public int m_pid;
	public int m_halfArr;
	public int m_txLength;
	public int m_scanRate;
	public float m_sosAtXd;
	public short m_shading;
	public short m_mainGain;
	public short m_gainBlank;
	public short m_adcInput;
	public short m_spreadGain;
	public short m_absorbGain;
	public int m_bfFocus;
	public short m_bfGain;
	public float m_bfAperture;
	public short m_txStart;
	public short m_txLen;
	public float m_txRadius;
	public float m_txRng;
	public int m_modFreq;
	public float m_sosAtXd_2;
	public short m_rx1;
	public short m_rx2;
	public short m_tx1;
	public short m_pingFlags;
	public int m_rx1Arr;
	public int m_rx2Arr;
	public int m_tx1Arr;
	public int m_tx2Arr;
	public int m_tid;
	public int m_pid2;
	public double m_txTime;
	public double m_endTime;
	public double m_txAngle;
	public double m_sosAvg;
	public int mask;
	public int m_bpp;
	public int m_nRngs;
	public int m_b0;
	public int m_b1;
	public int m_r0;
	public int m_r1;
	public int dual;
	public int m_nBrgs;
	public int m_Brgs_2;
	public byte[] cData; // compressed data
	public int sCount; // length of compressed data

	private GeminiAcousticZoom acousticZoom;
		
	public ECDImageRecord(String filePath, int filePos, int recordNumber) {
		super(null, filePath, filePos, recordNumber);
	}
	
	/**
	 * check for consistency between record type and version numbers
	 * @param type record type
	 * @param ver record version
	 * @return OK if recognised pair of values
	 */
	public static boolean checkTypeVersion(int type, int ver) {
		switch (type) {
		case TYPE_SENSOR_RECORD:
			return ver == VER_SENSOR_RECORD;
		case TYPE_TARGET_RECORD:
			return ver == VER_TARGET_RECORD;
		case TYPE_TARGET_IMAGE_RECORD:
			return ver == VER_TARGET_IMAGE_RECORD;
		case TYPE_PING_TAIL_RECORD:
			return ver == VER_PING_TAIL_RECORD;
		case TYPE_ACOUSTIC_ZOOM_RECORD:
			return ver == VER_ACOUSTIC_ZOOM_RECORD;
		}
		return false;
	}
	
	/**
	 * Get the maximum range using eq' provided by Phil. 
	 * @return Max range 
	 */
	public double getMaxRange() {
		/**
(CTgtImg->m_nRngs * (CPing->m_sosAtXD/2.0) / CPing->m_modFreq
I would try to avoid using the PingTail Extension record, unless you think there is something vital in there.
		 */
		return getnRange() * (m_sosAtXd/2.)/m_modFreq;
	}

	@Override
	public int getSonarType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarPlatform() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarIndex() {
		/**
		 * THis seems OK for glf data, but for ECD data this is often giving a big
		 * number, which mashes the system. For now set at 0 so we can work, but beaware 
		 * that this will mess and ECD analysis with multiple sonars.  
		 */
		return 0;// m_pid2 & 0x255;
	}

	@Override
	public int getDeviceId() {
		return m_rx1;
	}

	public void setPingTail(GeminiPingTail pingTail) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set the acoustic zoom data which always follows the main image record in an ECD file. 
	 * @param acousticZoom
	 */
	public void setAcousticZoom(GeminiAcousticZoom acousticZoom) {
		this.acousticZoom = acousticZoom;
	}
	
	public byte[] uncompressData() {
		int m_dataSize = m_nBrgs*m_nRngs*m_bpp;
		byte[] pData = new byte[m_nBrgs*m_nRngs*m_bpp]; // have to use short ?
		//		byte[] pBlockLn = cData;
		if (cData == null) {
			return null;
		}
		int size = cData.length; // size of input data
		int iC = 0, iU = 0;
		int nZeros = 0;
		byte maxByte = (byte) 0xFF;
		int posPix = 0;

		while ( iC < size && iU < m_dataSize)
		{
			if (cData[iC] == 0 && iC < (size - 1))
			{
				iC++;
				nZeros = Byte.toUnsignedInt(cData[iC++]);
				if (nZeros == maxByte)
					if (cData[iC] == maxByte)
					{
						iC++;
					}
				while (nZeros != 0 && iU < m_dataSize)
				{
					pData[iU++] = 0;
					nZeros--;
				}
			}
			else if (cData[iC] == 1)
			{
				iC++;
				pData[iU++] = 0;
			}
			else
			{
				pData[iU++] = cData[iC++];
				posPix++;
			}
		}
		

		return pData;
	}

	@Override
	public byte[] getImageData() {
		if (imageData != null) {
			return imageData;
		}
		if (cData == null) {
			return null;
		}
		imageData = uncompressData();
		return imageData;
	}


	@Override
	public void freeImageData() {
		super.freeImageData();
		cData = null;
		imageData = null;
		//	  bearingTable = null;
	}

	@Override
	public int getnRange() {
		return m_nRngs;
	}

	@Override
	public int getnBeam() {
		return m_b1-m_b0;
	}

	@Override
	public double getSoS() {
		return m_sosAtXd;
	}

	@Override
	public long getRecordTime() {
		return GeminiFileCatalog.cDateToMillis(m_txTime);
	}

	@Override
	public ECDImageRecord clone() {
			return (ECDImageRecord) super.clone();
	}

	@Override
	public int getChirp() {
		if (acousticZoom == null) {
			return -1;
		}
		else {
			return acousticZoom.m_chirp;
		}
	}

	@Override
	public int getGain() {
		return m_mainGain;
	}
}
