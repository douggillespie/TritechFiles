package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import tritechgemini.imagedata.GLFImageRecord;
import tritechgemini.imagedata.GLFStatusData;

public class GLFFileCatalog extends GeminiFileCatalog<GLFImageRecord> {

	private static final long serialVersionUID = 2L;

	private static final int DE = 0xDE;

	private transient Inflater inflater;

	private int zippedDataSize = 0;

	private double[] lastBearingTable = { 0. };

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
//		System.out.println("Finalise GLFFileCatalog");
	}

	private GLFFastInputStream fastInput;

	private volatile boolean continueStream;

	public GLFFileCatalog(String filePath) {
		super(filePath);
	}

	@Override
	public boolean buildCatalogue(ArrayList<GLFImageRecord> imageRecords) throws Exception {
		InputStream inputStream = findDataInputStream();

		/*
		 * Using a buffered input stream brings down the file read time from 18s to 322
		 * millis (x56 speed up) i've also tried various combinations of random access
		 * files and they are not ideal since they go even slower than a basic
		 * unbuffered file input stream.
		 */
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
		CountingInputStream cis = new CountingInputStream(inputStream);
		DataInput dis = new LittleEndianDataInputStream(cis);

		int nRec = 0;
		long t1 = System.currentTimeMillis();
		int badRec = 0;
		try {
			while (true) {

				GLFGenericHeader header = readNextHeader(dis);
				if (header == null) {
					break; // should be EOF.
				}
				if (header.m_idChar != 42) {
					System.out.printf("Bad header id character in GLF: %d\n", header.m_idChar);
				}

//				long p1 = cis.getPos();
				switch (header.m_dataType) {
				case 0: // image record
					GLFImageRecord glfImage = new GLFImageRecord(header, getFilePath(), (int) cis.getPos(), nRec);
					int ok = readGlfRecord(glfImage, dis, false);
					if (ok == 0) {
						imageRecords.add(glfImage);
						nRec++;
					}
					break;
				case 3: // status
					GLFStatusData statusData = new GLFStatusData(header);
					statusData.read(dis, false);
				}

			}
		} catch (CatalogException e) {
			e.printStackTrace();
		}
		if (badRec > 0) {
			System.out.println("Incomprehensible records records in file are " + badRec);
		}

		return true;
	}

	/**
	 * Read the next generic header object from the file.
	 * 
	 * @param dis
	 * @return Generic header or thrown an exception.
	 * @throws CatalogException
	 */
	private GLFGenericHeader readNextHeader(DataInput dis) throws CatalogException {
		GLFGenericHeader header = new GLFGenericHeader();
		try {
			header.m_idChar = dis.readByte();
			header.m_version = dis.readUnsignedByte();
			// if (glfImage.m_version == DE || glfImage.m_idChar != 42) {
			// return false;
			// }
			header.m_length = dis.readInt();
			header.m_timestamp = dis.readDouble();
			header.m_dataType = dis.readUnsignedByte(); // getting a datatype 3, which is not image data. FFS.
			header.tm_deviceId = dis.readUnsignedShort();
			header.m_node_ID = dis.readUnsignedShort();
			header.m_spare = dis.readShort();
		} catch (EOFException eof) {
			return null;
		} catch (IOException ioEx) {
			throw (new CatalogException(ioEx.getMessage()));
		}
		return header;
	}

	@Override
	boolean loadFullRecord(GLFImageRecord geminiRecord) throws IOException {
		InputStream inputStream;
		if (fastInput != null) {
			fastInput.resetDataStream();
			inputStream = fastInput;
		} else {
			inputStream = findDataInputStream();
		}
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
		LittleEndianDataInputStream dis = new LittleEndianDataInputStream(inputStream);
		dis.skip(geminiRecord.filePos);
		boolean ok = readGlfRecord(geminiRecord, dis, true) == 0;
		inputStream.close();
		return ok;
	}

	/**
	 * Read a GLF record which may or may not have already been partially read.
	 * 
	 * @param glfImage
	 * @param dis
	 * @param readFully
	 * @return 0 for a normal GLF record, 1 for file end, 2 for something else we
	 *         didn't understand
	 * @throws CatalogException
	 */
	public int readGlfRecord(GLFImageRecord glfImage, DataInput dis, boolean readFully) throws CatalogException {

//		if (glfImage.getRecordNumber() >= 341) {
//			System.out.println("Record " + glfImage.getRecordNumber());
//			readFully = true;
//		}

		try {
			// end of standard header section.

			int imageRec = dis.readUnsignedShort();
			int efef = dis.readUnsignedShort();
			if (efef != 0xEFEF) {
				String err = String.format("Unrecognised (a) byte pattern ox%X  in file\n", efef);
				if (true) {
//					System.out.printf("Problem at record %d type %d with %d bytes\n", glfImage.recordIndex, glfImage.m_dataType, glfImage.m_length);
					dis.skipBytes(214);
					return 2;
				}
//				throw new CatalogException(err);
				// find DEDE and bomb.
				int nBytesJunk = 0;
				int prev = 0;
				while (true) {
					int nxt = dis.readUnsignedByte();
//					System.out.printf("%d 0X%X\n", nBytesJunk, nxt);
					nBytesJunk++;
					if (nxt != 0 && nxt == prev) {
						System.out.printf("fByte match for dataType %d after %d more bytes: 0x%X\n",
								glfImage.genericHeader.m_dataType, nBytesJunk, nxt);
					}
					if (nxt == 42) {
						System.out.printf("Found %c after %d bytes\n", nxt, nBytesJunk);
					}
					if (nxt == 0xDE && prev == 0xDE) {
						/*
						 * this might be a record end, but might not be ! I think it's actually the end
						 * of the record after and that whatever this thing is, it's
						 */
//						System.out.printf("found length of record for dataType %d after %d more bytes\n", glfImage.m_dataType, nBytesJunk);
						return 2;
					}
					prev = nxt;
				}
			}

			glfImage.imageVersion = dis.readUnsignedShort();
			glfImage.startRange = dis.readInt();
			glfImage.endRange = dis.readInt();
			glfImage.rangeCompression = dis.readUnsignedShort();
			glfImage.startBearing = dis.readInt();
			glfImage.endBearing = dis.readInt();

			if (glfImage.imageVersion == 3) {
				// two extra bytes in imageVersion 3.
				int fKnows = dis.readShort();
			}

			glfImage.dataSize = dis.readInt();

			int nBearing = glfImage.endBearing - glfImage.startBearing;
			int nRange = glfImage.endRange - glfImage.startRange;
			int iRec = glfImage.recordIndex;

			zippedDataSize += glfImage.dataSize;
			if (readFully) {
				int expSize = (glfImage.endBearing - glfImage.startBearing) * (glfImage.endRange - glfImage.startRange);
				if (expSize == glfImage.dataSize) {
					// it's not zipped
					byte[] data = new byte[expSize];
					dis.readFully(data);
					glfImage.setImageData(data);
				} else { // it is zipped
							// read the image
					byte[] zippedData = new byte[glfImage.dataSize];
					dis.readFully(zippedData);
					try {
						byte[] data = inflateData(zippedData, glfImage.endRange - glfImage.startRange, nBearing);
						glfImage.setImageData(data);
					} catch (DataFormatException dataFormatException) {
						throw new CatalogException("Error unzipping raw data: " + dataFormatException.getMessage());
					}
				}

				// read the bearing table
				if (nBearing == lastBearingTable.length) {
					glfImage.bearingTable = lastBearingTable;
					dis.skipBytes(nBearing * Double.BYTES);
				} else {
					glfImage.bearingTable = new double[nBearing];
					for (int i = 0; i < nBearing; i++) {
						glfImage.bearingTable[i] = dis.readDouble();
					}
					lastBearingTable = glfImage.bearingTable;
				}
			} else {
				// skip the image and bearing table
				dis.skipBytes(glfImage.dataSize + nBearing * Double.BYTES);
//				dis.skipBytes(glfImage.dataSize);
			}
			glfImage.m_uiStateFlags = dis.readInt();
			glfImage.m_UiModulationFrequency = dis.readInt();
			glfImage.m_fBeamFormAperture = dis.readFloat();
			glfImage.m_dbTxtime = dis.readDouble();
			glfImage.m_usPingFlags = dis.readUnsignedShort();
			glfImage.m_sosAtXd = dis.readFloat();
			glfImage.m_sPercentGain = dis.readUnsignedShort();
			glfImage.m_fChirp = dis.readUnsignedByte();
			glfImage.m_ucSonartype = dis.readUnsignedByte();
			glfImage.m_ucPlatform = dis.readUnsignedByte();
			glfImage.oneSpare = dis.readByte();
			glfImage.dede = dis.readUnsignedShort();
			if (glfImage.dede != 0xDEDE) {
				String err = String.format("Unrecognised (c) byte pattern ox%X  in file\n", glfImage.dede);
				throw new CatalogException(err);
			}
		} catch (EOFException eof) {
			return 1;
		} catch (IOException ioEx) {
			throw (new CatalogException(ioEx.getMessage()));
		}
		return 0;
	}

	/**
	 * Unzip the data which is in a standard zipped archive format.
	 * 
	 * @param zippedData zipped data
	 * @param nRange     number of ranges
	 * @param nBearing   number of bearings
	 * @return unzipped data.
	 * @throws DataFormatException Exception thrown by the unzipper.
	 */
	private byte[] inflateData(byte[] zippedData, int nRange, int nBearing) throws DataFormatException {
		inflater = getInflater();
		inflater.reset();
		inflater.setInput(zippedData);
		int outSize = nRange * nBearing;
		byte[] unzippedData = new byte[outSize];
		int bytesRead = 0;
		bytesRead = inflater.inflate(unzippedData);
		return unzippedData;
	}

	/**
	 * getter for inflater to call to make sure it's there since it's not
	 * serialised.
	 * 
	 * @return inflater
	 */
	private Inflater getInflater() {
		if (inflater == null) {
			inflater = new Inflater();
		}
		return inflater;
	}

	/**
	 * Find the input stream. normally it's a GLF file, which is a zipped archive
	 * containing the dat file. The dat file contains the actual data and is the
	 * thing we want to read. Java ZipInputStream provides direct access to this
	 * without unpacking physical files. The GLFFastInputStream is a hack of the glf
	 * file which indexes it and can then quickly access any record using something
	 * close to random file access which is useful when processing offline.
	 * 
	 * @return Input stream.
	 * @throws IOException if the input stream cannot be found / opened.
	 */
	private InputStream findDataInputStream() throws IOException {
//		if (fastInput != null) {
//			fastInput.resetDataStream();
//			return fastInput;
//		}

		String filePath = getFilePath().toLowerCase();
		File file = new File(filePath);
		boolean glf = filePath.endsWith(".glf");
		boolean dat = filePath.endsWith(".dat");
		if (dat) {
			return new FileInputStream(file);
		}
		if (glf) {
			fastInput = new GLFFastInputStream(file);
			if (fastInput.isOk) {
				fastInput.resetDataStream();
				return fastInput;
			} else {
				return openZippedinputStream();
			}
		}
		throw new IOException("Input stream unavailable in archive file " + file);
	}

	private InputStream openZippedinputStream() throws IOException {
		String filePath = getFilePath().toLowerCase();
		File file = new File(filePath);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			String entryName = zipEntry.getName();
			if (entryName.endsWith(".dat")) {
				return zis;
			}
			zipEntry = zis.getNextEntry();
		}

		throw new IOException("Input stream unavailable in archive file " + file);
	}

	@Override
	public int streamCatalog(CatalogStreamObserver streamObserver) throws CatalogException {

		continueStream = true;

		InputStream inputStream;
		try {
//			inputStream = findDataInputStream();
			// no point with dealing with the random access methods here so just use normal
			// zipped reader
			inputStream = openZippedinputStream();
		} catch (IOException e1) {
			throw new CatalogException(e1.getMessage());
		}

		/*
		 * Using a buffered input stream brings down the file read time from 18s to 322
		 * millis (x56 speed up) i've also tried various combinations of random access
		 * files and they are not ideal since they go even slower than a basic
		 * unbuffered file input stream.
		 */
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
		CountingInputStream cis = new CountingInputStream(inputStream);
		DataInput dis = new LittleEndianDataInputStream(cis);

		int nRec = 0;
		long t1 = System.currentTimeMillis();
		int badRec = 0;
		while (continueStream) {

			GLFGenericHeader header = readNextHeader(dis);
			if (header == null) {
				break; // should be EOF.
			}
			if (header.m_idChar != 42) {
				System.out.printf("Bad header id character in GLF: %d\n", header.m_idChar);
			}

//				long p1 = cis.getPos();
			switch (header.m_dataType) {
			case 0: // image record
				GLFImageRecord glfImage = new GLFImageRecord(header, getFilePath(), (int) cis.getPos(), nRec);
				int ok = readGlfRecord(glfImage, dis, true);
				if (ok == 0) {
//					imageRecords.add(glfImage);
					streamObserver.newImageRecord(glfImage);
					nRec++;
				}
				break;
			case 3: // status
				GLFStatusData statusData = new GLFStatusData(header);
				statusData.read(dis, false);
				streamObserver.newStatusData(statusData);
			}

		}

		return nRec;
	}

	@Override
	public void stopCatalogStream() {
		continueStream = false;
	}

}
