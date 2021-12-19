package tritechgemini.fileio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GLFFastInputStream extends InputStream {

	private LittleEndianDataInputStream glfInputStream;
	private CountingInputStream countingInputStream;

	boolean isOk;

	private String cfgFileName, datFileName, xmlFileName;
	private long cfgFilePos, datFilePos, xmlFilePos;
	private long cfgFileLen, datFileLen, xmlFileLen;
	
	private File glfFile;
	
	/**
	 * total length of each complete block
	 */
	private static final int BLOCKLENGTH = 65536;
	/**
	 * length of the actual data in each complete block. 
	 */
	private static final int BLOCKDATALENGTH = 65531;
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
	private byte[] currentBlockData = new byte[BLOCKDATALENGTH];
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
		isOk = checkGlfInput();
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
	private boolean checkGlfInput() {
		int foundFiles = 0;
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
					cfgFileName = fileName;
					cfgFilePos = countingInputStream.getPos();
					cfgFileLen = uSize;
				}
				else if (fileName.endsWith(".dat")) {
					datFileName = fileName;
					datFilePos = countingInputStream.getPos();
					datFileLen = uSize;
				}
				else if (fileName.endsWith(".xml")) {
					xmlFileName = fileName;
					xmlFilePos = countingInputStream.getPos();
					xmlFileLen = uSize;
				}
				else {
					return false;
				}
				glfInputStream.skip(cSize);
				// now need to go through all the blocks ...
//				boolean lastBlock = false;
//				boolean isRaw;
//				while (!lastBlock) {
//					int bMap = glfInputStream.readUnsignedByte();
//					lastBlock = ((bMap & 0x1) == 0x1);
//					isRaw = ((bMap & 0x6) == 0);
//					if (!isRaw) {
//						return false;
//					}
//					int blockSize = glfInputStream.readUnsignedShort();
//					int spares = glfInputStream.readUnsignedShort();
//					glfInputStream.skip(blockSize);
//				}
				
				
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
			loadNextBlock();
		}
		int data = currentBlockData[(int) (currentAbsPos-blockStartByte)];
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
				loadNextBlock();
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
	public long skip(long n) throws IOException {
		skipMonitor.start();
		long endByte = currentAbsPos + n;
		long remaining = blockEndByte-currentAbsPos; // bytes remaining in memory 	
		currentAbsPos += n;	
		if (n < remaining) {
			skipMonitor.stop();
			return n;
		}
		// otherwise, go to the end of this block, then see how many are left
//		n -= remaining;
		int toSkip = (int) ((n-remaining)/BLOCKDATALENGTH)+1;
		skipNBlocks(toSkip);
		skipMonitor.stop();
				
		return n;
	}

//	@Override
//	public void skipNBytes(long n) throws IOException {
//		super.skip(n);
//	}

	@Override
	public int available() throws IOException {
		return (int) (datFileLen-currentAbsPos);
	}

	@Override
	public void close() throws IOException {
		super.close();
	}


	@Override
	public String toString() {
		return super.toString();
	}

	public void resetDatStream() throws IOException {
		if (glfInputStream != null) {
			glfInputStream.close();
		}
		openInputStream();
		glfInputStream.skip(datFilePos);
		blockStartByte = blockEndByte = 0;
		currentBlockLength = 0;
		currentAbsPos = 0;
		loadNextBlock();
	}
	
	/**
	 * Load the next block of data into memory. 
	 * @return true if successful. 
	 * @throws IOException
	 */
	private boolean loadNextBlock() throws IOException {
		loadMonitor.start();
		blockStartByte += currentBlockLength;
		int bMap = glfInputStream.readUnsignedByte();
//		boolean lastBlock = ((bMap & 0x1) == 0x1);
		boolean isRaw = ((bMap & 0x6) == 0);
		if (!isRaw) {
			throw new IOException("Data in GLF block are not raw data");
		}
		currentBlockLength = glfInputStream.readUnsignedShort();
		int spares = glfInputStream.readShort();
		int read = glfInputStream.read(currentBlockData, 0, currentBlockLength);
		blockEndByte = blockStartByte + read;
		loadMonitor.stop();
		return read == currentBlockLength;
	}
	
	
	/**
	 * Skip a number of what are assumed to be full blocks. We actually skip
	 * n-1 blocks and read the last one into currentData. <p>
	 * Calling this with n = 1 is equivalent to calling loadNextBlock()
	 * @param n number to skip. 
	 * @return ok
	 * @throws IOException 
	 */
	private boolean skipNBlocks(int n) throws IOException {
		if (n == 0) {
			return true;
		}
		int toSkip = n-1;
		countingInputStream.skip(toSkip*BLOCKLENGTH);
		blockStartByte += toSkip*BLOCKDATALENGTH;
		blockEndByte += toSkip*BLOCKDATALENGTH;
		return loadNextBlock();
	}

}
