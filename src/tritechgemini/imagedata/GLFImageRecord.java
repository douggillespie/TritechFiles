package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.fileio.GLFGenericHeader;
import tritechgemini.fileio.GeminiFileCatalog;

public class GLFImageRecord extends GeminiImageRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	public GLFImageRecord(GLFGenericHeader genericHeader, String filePath, int filePos, int recordIndex) {
		super(genericHeader, filePath, filePos, recordIndex);
	}
	
	
	public int imageVersion;
	public int startRange;
	public int endRange;
	public int rangeCompression;
	public int startBearing;
	public int endBearing;
	public int dataSize;
//	public double[] bearingTable;
	
	public int m_uiStateFlags;
	public int m_UiModulationFrequency;
	public float m_fBeamFormAperture;
	public double m_dbTxtime;
	public int m_usPingFlags;
	public float m_sosAtXd;
	public int m_sPercentGain;
	public int m_fChirp;
	public int m_ucSonartype;
	public int m_ucPlatform;
	public byte oneSpare;
	public int dede;

//	@Override
//	public byte[] getImageData() {
//		return imageData;
//	}

	@Override
	public long getRecordTime() {
		return GeminiFileCatalog.cDateToMillis(m_dbTxtime);
	}

	@Override
	public double getMaxRange() {
		// TODO Auto-generated method stub
		return endRange * m_sosAtXd/2. / m_UiModulationFrequency;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeviceId() {
		return genericHeader.tm_deviceId;
	}

	@Override
	public void freeImageData() {
		imageData = null;
//		bearingTable = null;
		isFullyRead = false;
	}

	@Override
	public int getnRange() {
		return endRange-startRange;
	}

	@Override
	public int getnBeam() {
		return endBearing-startBearing;
	}

	@Override
	public double getSoS() {
		return m_sosAtXd;
	}

	@Override
	public GLFImageRecord clone() {
//		try {
			return (GLFImageRecord) super.clone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//			return null;
//		}
	}

	@Override
	public int getChirp() {
		return m_fChirp;
	}

	@Override
	public int getGain() {
		return m_sPercentGain;
	}
	
	/**
	 * Is HF - 1200 only I think. 
	 * @return true if HF - means 1.2MHz.
	 */
	public boolean isHF() {
		return ((m_usPingFlags & 256) != 0);
	}
	
	/**
	 * Get the device type as a string. 
	 * @return
	 */
	public String getDeviceType() {
		switch (m_ucPlatform) {
		case 0:
			return "none";
		case 1:
			return "720is";
		case 2:
			return "720ik";
		case 3:
			return "720im";
		case 4:
			return "1200ik";
		}
		return null;
	}

}
