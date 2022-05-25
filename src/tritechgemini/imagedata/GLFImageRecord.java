package tritechgemini.imagedata;

import tritechgemini.fileio.GLFGenericHeader;
import tritechgemini.fileio.GeminiFileCatalog;

public class GLFImageRecord extends GeminiImageRecord {

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

}
