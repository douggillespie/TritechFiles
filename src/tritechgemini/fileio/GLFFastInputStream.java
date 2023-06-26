package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
			saveGlfFastData(glfFastData);
//			makeNew = true;
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
		try {
			while (true) {
				int sig = glfInputStream.readInt();
				int version = glfInputStream.readUnsignedShort();
				int bitFlag = glfInputStream.readUnsignedShort();
				int method = glfInputStream.readUnsignedShort();
				int modTime = glfInputStream.readUnsignedShort();
				int modDate = glfInputStream.readUnsignedShort();
				int crc = glfInputStream.readInt();
				int cSize = glfInputStream.readInt();
				int uSize = glfInputStream.readInt();
				if (cSize < uSize) {
					//				compressed is smaller, so it IS compressed
					return false;
				}
				int fNameLen = glfInputStream.readUnsignedShort();
				int exf = glfInputStream.readUnsignedShort(); // no idea !
				byte[] nameData = new byte[fNameLen];
				glfInputStream.read(nameData);
				String fileName = new String(nameData);
				if (fileName.endsWith(".cfg")) {
					glfFastData.cfgFileName = fileName;
					glfFastData.cfgFilePos = countingInputStream.getPos();
					glfFastData.cfgFileLen = uSize;
				}
				else if (fileName.endsWith(".dat")) {
					glfFastData.datFileName = fileName;
					glfFastData.datFilePos = countingInputStream.getPos();
					glfFastData.datFileLen = uSize;
				}
				else if (fileName.endsWith(".xml")) {
					glfFastData.xmlFileName = fileName;
					glfFastData.xmlFilePos = countingInputStream.getPos();
					glfFastData.xmlFileLen = uSize;
				}
				else {
					return false;
				}
//				glfInputStream.skipBytes(cSize);
				// now need to go through all the blocks ...
				boolean lastBlock = false;
				boolean isRaw;
				long totalBlockBytes = 0;
				while (!lastBlock) {
					int bMap = glfInputStream.readUnsignedByte();
					lastBlock = ((bMap & 0x1) == 0x1);
					isRaw = ((bMap & 0x6) == 0);
					if (!isRaw) {
						return false;
					}
					long bCount = countingInputStream.getPos();
//					if (bCount > 430070990 && bCount < 430070990 + 65536*2) {
//						System.out.println("Block data start at byte " + bCount);
//					}
					int blockSize = glfInputStream.readUnsignedShort();
//					if (lastBlock == false && blockSize != BLOCKDATALENGTH) {
//						System.out.printf("irregular block length %d at byte %d\n", blockSize, bCount);
//					}
					if (foundFiles == 1) {
						glfFastData.datBlockStarts.add(new GLFFastBlockData(totalBlockBytes, blockSize, bCount));
					}
					int spares = glfInputStream.readUnsignedShort();
					totalBlockBytes += blockSize;
					glfInputStream.skipBytes(blockSize);
				}
				
				if (totalBlockBytes != uSize) {
					System.out.println("Total data size not as expected");
					return false;
				}
				
				if (++foundFiles == 3) {
					return true;
				}

			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public int read() throws IOException {
		if (currentlyAvailable() <= 0) {
			loadNextBlock(currentLoadedBlock+1);
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
		while (bytesRead < len) {
			int toRead = Math.min(len-bytesRead, currentlyAvailable());
			System.arraycopy(currentBlockData, (int) (currentAbsPos-blockStartByte), b, off + bytesRead, toRead);
			currentAbsPos += toRead;
			bytesRead += toRead;
			if (currentAbsPos >= blockEndByte) {
				loadNextBlock(currentLoadedBlock+1);
			}
		}
		readMonitor.stop();
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
		blockStartByte = glfFastData.datBlockStarts.get(blockNumber).getVirtualStartByte();
		int bMap = glfInputStream.readUnsignedByte();
//		boolean lastBlock = ((bMap & 0x1) == 0x1);
		boolean isRaw = ((bMap & 0x6) == 0);
		if (!isRaw) {
			throw new IOException("Data in GLF block are not raw data at bytes " + countingInputStream.getPos());
			/*
			 * Probably if bytes 2 and 3 as 10, it's Huffman coding, so will need a decoder for that. 
			 * Also note that the skipblocks won't work any more since this block size will be a lot smaller. 
			 * So basically need to write something a whole lot more complicated, making it V hard to skip to 
			 * where we want to in a file. 
			 */
		}
		currentBlockLength = glfInputStream.readUnsignedShort();
		int spares = glfInputStream.readShort();
		if (currentBlockData == null || currentBlockData.length < currentBlockLength) {
			currentBlockData = new byte[currentBlockLength];
		}
		int read = glfInputStream.read(currentBlockData, 0, currentBlockLength);
		blockEndByte = blockStartByte + read;
		loadMonitor.stop();
		return read == currentBlockLength;
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
