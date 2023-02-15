package tritechgemini.fileio;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LittleEndianDataOutputStream extends OutputStream implements DataOutput {

	private DataOutputStream o;
	
	public LittleEndianDataOutputStream(OutputStream outputStream) {
		super();
		this.o = new DataOutputStream(outputStream);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		o.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		o.writeByte(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		// TODO Auto-generated method stub
		short s = Short.reverseBytes((short) v);
		o.writeShort(s);		
	}

	@Override
	public void writeChar(int v) throws IOException {
		o.writeChar(Character.reverseBytes((char) v));
	}

	@Override
	public void writeInt(int v) throws IOException {
		o.writeInt(Integer.reverseBytes(v));
	}

	@Override
	public void writeLong(long v) throws IOException {
		o.writeLong(Long.reverseBytes(v));
	}

	@Override
	public void writeFloat(float v) throws IOException {
		int intVal = Float.floatToIntBits(v);
		writeInt(intVal);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		long longVal = Double.doubleToLongBits(v);
		writeLong(longVal);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		o.writeBytes(s);
	}

	@Override
	public void writeChars(String s) throws IOException {
		o.writeBytes(s);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		o.writeBytes(s);
	}

	@Override
	public void write(int b) throws IOException {
		o.write(Integer.reverseBytes(b));		
	}

	@Override
	public void write(byte[] b) throws IOException {
		o.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		o.write(b, off, len);
	}

}
