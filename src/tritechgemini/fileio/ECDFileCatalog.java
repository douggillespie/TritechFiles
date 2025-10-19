package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import tritechgemini.imagedata.ECDImageRecord;

public class ECDFileCatalog extends GeminiFileCatalog<ECDImageRecord> {

	private static final long serialVersionUID = 1L;

	public static final int HEAD_VERSION = 0x0F0F;

	public static final String HEAD_MSG = "Main Data";

	public static final int MASK_DEFAULT = 0x000C;
	public static final int MASK_TIS_QI = 0x01;
	public static final int MASK_TIS_PA = 0x02;
	public static final int MASK_TIS_SCAN = 0x04;
	public static final int MASK_TIS_RB = 0x08;
	public static final int MASK_TIS_TGT = 0x10;
	public static final int MASK_TIS_XYZ = 0x20;
	public static final int MASK_TIS_SHRP = 0x40;
	public static final int MASK_TIS_ = 0x80;

	private int head_ver;

	private int end_inf;

	private String head_msg;

	private double[] lastBearingTable = { 0. };

	private volatile boolean continueStream;

	public ECDFileCatalog(String filePath) {
		super(filePath);
	}

	@Override
	public boolean buildCatalogue(ArrayList<ECDImageRecord> imageRecords) throws Exception {
		try {
			File ecdFile = new File(getFilePath());
			FileInputStream fis = new FileInputStream(ecdFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			CountingInputStream cis = new CountingInputStream(bis);
			DataInput dis = new LittleEndianDataInputStream(cis);

			this.head_ver = dis.readInt();
			this.end_inf = dis.readInt();
			this.head_msg = readUnicodeString(dis, 9);

			ECDImageRecord ecdRecord = null;

			int frameNumber = 0;
			while (true) {
				long filePos = cis.getPos();
				int type = dis.readUnsignedShort(); // first record is TARGET_IMAGE_RECORD
				if (type == ECDImageRecord.END_TAG) {
					break;
				}
				int ver = dis.readUnsignedShort();
				// System.out.printf("Reading type %d version %d\n", type, ver);
				boolean ok = ECDImageRecord.checkTypeVersion(type, ver);
				if (ok == false) {
					break;
				}
				switch (type) {
				case ECDImageRecord.TYPE_SENSOR_RECORD:
					// readSensorRecord(ecdFile, type, ver, dis);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_TARGET_RECORD:
					// readSensorRecord(ecdFile, type, ver, dis);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_TARGET_IMAGE_RECORD:
					ecdRecord = new ECDImageRecord(getFilePath(), (int) cis.getPos(), frameNumber++);
					boolean recOK = readTargetImageRecord(ecdRecord, type, ver, dis, false);
					if (recOK) {
						imageRecords.add(ecdRecord);
					}

					// System.out.println("Read target image record " + nImage);
					// }
					break;
				case ECDImageRecord.TYPE_PING_TAIL_RECORD:
					GeminiPingTail pingTail = readPingTailRecord(ecdFile, type, ver, dis);
					ecdRecord.setPingTail(pingTail);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_ACOUSTIC_ZOOM_RECORD:
					GeminiAcousticZoom acousticZoom = readAcousticZoomRecord(ecdFile, type, ver, dis);
					ecdRecord.setAcousticZoom(acousticZoom);
					gotoNextEndTag(dis);
					break;
				default:
					System.err.printf("Unknown gemini record type %d version %d in file %s\n", type, ver,
							ecdFile.getAbsolutePath());
				}
			}

			fis.close();

		} catch (IOException e) {
			throw e;
		}

		return true;
	}

	private void readSensorRecord(File ecdFile, int type, int ver, DataInput dis) {
		try {
			short dfdf = dis.readShort();
			short ping_version = dis.readShort();
			short ping_pid = dis.readByte();
			int ping_halfArr = dis.readInt();
			byte ping_txLength = dis.readByte();
			byte ping_scanRate = dis.readByte();
			float ping_sos = dis.readFloat();

			short m_tid = dis.readShort();
			short m_pid = dis.readShort();
			double m_txTime = dis.readDouble();
			double m_txAngle = dis.readDouble();
			double m_sosAvg = dis.readDouble();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean loadFullRecord(ECDImageRecord geminiRecord) throws IOException {
		File ecdFile = new File(getFilePath());
		if (ecdFile.exists() == false) {
			return false;
		}
		FileInputStream fis = new FileInputStream(ecdFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		CountingInputStream cis = new CountingInputStream(bis);
		DataInput dis = new LittleEndianDataInputStream(cis);
		dis.skipBytes(geminiRecord.filePos);
		boolean ok = readTargetImageRecord(geminiRecord, 0, 0, dis, true);
		fis.close();

		return ok;
	}

	private boolean readTargetImageRecord(ECDImageRecord ecdRecord, int type, int ver, DataInput dis, boolean readFully)
			throws IOException {
		// start of record CPing
		ecdRecord.m_version = dis.readShort(); // def 0
		ecdRecord.m_pid = dis.readUnsignedByte(); // unique id for ping
		boolean notSuperQuick = true;
		if (notSuperQuick) {
			ecdRecord.m_halfArr = dis.readInt(); // 4
			ecdRecord.m_txLength = dis.readUnsignedByte();
			ecdRecord.m_scanRate = dis.readUnsignedByte(); // 6
			ecdRecord.m_sosAtXd = dis.readFloat(); // 10
			ecdRecord.m_shading = dis.readShort(); // 12
			ecdRecord.m_mainGain = dis.readShort();
			ecdRecord.m_gainBlank = dis.readShort();
			ecdRecord.m_adcInput = dis.readShort();
			ecdRecord.m_spreadGain = dis.readShort();
			ecdRecord.m_absorbGain = dis.readShort(); // 22
			ecdRecord.m_bfFocus = dis.readInt();
			ecdRecord.m_bfGain = dis.readShort(); // 28
			ecdRecord.m_bfAperture = dis.readFloat(); // 32
			ecdRecord.m_txStart = dis.readShort();
			ecdRecord.m_txLen = dis.readShort(); // correct to here. // 36
			ecdRecord.m_txRadius = dis.readFloat(); // 40
			ecdRecord.m_txRng = dis.readFloat();
			ecdRecord.m_modFreq = dis.readInt(); // 48
		} else {
			dis.skipBytes(48);
		}
		short m_numBeams = dis.readShort(); // need this !
		if (m_numBeams == 0) {

		}
		if (notSuperQuick) {
			ecdRecord.m_sosAtXd_2 = dis.readFloat(); // speed of sound
			ecdRecord.m_rx1 = dis.readShort();
			ecdRecord.m_rx2 = dis.readShort(); // 58
			ecdRecord.m_tx1 = dis.readShort();
			ecdRecord.m_pingFlags = dis.readShort();
			ecdRecord.m_rx1Arr = dis.readUnsignedByte();
			ecdRecord.m_rx2Arr = dis.readUnsignedByte();
			ecdRecord.m_tx1Arr = dis.readUnsignedByte();
			ecdRecord.m_tx2Arr = dis.readUnsignedByte();
			// End of data from CPing
			ecdRecord.m_tid = dis.readUnsignedShort(); // From CTgtRec
		} else {
			dis.skipBytes(18);
		}
		ecdRecord.m_pid2 = dis.readUnsignedShort(); // oscillates between 2 and 1. Is this the sonar number ?
		ecdRecord.m_txTime = dis.readDouble(); // typical 1.2894707737033613E9
		ecdRecord.m_endTime = dis.readDouble();
		// ecdRecord.recordTimeMillis = cDateToMillis(ecdRecord.m_txTime);
		// double dt = m_endTime-m_txTime; // comes out at 0 every time.
		if (notSuperQuick) {
			ecdRecord.m_txAngle = dis.readDouble();
			ecdRecord.m_sosAvg = dis.readDouble(); // End of data from CTgtRec - looks like a reasonable value for speed
													// of sound
			ecdRecord.mask = dis.readInt(); // From CTgtImg
			ecdRecord.m_bpp = dis.readUnsignedByte();
			ecdRecord.m_nRngs = dis.readInt(); // 25 this changes with set range. On old Gemini's 1m=122, 5.1m=677
												// 101m=1545 , New gemini 50.1m 763 ranges.
			ecdRecord.m_b0 = dis.readInt();
			ecdRecord.m_b1 = dis.readInt();
			ecdRecord.m_r0 = dis.readInt();
			ecdRecord.m_r1 = dis.readInt();
			ecdRecord.dual = dis.readInt();
			ecdRecord.m_nBrgs = dis.readInt();
		} else {
			dis.skipBytes(49);
		}
		if (readFully) {
			/**
			 * Bearing tables are (nearly) always the same, so don't bother reading them.
			 */
			if (lastBearingTable != null && lastBearingTable.length == m_numBeams) {
				ecdRecord.bearingTable = lastBearingTable;
				dis.skipBytes(m_numBeams * Double.BYTES);
			} else {
				ecdRecord.bearingTable = new double[m_numBeams];
				lastBearingTable = ecdRecord.bearingTable;
				for (int i = 0; i < m_numBeams; i++) {
					/*
					 * Sweet - clearly OK at this point since I get to read an array of 512 angles
					 * in radians that goes from +60 deg to -60 deg..
					 */
					ecdRecord.bearingTable[i] = dis.readDouble();
				}
			}
		} else {
			dis.skipBytes(m_numBeams * Double.BYTES);
		}
		ecdRecord.m_Brgs_2 = dis.readInt();
		int cSize = dis.readInt();
		if (readFully) {
			ecdRecord.cData = new byte[cSize];
			dis.readFully(ecdRecord.cData);
		} else {
			dis.skipBytes(cSize);
		}
		ecdRecord.sCount = dis.readInt();
		int tag = dis.readUnsignedShort(); // correct tag for end of frame!

		return checkBadRecord(ecdRecord);
	}

	/**
	 * Some ECD files seem corrupt. Do a few checks of number of bearings and ranges
	 * and check they are sensible. It may be quite hard to identify these records
	 * though.
	 * 
	 * @param ecdRecord
	 * @return true if the record is OK, of false if it's suspect.
	 */
	private boolean checkBadRecord(ECDImageRecord ecdRecord) {
		if (ecdRecord.m_nBrgs <= 0) {
			return false;
		}
		if (ecdRecord.m_nRngs <= 0) {
			return false;
		}
		if (ecdRecord.m_sosAvg < 1000 || ecdRecord.m_sosAvg > 2000) {
			return false;
		}
		return true;
	}

	private static GeminiPingTail readPingTailRecord(File ecdFile, int type, int ver, DataInput dis)
			throws IOException {
		GeminiPingTail pingTail = new GeminiPingTail(ecdFile, type, ver);
		boolean ok = pingTail.readDataFile(dis);
		return ok ? pingTail : null;

	}

	private static GeminiAcousticZoom readAcousticZoomRecord(File ecdFile, int type, int ver, DataInput dis)
			throws IOException {
		GeminiAcousticZoom acousticZoom = new GeminiAcousticZoom();
		boolean ok = acousticZoom.readDataFile(dis);
		return ok ? acousticZoom : null;
	}

	private static String readUnicodeString(DataInput dis, int nChar) throws IOException {
		byte[] bytes = new byte[nChar * 2];
		dis.readFully(bytes);
		return new String(bytes, Charset.forName("UTF_16LE"));
	}

	/**
	 * work through file byte at a time until next endflag is found. These occurr at
	 * end of every record.
	 * 
	 * @param dis
	 */
	private int gotoNextEndTag(DataInput dis) {
		int prevByte = 0;
		int nRead = 0;
		try {
			while (true) {
				int aByte = dis.readUnsignedByte();
				nRead++;
				if (aByte == ECDImageRecord.HALF_END_TAG && prevByte == ECDImageRecord.HALF_END_TAG) {
					break;
				}
				prevByte = aByte;
			}

		} catch (IOException e) {
			System.out.println("Error in ECD gogNextEndTag: " + e.getLocalizedMessage());
		}
		return nRead;
	}

	@Override
	public CatalogStreamSummary streamCatalog(CatalogStreamObserver streamObserver) throws CatalogException {
		int frameNumber = 0;
		continueStream = true;
		int recordsRead = 0;
		long firstRecordTime = 0, lastRecordTime = 0;
		try {
			File ecdFile = new File(getFilePath());
			FileInputStream fis = new FileInputStream(ecdFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			CountingInputStream cis = new CountingInputStream(bis);
			DataInput dis = new LittleEndianDataInputStream(cis);

			this.head_ver = dis.readInt();
			this.end_inf = dis.readInt();
			this.head_msg = readUnicodeString(dis, 9);

			ECDImageRecord ecdRecord = null;

			while (continueStream) {
				long filePos = cis.getPos();
				int type = dis.readUnsignedShort(); // first record is TARGET_IMAGE_RECORD
				if (type == ECDImageRecord.END_TAG) {
					break;
				}
				int ver = dis.readUnsignedShort();
				// System.out.printf("Reading type %d version %d\n", type, ver);
				boolean ok = ECDImageRecord.checkTypeVersion(type, ver);
				if (ok == false) {
					break;
				}
				switch (type) {
				case ECDImageRecord.TYPE_SENSOR_RECORD:
					// readSensorRecord(ecdFile, type, ver, dis);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_TARGET_RECORD:
					// readSensorRecord(ecdFile, type, ver, dis);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_TARGET_IMAGE_RECORD:
					ecdRecord = new ECDImageRecord(getFilePath(), (int) cis.getPos(), frameNumber++);
					boolean recOK = readTargetImageRecord(ecdRecord, type, ver, dis, true);
					// imageRecords.add(ecdRecord);
					if (recOK) {
						recordsRead++;
						if (firstRecordTime == 0) {
							firstRecordTime = ecdRecord.getRecordTime();
						}
						lastRecordTime = ecdRecord.getRecordTime();
						continueStream = streamObserver.newImageRecord(ecdRecord);
					}

					// System.out.println("Read target image record " + nImage);
					// }
					break;
				case ECDImageRecord.TYPE_PING_TAIL_RECORD:
					GeminiPingTail pingTail = readPingTailRecord(ecdFile, type, ver, dis);
					ecdRecord.setPingTail(pingTail);
					gotoNextEndTag(dis);
					break;
				case ECDImageRecord.TYPE_ACOUSTIC_ZOOM_RECORD:
					GeminiAcousticZoom acousticZoom = readAcousticZoomRecord(ecdFile, type, ver, dis);
					ecdRecord.setAcousticZoom(acousticZoom);
					gotoNextEndTag(dis);
					break;
				default:
					System.err.printf("Unknown Gemini record type %d version %d in file %s\n", type, ver,
							ecdFile.getAbsolutePath());
				}
			}

			fis.close();

		} catch (IOException e) {
			throw new CatalogException(e.getMessage());
		}

		return new CatalogStreamSummary(recordsRead, firstRecordTime, lastRecordTime,
				continueStream ? CatalogStreamSummary.FILEEND : CatalogStreamSummary.PROCESSSTOP);
	}

	@Override
	public void stopCatalogStream() {
		continueStream = false;
	}

	@Override
	protected void checkDeserialisedCatalog(String filePath2) {
		// TODO Auto-generated method stub
		
	}

}
