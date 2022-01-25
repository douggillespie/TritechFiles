package tritechgemini.detect;

/**
 * Really simple background subtraction class
 * @author dg50
 *
 */
public class BackgroundSub {

	private short[] background;
	
	/**
	 * Need to scale up the data, might even be best to use float, or small values never
	 * get to contribute to the background data. 
	 */
	private int backgroundScale = 128;
	/**
	 * update constant. should be about 1/20.
	 * but it's stored as pos integer, so will be about 20.  
	 */
	private short updateConst = 20; 
	
	public byte[] removeBackground(byte[] data, boolean updateFirst) {
		if (updateFirst) {
			calcBackground(data);
		}
		else {
			checkArray(data.length);
		}
		byte[] cleanData = new byte[data.length];
		int val;
		for (int i = 0; i < data.length; i++) {
			val =  Math.max(Byte.toUnsignedInt(data[i])-background[i]/backgroundScale,0);
			cleanData[i] = (byte) (val & 0xFF);
		}
		return cleanData;
	}
	
	public short[] calcBackground(byte[] data) {
		checkArray(data.length);
		for (int i = 0; i < data.length; i++) {
			background[i] += ((Byte.toUnsignedInt(data[i])*backgroundScale-background[i]) / updateConst);
		}
		return background;
	}
	
	private void checkArray(int len) {
		if (background == null || background.length != len) {
			background = new short[len];
		}
	}
	
	public byte[] getBackground() {
		if (background == null) {
			return null;
		}
		byte[] data = new byte[background.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (Math.max(background[i]/backgroundScale,0) & 0xFF);
		}
		return data;
		
	}
}
