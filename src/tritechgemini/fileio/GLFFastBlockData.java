package tritechgemini.fileio;

import java.io.Serializable;

public class GLFFastBlockData implements Serializable {

	private static final long serialVersionUID = 1L;

	private long virtualStartByte;
	
	private int thisBlockBytes;
	
	private long inputStreamPos;

	/**
	 * Make information about a block in a zipped input file. 
	 * @param totalVirtualBytes
	 * @param thisBlockBytes
	 * @param inputStreamPos
	 */
	public GLFFastBlockData(long virtualStartByte, int thisBlockBytes, long inputStreamPos) {
		super();
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


	
}
