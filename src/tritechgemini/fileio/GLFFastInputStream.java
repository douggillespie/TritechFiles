package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Fast reading of uncompressed GLF files. GLF files are a zip archive of 
 * sonar data with the main .dat data file and one or two configuration files.
 * so far as I can tell, only the .dat file is important. This class makes it 
 * possible to read the dat file from the GLF file without actually unzipping
 * the archive.  
 * @author dg50
 *
 */
public class GLFFastInputStream extends InputStream implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private transient LittleEndianDataInputStream glfInputStream;
	private transient CountingInputStream countingInputStream;

	private GLFFastData glfFastData;

	protected boolean isOk;
	
	private File glfFile;
		
	private int currentLoadedBlock = 0;
	
	/**
	 * length of header on each block.
	 */
	private static final int BLOCKHEADLENGTH = 5;
	
	public static CPUMonitor skipMonitor = new CPUMonitor();
	public static CPUMonitor readMonitor = new CPUMonitor();
	public static CPUMonitor loadMonitor = new CPUMonitor();
	
	
	/**
	 * Data for the current uncompressed block. 
	 */
	private byte[] currentBlockData = null; //new byte[BLOCKDATALENGTH];
	/**
	 * Byte number for the first byte in currentblockData relative
	 * to the true position of the virtual file. 
	 */
	private long blockStartByte;
	
	/**
	 * End byte for the current block = blockStartByte+currentBlockLength
	 * so we can only read up to blockEndByte-1;
	 */
	private long blockEndByte;
	/**
	 * Length of the current block. 
	 */
	private int currentBlockLength;
	
	/**
	 * Current absolute position of the pointer in the virtual file. 
	 * 
	 */
	private long currentAbsPos;
	
	private long totalFileBytes;
	

	public GLFFastInputStream(File glfFile) throws FileNotFoundException {
		super();
		this.glfFile = glfFile;
		
		openInputStream();
		
		/**
		 * New system 2022-08-03. Keep all the important index
		 * information in a separate serializable object and write / load
		 * from file after it's first generates to speed this up. With SSD
		 * laptop this speeds the indexing of the glf file from 3s to about 15ms
		 * so makes scrolling a lot smoother when jumping to next file. 
		 */
//		long t1 = System.nanoTime();
//		boolean makeNew = false;
		glfFastData = loadGlfFastData();
		isOk = (glfFastData != null);
		if (glfFastData == null) {
			isOk = createGlfFastInput();
			if (isOk) {
				saveGlfFastData(glfFastData);
			}
//			makeNew = true;
		}
		if (glfFastData != null) {
			totalFileBytes = glfFastData.getFileBytes();
		}
//		long t2 = System.nanoTime();
//		if (makeNew) {
//			System.out.printf("GLF zip file structure analysed in %3.3fms\n", (t2-t1)/1000000.);
//		}
//		else {
//			System.out.printf("GLF structure reloaded in %3.3fms\n", (t2-t1)/1000000.);
//		}
	}

	private GLFFastData loadGlfFastData() {
		File fastFile = getGlfFastFile(glfFile);
		GLFFastData fastData = null;
		if (fastFile.exists() == false) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fastFile)));
			fastData = (GLFFastData) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error reading fast glf index file " + fastFile.toString());
		}
		return fastData;
	}

	private void saveGlfFastData(GLFFastData glfFastData) {
		File fastFile = getGlfFastFile(glfFile);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fastFile)));
			oos.writeObject(glfFastData);
			oos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	/**
	 * Get a name for a fast glf data file. Basicaly the glf file with .ind on the end
	 * @param glfFile
	 * @return
	 */
	public static File getGlfFastFile(File glfFile) {
		if (glfFile == null) {
			return null;
		}
		String name = glfFile.getAbsolutePath() + ".ind";
		return new File(name);
	}

	private void openInputStream() throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(glfFile);
		countingInputStream = new CountingInputStream(new BufferedInputStream(fis));
		glfInputStream = new LittleEndianDataInputStream(countingInputStream);
	}

	/**
	 * If I'm right, the GLF files are all REALLY simple zip files, with no actual 
	 * compression, so it should be very easy to find the starts of the three
	 * files within each archive, then set up the reader to get quickly to any 
	 * point within the arcive to grab data. 
	 * @param inputStream
	 * @return true if all OK.
	 */
	private synchronized boolean createGlfFastInput() {
		int foundFiles = 0;
		glfFastData = new GLFFastData();
		boolean isDatFile = false;
		try {
			while (true) {
				int sig = glfInputStream.readInt();
//				System.out.printf("Zip maginc number = 0x%08x at %d\n", sig, countingInputStream.getPos());
				//				sig = Integer.reverseBytes(sig);
				if (sig == ZipInputStream.LOCSIG) { // file data
//					System.out.println("ZipInputStream.LOCSIG");
//					System.out.printf("Sig 0x%08X\n", sig);
					int version = glfInputStream.readUnsignedShort();
					int bitFlag = glfInputStream.readUnsignedShort();
					int method = glfInputStream.readUnsignedShort();
					int modTime = glfInputStream.readUnsignedShort();
					int modDate = glfInputStream.readUnsignedShort();
					long crc = glfInputStream.readUnsignedInt();
					long cSize = glfInputStream.readUnsignedInt();
					long uSize = glfInputStream.readUnsignedInt();
					if (cSize < uSize) {
						//				compressed is smaller, so it IS compressed
//						return false;
					}
					int fNameLen = glfInputStream.readUnsignedShort();
					int exf = glfInputStream.readUnsignedShort(); // no idea !
					byte[] nameData = new byte[fNameLen];
					glfInputStream.read(nameData);
					byte[] extraData = new byte[exf];
					glfInputStream.read(extraData);
					String fileName = new String(nameData);
					if (fileName.endsWith(".cfg")) {
						glfFastData.cfgFileName = fileName;
						glfFastData.cfgFilePos = countingInputStream.getPos();
						glfFastData.cfgFileLen = uSize;
						isDatFile = false;
					}
					else if (fileName.endsWith(".dat")) {
						glfFastData.datFileName = fileName;
						glfFastData.datFilePos = countingInputStream.getPos();
						glfFastData.datFileLen = uSize;
						isDatFile = true;
					}
					else if (fileName.endsWith(".xml")) {
						glfFastData.xmlFileName = fileName;
						glfFastData.xmlFilePos = countingInputStream.getPos();
						glfFastData.xmlFileLen = uSize;
						isDatFile = false;
					}
					else {
						// tends to crap out here when reading the general directory footer
						// since there isn't one of the three valid file names. 
						return false;
					}
//					glfInputStream.skipBytes(cSize);
					// now need to go through all the blocks ...
					boolean lastBlock = false;
					boolean isRaw;
					long totalBlockBytes = 0;
					int nBlocks = 0;
					while (!lastBlock) {
						nBlocks++;
						int bMap = glfInputStream.readUnsignedByte();
						lastBlock = ((bMap & 0x1) == 0x1);
						isRaw = ((bMap & 0x6) == 0);
//						isRaw = true;
						long bCount = countingInputStream.getPos();
//						if (bCount > 430070990 && bCount < 430070990 + 65536*2) {
//							System.out.println("Block data start at byte " + bCount);
//						}
						int blockSize = glfInputStream.readUnsignedShort();
						if (!isRaw) {
//							return false;
						}
//						if (lastBlock == false && blockSize != BLOCKDATALENGTH) {
//							System.out.printf("irregular block length %d at byte %d\n", blockSize, bCount);
//						}
						if (isDatFile) {
							glfFastData.datBlockStarts.add(new GLFFastBlockData(bMap, totalBlockBytes, blockSize, bCount));
						}
						int spares = glfInputStream.readUnsignedShort();
//						if (lastBlock || spares > 4) {
//							System.out.printf("Spares in %s = %d \n", fileName,  spares);
//						}
						totalBlockBytes += blockSize;
						glfInputStream.skipBytes(blockSize);
					}
//					System.out.println("blocks read from archive: " + nBlocks);
					
					long currentCount = countingInputStream.getPos();
					
					if (totalBlockBytes != uSize && uSize > 0) {
//						System.out.printf("Total data size not as expected (%d/%d) at block %d in %s\n",
//								totalBlockBytes, uSize,
//								glfFastData.datBlockStarts.size() + 1, glfFile.getName());
//						return false;
					}
				}
				else if (sig == ZipInputStream.CENSIG) { // central directory file header
//					System.out.println("ZipInputStream.CENSIG");
					int vMade = glfInputStream.readUnsignedShort();
					int vNeeded = glfInputStream.readUnsignedShort();
					int bitFlags = glfInputStream.readUnsignedShort();
					int method = glfInputStream.readUnsignedShort();
					int lastDate = glfInputStream.readUnsignedShort();
					int lastTime = glfInputStream.readUnsignedShort();
					long crcAll = glfInputStream.readUnsignedInt();
					long cmpSize = glfInputStream.readUnsignedInt();
					long unCmpSize = glfInputStream.readUnsignedInt();
					int nameLen = glfInputStream.readUnsignedShort();
					int exLen = glfInputStream.readUnsignedShort();
					int commentLen = glfInputStream.readUnsignedShort();
					int diskNo = glfInputStream.readUnsignedShort();
					int intlFileAttr = glfInputStream.readUnsignedShort();
					long fileAttr = glfInputStream.readUnsignedInt();
					long relOffset = glfInputStream.readUnsignedInt();
					byte[] nB = new byte[nameLen];
					glfInputStream.read(nB);
					String fileName = new String(nB);
					byte[] eB = new byte[exLen];
					glfInputStream.read(eB);
					String extraName = new String(eB);
					byte[] cB = new byte[commentLen];
					glfInputStream.read(cB);
					String comment = new String(cB);
//					if (comment != null) {
//						System.out.println(comment);
//					}
				}
				else if (sig == ZipInputStream.EXTSIG) {
//					System.out.println("ZipInputStream.EXTSIG");
				}
				else if (sig == ZipInputStream.ENDSIG) {
//					System.out.println("ZipInputStream.ENDSIG");
					int diskNo = glfInputStream.readUnsignedShort();
					int dirStart = glfInputStream.readUnsignedShort();
					int nCenDirThis = glfInputStream.readUnsignedShort();
					int nCenDir = glfInputStream.readUnsignedShort();
					long dirLen = glfInputStream.readUnsignedInt();
					long cenDirStart = glfInputStream.readUnsignedInt();
					int comLen = glfInputStream.readUnsignedShort();
					byte[] cD = new byte[comLen];
					String comment = new String(cD);
//					if (comment != null && comment.length() > 0) {
//						System.out.println(comment);
//					}
				}
				else {
					System.out.printf("Unknown sig in GLF file: 0x%08X\n", sig);
				}
//				
				
//				if (++foundFiles == 3) {
//					return true;
//				}

			}


		} 
		catch (EOFException eof) {
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return glfFastData.datFileName != null;
	}

	@Override
	public int read() throws IOException {
		if (currentlyAvailable() <= 0) {
			if (loadNextBlock(currentLoadedBlock+1) == false) {
				return -1;
			};
		}
		int data = Byte.toUnsignedInt(currentBlockData[(int) (currentAbsPos-blockStartByte)]);
		currentAbsPos++;
		return data;
	}
	
	/**
	 * Number of bytes of data available in the current buffer. 
	 * @return
	 */
	int currentlyAvailable() {
		return (int) (blockEndByte-currentAbsPos);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
//		if (len > 8) {
//			System.out.println("Big read of " + len + " bytes");
//		}
		readMonitor.start();
		long endByte = currentAbsPos + len;
		int bytesRead = 0;
		boolean pastEnd = false;
		while (bytesRead < len) {
			int toRead = Math.min(len-bytesRead, currentlyAvailable());
			System.arraycopy(currentBlockData, (int) (currentAbsPos-blockStartByte), b, off + bytesRead, toRead);
			currentAbsPos += toRead;
			bytesRead += toRead;
			if (currentAbsPos >= blockEndByte) {
				boolean loaded = loadNextBlock(currentLoadedBlock+1);
				if (loaded == false) {
					pastEnd = true;
					break;
				}
			}
		}
		readMonitor.stop();
		if (pastEnd && bytesRead == 0) {
			throw new EOFException();
		}
		return bytesRead;
	}

//	@Override
//	public byte[] readAllBytes() throws IOException {
//		return super.readNBytes(available());
//	}
//
//	@Override
//	public byte[] readNBytes(int len) throws IOException {
//		byte[] rData  = new byte[len];
//		read(rData, 0, len);
//		return rData;
//	}
//
//	@Override
//	public int readNBytes(byte[] b, int off, int len) throws IOException {
//		return read(b, off, len);
//	}

	@Override
	public synchronized long skip(long n) throws IOException {
		skipMonitor.start();
//		if (skipMonitor.getProcessCalls() == 1295) {
//			System.out.println("All about to go horribly wrong");
//		}
		long endByte = currentAbsPos + n;
		long remaining = blockEndByte-currentAbsPos; // bytes remaining in memory 	
		currentAbsPos += n;	
		if (n < remaining) {
			skipMonitor.stop();
			return n;
		}
		if (glfFastData == null) {
			return 0;
		}
		/*
		 * Otherwise we need to go through the blocks until currentAbsPos >= a block start
		 * in the great array list. Then we need to skip the appropriate number of bytes in 
		 * the underlying input stream and also then load the next block in the actual data
		 * the underlying input stream will currently be at the end of currentLoadedBlock 
		 */
		int whichBlock = currentLoadedBlock+1;
		long skipInUnderlying = 0;
		while (whichBlock < glfFastData.datBlockStarts.size()-1) {
			if (glfFastData.datBlockStarts.get(whichBlock).getVirtualEndByte() >= currentAbsPos) {
				// where we want to be will be within that block, so 
				// no skipping in the underlying data.
				break;
			}
			else {
				skipInUnderlying += glfFastData.datBlockStarts.get(whichBlock).getThisBlockBytes()+BLOCKHEADLENGTH;
				whichBlock++;
			}
		}
		if (skipInUnderlying > 0) {
			glfInputStream.skip(skipInUnderlying);
		}
		loadNextBlock(whichBlock);
		// otherwise, go to the end of this block, then see how many are left
////		n -= remaining;
//		int toSkip = (int) ((n-remaining)/BLOCKDATALENGTH)+1;
//		skipNBlocks(toSkip);
		skipMonitor.stop();
				
		return n;
	}

//	@Override
//	public void skipNBytes(long n) throws IOException {
//		super.skip(n);
//	}

	@Override
	public synchronized int available() throws IOException {
		return (int) (glfFastData.datFileLen-currentAbsPos);
	}

	@Override
	public void close() throws IOException {
		super.close();
	}


	@Override
	public String toString() {
		return super.toString();
	}

	public synchronized void resetDataStream() throws IOException {
		if (glfInputStream != null) {
			glfInputStream.close();
		}
		openInputStream();
		if (glfInputStream == null || glfFastData == null) {
			// not sure how glfFastData can be null ? 
			return;
		}
		glfInputStream.skip(glfFastData.datFilePos);
		blockStartByte = blockEndByte = 0;
		currentBlockLength = 0;
		currentAbsPos = 0;
		loadNextBlock(0);
	}
	
	/**
	 * Load the next block of data into memory. 
	 * @return true if successful. 
	 * @throws IOException
	 */
	private synchronized boolean loadNextBlock(int blockNumber) throws IOException {
		loadMonitor.start();
		currentLoadedBlock = blockNumber;
		ArrayList<GLFFastBlockData> blockStarts = glfFastData.datBlockStarts;
		if (blockNumber >= blockStarts.size()) {
//			System.out.printf("Unavailable block number %d in GLF has %d: %s\n", blockNumber, blockStarts.size(), this.glfFile.getName());		
			return false;
		}
		GLFFastBlockData blockData = blockStarts.get(blockNumber);
		int bMap = blockData.getbMap();
		boolean isRaw = ((bMap & 6) == 0);
		if (isRaw) {
			return loadRawBlock(blockData);
		}
		else {
			return loadRawBlock(blockData);
		}
	}
	private synchronized boolean loadCompressedBlock(GLFFastBlockData blockData) throws IOException {
		blockStartByte = blockData.getVirtualStartByte();
		int bScan = glfInputStream.readUnsignedByte();
		int compSize = glfInputStream.readUnsignedShort();
		int spares = glfInputStream.readUnsignedShort();
		byte[] data = new byte[5 + compSize + spares];
		ByteArrayOutputStream bos;
		LittleEndianDataOutputStream ls = new LittleEndianDataOutputStream(bos = new ByteArrayOutputStream(5 + compSize));
		ls.writeByte(bScan);
		ls.writeShort(compSize);
		ls.writeShort(spares);
		byte[] fileData = new byte[compSize + spares];
		int fileRead = glfInputStream.read(fileData);
		ls.write(fileData);
//		bos.
		byte[] byteData = bos.toByteArray();
		
		byte[] inflated = new byte[65536];
		Inflater inflater = new Inflater();
		inflater.setInput(byteData);
		int decompSize = 0;
		try {
			decompSize = inflater.inflate(inflated);
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
//		int maxSize = 65536;
//		byte[] data = new byte[maxSize];
//		int bytesRead = glfInputStream.read(data);
//		LittleEndianDataInputStream lis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
////		int bScan = lis.readUnsignedByte();
//		Inflater inflater = new Inflater();
//		inflater.setInput(data, 0, bytesRead);
//		inflater.
		
	}
	private synchronized boolean loadRawBlock(GLFFastBlockData blockData) throws IOException {
				
		blockStartByte = blockData.getVirtualStartByte();
		int bMap = glfInputStream.readUnsignedByte();
		boolean isRaw = ((bMap & 0x6) == 0);
		int bytesRead = 0;
		// raw data, don't really need to do anything. 
		currentBlockLength = glfInputStream.readUnsignedShort();
		int spares = glfInputStream.readShort();
		if (currentBlockData == null || currentBlockData.length < currentBlockLength) {
			currentBlockData = new byte[currentBlockLength];
		}
		bytesRead = glfInputStream.read(currentBlockData, 0, currentBlockLength);
		blockEndByte = blockStartByte + bytesRead;
		loadMonitor.stop();
		return bytesRead == currentBlockLength;
	}

	/**
	 * @return the glfFile
	 */
	public File getGlfFile() {
		return glfFile;
	}

	/**
	 * @param glfFile the glfFile to set
	 */
	public void setGlfFile(File glfFile) {
		this.glfFile = glfFile;
	}
	
	
//	/**
//	 * Skip a number of what are assumed to be full blocks. We actually skip
//	 * n-1 blocks and read the last one into currentData. <p>
//	 * Calling this with n = 1 is equivalent to calling loadNextBlock()
//	 * @param n number to skip. 
//	 * @return ok
//	 * @throws IOException 
//	 */
//	private boolean skipNBlocks(int n) throws IOException {
//		if (n == 0) {
//			return true;
//		}
//		int toSkip = n-1;
//		countingInputStream.skip(toSkip*BLOCKLENGTH);
//		blockStartByte += toSkip*BLOCKDATALENGTH;
//		blockEndByte += toSkip*BLOCKDATALENGTH;
//		return loadNextBlock();
//	}

}
