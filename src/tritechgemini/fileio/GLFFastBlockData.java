package tritechgemini.fileio;

import java.io.Serializable;

public class GLFFastBlockData implements Serializable {

	private static final long serialVersionUID = 1L;

	private long virtualStartByte;
	
	private int thisBlockBytes;
	
	private long inputStreamPos;

	private int bMap;

	/**
	 * Make information about a block in a zipped input file. 
	 * @param totalVirtualBytes
	 * @param thisBlockBytes
	 * @param inputStreamPos
	 */
	public GLFFastBlockData(int bMap, long virtualStartByte, int thisBlockBytes, long inputStreamPos) {
		super();
		this.bMap = bMap;
		this.virtualStartByte = virtualStartByte;
		this.thisBlockBytes = thisBlockBytes;
		this.inputStreamPos = inputStreamPos;
	}

	public long getVirtualStartByte() {
		return virtualStartByte;
	}
	
	public long getVirtualEndByte() {
		return virtualStartByte+thisBlockBytes;
	}

	public int getThisBlockBytes() {
		return thisBlockBytes;
	}

	public long getInputStreamPos() {
		return inputStreamPos;
	}

	/**
	 * @return the bMap
	 */
	public int getbMap() {
		return bMap;
	}


	
}
