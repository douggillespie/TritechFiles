package tritechgemini.fileio;

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

public class GLFFileCatalog extends GeminiFileCatalog<GLFImageRecord> {


	private static final long serialVersionUID = 1L;

	private static final int DE = 0xDE;

	private transient Inflater inflater;
	
	private int zippedDataSize = 0;

	private GLFFastInputStream fastInput;

	public GLFFileCatalog(String filePath) {
		super(filePath);
	}

	@Override
	public boolean buildCatalogue(ArrayList<GLFImageRecord> imageRecords) throws Exception {
		InputStream inputStream = findDataInputStream();

		/*
		 * Using a buffered input stream brings down the file read time from 18s to 322 millis (x56 speed up)
		 * i've also tried various combinations of random access files and they are not ideal since they go even 
		 * slower than a basic unbuffered file input stream. 
		 */
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
		CountingInputStream cis = new CountingInputStream(inputStream);
		DataInput dis = new LittleEndianDataInputStream(cis);

		int nRec = 0;
		long t1 = System.currentTimeMillis();
		try {
			while (true) {

				GLFImageRecord glfImage = new GLFImageRecord(getFilePath(), (int) cis.getPos(), nRec);

				boolean ok = readGlfRecord(glfImage, dis, false);
				
				if (!ok) {
					break;
				}

				imageRecords.add(glfImage);
				
				nRec++;

			}
		}
		catch (CatalogException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	boolean loadFullRecord(GLFImageRecord geminiRecord) throws IOException {
		InputStream inputStream;
		if (fastInput != null) {
			fastInput.resetDataStream();
			inputStream = fastInput;
		}
		else {
			inputStream = findDataInputStream();
		}
//		BufferedInputStream bis = new BufferedInputStream(inputStream);
		DataInput dis = new LittleEndianDataInputStream(inputStream);
		dis.skipBytes(geminiRecord.filePos);
		boolean ok = readGlfRecord(geminiRecord, dis, true);
		inputStream.close();
		return ok;
	}

	/**
	 * Read a GLF record which may or may not have already been partially read. 
	 * @param glfImage
	 * @param dis
	 * @param readFully
	 * @return
	 * @throws CatalogException
	 */
	boolean readGlfRecord(GLFImageRecord glfImage, DataInput dis, boolean readFully) throws CatalogException {

//		if (glfImage.getRecordNumber() >= 341) {
//			System.out.println("Record " + glfImage.getRecordNumber());
//			readFully = true;
//		}
		
		try {
			glfImage.m_idChar = dis.readByte();
			glfImage.m_version = dis.readUnsignedByte();
			if (glfImage.m_version == DE || glfImage.m_idChar != 42) {
				return false;
			}
			glfImage.m_length = dis.readInt();
			glfImage.m_timestamp = dis.readDouble();
			glfImage.m_dataType = dis.readUnsignedByte();
			glfImage.tm_deviceId = dis.readUnsignedShort();
			glfImage.m_utility = dis.readUnsignedShort();
			glfImage.m_spare = dis.readShort();

			int imageRec = dis.readUnsignedShort();
			int efef = dis.readUnsignedShort();
			if (efef != 0xEFEF) {
				String err = String.format("Unrecognised byte pattern ox%X  in file\n", efef);
				throw new CatalogException(err);
			}

			glfImage.imageVersion = dis.readUnsignedShort();
			glfImage.startRange = dis.readInt();
			glfImage.endRange = dis.readInt();
			glfImage.rangeCompression = dis.readUnsignedShort();
			glfImage.startBearing = dis.readInt();
			glfImage.endBearing = dis.readInt();
			glfImage.dataSize = dis.readInt();

			int nBearing = glfImage.endBearing-glfImage.startBearing;
			int nRange = glfImage.endRange-glfImage.startRange;
			int iRec = glfImage.recordIndex;
			
			zippedDataSize += glfImage.dataSize;
			if (readFully) {
				// read the image
				byte[] zippedData = new byte[glfImage.dataSize];
				dis.readFully(zippedData);
				try {
					glfImage.imageData = inflateData(zippedData, glfImage.endRange-glfImage.startRange, nBearing);
				}
				catch (DataFormatException dataFormatException) {
					throw new CatalogException("Error unzipping raw data: " + dataFormatException.getMessage());
				}

				// read the bearing table
				glfImage.bearingTable = new double[nBearing];
				for (int i = 0; i < nBearing; i++) {
					glfImage.bearingTable[i] = dis.readDouble();
				}
			}
			else {
				// skip the image and bearing table
				dis.skipBytes(glfImage.dataSize + nBearing*Double.BYTES);
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
				String err = String.format("Unrecognised byte pattern ox%X  in file\n", efef);
				throw new CatalogException(err);
			}
		}
		catch (EOFException eof) {
			return false;
		}
		catch (IOException ioEx) {
			throw (new CatalogException(ioEx.getMessage()));
		}
		return true;
	}
	
	/**
	 * Unzip the data which is in a standard zipped archive format. 
	 * @param zippedData zipped data
	 * @param nRange number of ranges
	 * @param nBearing number of bearings
	 * @return unzipped data. 
	 * @throws DataFormatException Exception thrown by the unzipper. 
	 */
	private byte[] inflateData(byte[] zippedData, int nRange, int nBearing) throws DataFormatException{
		inflater = getInflater();
		inflater.reset();
		inflater.setInput(zippedData);
		int outSize = nRange*nBearing;
		byte[] unzippedData = new byte[outSize];
		int bytesRead = 0;
		bytesRead = inflater.inflate(unzippedData);
		return unzippedData;
	}

	/**
	 * getter for inflater to call to make sure it's there since it's not
	 * serialised. 
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
	 * containing the dat file. The dat file contains the actual data and is the thing
	 * we want to read. Java ZipInputStream provides direct access to this without unpacking
	 * physical files. 
	 * @return Input stream. 
	 * @throws IOException if the input stream cannot be found / opened. 
	 */
	private InputStream findDataInputStream() throws IOException {
		if (fastInput != null) {
			fastInput.resetDataStream();
			return fastInput;
		}
		
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
			}
			else {
				return openZippedinputStream();
			}
		}
		throw new IOException("Input stream unavailable in archive file " + file);
	}
	
	private InputStream openZippedinputStream() throws IOException {
		String filePath = getFilePath().toLowerCase();
		File file = new File(filePath);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
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

}
