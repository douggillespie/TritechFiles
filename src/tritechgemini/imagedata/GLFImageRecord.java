package tritechgemini.imagedata;

import tritechgemini.fileio.GeminiFileCatalog;

public class GLFImageRecord extends GeminiImageRecord {

	public GLFImageRecord(String filePath, int filePos, int recordIndex) {
		super(filePath, filePos, recordIndex);
	}
	
	public byte m_idChar;
	public int m_version;
	public int m_length;
	public double m_timestamp;
	public int m_dataType;
	public int tm_deviceId;
	public int m_utility;
	public int m_spare;
	
	public int imageVersion;
	public int startRange;
	public int endRange;
	public int rangeCompression;
	public int startBearing;
	public int endBearing;
	public int dataSize;
	public byte[] imageData;
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

	@Override
	public byte[] getImageData() {
		return imageData;
	}

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
		return tm_deviceId;
	}

	@Override
	public void freeImageData() {
		imageData = null;
		bearingTable = null;
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

}
