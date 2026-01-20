package aris;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;

public abstract class ARISHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	public ARISHeader() {
	}

	public abstract void readHeader(DataInput dis) throws IOException;

	protected String readString(DataInput dis, int nBytes) throws IOException {
		byte[] b = new byte[nBytes];
		dis.readFully(b);
		String str = new String(b);
		return str;
	}

}
