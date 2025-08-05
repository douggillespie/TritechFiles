package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.ZipInputStream;


/**
 * Class to write uncompressed zip files since all the ones that are in Java
 * don't see to actually work in any way and always compress the data. 
 * To be used for creating GLF files in same format as Genesis data.  
 * Tested with a single file input. May or may not work with two or more input files. 
 * <p>
 * Format largely described at https://en.wikipedia.org/wiki/ZIP_(file_format)
 * @author dg50
 *
 */
public class UnzippedWriter {

	private static final int ZIPVERSION = 20;
	private LittleEndianDataOutputStream los;
	private tritechgemini.fileio.CountingOutputStream cos;
	private File outFile;
	private File[] dataFiles;
	private CRC32 crc;
	private int fileSize;
	private int compressedSize;
	private int nBlocks;
	String comment = "PAMGuard Writer";
//	String comment = "test comment";

	private static final int BLOCKLEN = 65531;

	public UnzippedWriter() {
	}

	/**
	 * Write a list of files to an uncompressed zip archive. 
	 * @param outputFile output (zipped) file
	 * @param files list of data files. 
	 * @return true if no exceptions
	 * @throws IOException
	 */
	public boolean writeArcive(File outputFile, File... files) throws IOException {
		outFile = outputFile;
		dataFiles = files;
		cos = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		los = new LittleEndianDataOutputStream(cos);

		for (int i = 0; i < files.length; i++) {
			getFileData(files[i]);
			writeFileHeader(files[i]);
			writeFile(files[i]);
			long sigStart = cos.getByteCount();
			writeFileFooter(files[i]);
			long sigEnd = cos.getByteCount();
			writeEndSig(sigStart, sigEnd-sigStart);
		}

		los.close();

		return true;
	}

	/*
	 * Thie size of the file header
	 */
	private int getHeadSize(File dataFile) {
		return 30 + dataFile.getName().length();
	}
	/**
	 * The size of the file footer. 
	 * @param dataFile
	 * @return
	 */
	private int getFootSize(File dataFile) {
		return 46 + dataFile.getName().length() + comment.length();
	}

	/**
	 * Write the header for a file. 
	 * @param dataFile
	 * @throws IOException
	 */
	private void writeFileHeader(File dataFile) throws IOException {
		long now = dataFile.lastModified();
		String name = dataFile.getName();
		los.writeInt((int) ZipInputStream.LOCSIG);
		los.writeShort(ZIPVERSION);
		los.writeShort(0); // bit flag
		los.writeShort(8); // comression
		los.writeShort(getDosTime(now));
		los.writeShort(getDosDate(now));
		los.writeInt((int) crc.getValue()); // CRC
		los.writeInt(compressedSize); // compressed size
		los.writeInt(fileSize); // uncompressedSize
		los.writeShort(name.length());
		los.writeShort(0);
		los.write(name.getBytes("UTF-8"));
	}

	/**
	 * work out some data about the file, such as how long the 
	 * packed data will be and the CRC. This annoyingly requires
	 * an extra pass through the file. 
	 * @param dataFile data file
	 * @throws IOException
	 */
	private void getFileData(File dataFile) throws IOException {
		fileSize = 0;
		crc = new CRC32();
		byte[] data = new byte[65536];
		int bytesRead = 1;
		InputStream dis = new BufferedInputStream(new FileInputStream(dataFile)); 
		while ((bytesRead = dis.read(data)) > 0) {
			crc.update(data, 0, bytesRead);
			fileSize += bytesRead;
		}
		nBlocks = (fileSize + BLOCKLEN-1)/BLOCKLEN;
		compressedSize = (nBlocks+1) * 5 + fileSize;// + getHeadSize(dataFile) + getFootSize(dataFile);

	}

	/**
	 * Write the footer for a file. 
	 * @param dataFile
	 * @throws IOException
	 */
	private void writeFileFooter(File dataFile) throws IOException {
		long now = dataFile.lastModified();
		String name = dataFile.getName();
		los.writeInt((int) ZipInputStream.CENSIG);
		los.writeShort(0);
		los.writeShort(ZIPVERSION);
		los.writeShort(0); // bit flag
		los.writeShort(8); // comression
		los.writeShort(getDosTime(now));
		los.writeShort(getDosDate(now));
		los.writeInt((int) crc.getValue()); // CRC
		los.writeInt((int) compressedSize); // compressed size
		los.writeInt((int) fileSize); // uncompressedSize
		los.writeShort(name.length());
		los.writeShort(0); // ext length
		los.writeShort(comment.length()); // comment length
		los.writeShort(0); // disk number
		los.writeShort(0); // internal file attributes
		los.writeInt(0); // external file attributes
		los.writeInt(0); // relative offset
		los.write(name.getBytes("UTF-8"));
		// no extra data field, otherwise write it here. 
		los.write(comment.getBytes("UTF-8"));

	}

	/**
	 * Write the file data in appropriate sized chunks. 
	 * @param dataFile
	 * @throws IOException
	 */
	private void writeFile(File dataFile) throws IOException {
		byte[] data = new byte[BLOCKLEN];
		fileSize = 0;
		int bytesRead = 1;
		int bMap, spares;
		InputStream dis = new BufferedInputStream(new FileInputStream(dataFile)); 
		int nBlock = 0;
		try {
			while ((bytesRead = dis.read(data)) > 0) {
//				crc.update(data, 0, bytesRead);
				fileSize += bytesRead;
				if (bytesRead < 0) {
					break;
				}
				if (bytesRead < BLOCKLEN) {
					bMap = 0;
//					spares = BLOCKLEN-bytesRead;
				}
				else {
					bMap = 0;
//					spares = 4;
				}
				spares = 65535-bytesRead;
				los.writeByte(bMap);
				los.writeShort(bytesRead);
				los.writeShort(spares);
				los.write(data, 0, bytesRead);
				nBlock++;
				if (bMap == 1) {
					//				break;
//					System.out.println("Near EOF");
				}
			}
			// then write an empty record
			los.writeByte(1);
			los.writeShort(0);
			los.writeShort(65535);
			
		}
		catch (EOFException e) {
//			System.out.println("EOF");
		}
//		System.out.println("Blocks written to archive = " + nBlock);
		dis.close();
	}
	
	/**
	 * Write the file end signature. 
	 * @param sigStart
	 * @param sigLength
	 * @return
	 * @throws IOException
	 */
	private boolean writeEndSig(long sigStart, long sigLength) throws IOException {
		los.writeInt((int) ZipInputStream.ENDSIG);
		los.writeShort(0);
		los.writeShort(0);
		los.writeShort(dataFiles.length);
		los.writeShort(dataFiles.length);
		long totLength = sigLength; 
		los.writeInt((int) totLength);
		los.writeInt((int) sigStart);
		los.writeShort(0);
		
		return true;
	}

	/**
	 * Format a DOS type data number
	 * @param timeMillis
	 * @return short as a DOS date
	 */
	private int getDosDate(long timeMillis) {
		/*
		 * 	MS-DOS Date Format:
			The date format is a 16-bit value with the following structure:
    		Bits 0-4: Day of the month (1-31). 
			Bits 5-8: Month (1 = January, 2 = February, etc.). 
			Bits 9-15: Year offset from 1980. 
		 */
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeMillis);
		int y = c.get(Calendar.YEAR) - 1980;
		int m = c.get(Calendar.MONTH)+1;
		int d = c.get(Calendar.DAY_OF_MONTH);
		int dosDate = d | m<<5 | y<<9;
		return dosDate;
	}

	/**
	 * Format a DOS time number
	 * @param timeMillis 
	 * @return time as a short (2s resolution)
	 */
	private int getDosTime(long timeMillis) {
		/*
		 * 	MS-DOS Time Format:
			The time format is also a 16-bit value with the following structure:
    		Bits 0-4: Number of 2-second intervals. 
			Bits 5-10: Minutes (0-59). 
			Bits 11-15: Hours (0-23). 
		 */
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeMillis);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND)/2;
		int dosTime = s | m<<5 | h<<11;
		return dosTime;
	}
	
	

}
