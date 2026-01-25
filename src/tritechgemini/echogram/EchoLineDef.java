package tritechgemini.echogram;

import java.io.Serializable;

public class EchoLineDef implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;
	
	int bearingBin1;
	
	int bearingBin2;

	/**
	 * Create a echo line definition using two bearing bins, which 
	 * must be within the range of the bearing table, i.e. 
	 * bearingBin1 >= 0 and bearingBin2 < bearingTable.length
	 * @param bearingBin1
	 * @param bearingBin2
	 */
	public EchoLineDef(int bearingBin1, int bearingBin2) {
		super();
		this.bearingBin1 = bearingBin1;
		this.bearingBin2 = bearingBin2;
	}
	
	/**
	 * Get the range from the given bearing table. 
	 * @param bearingTable
	 * @return
	 */
	public double[] getBearingRange(double[] bearingTable) {
		double[] range = {bearingTable[bearingBin1], bearingTable[bearingBin2]};
		return range;
	}

	@Override
	public EchoLineDef clone() {
		try {
			return (EchoLineDef) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof EchoLineDef == false) {
			return false;
		}
		EchoLineDef other = (EchoLineDef) arg0;
		return this.bearingBin1 == other.bearingBin1 && this.bearingBin2 == other.bearingBin2;
	}

	@Override
	public int hashCode() {
		return bearingBin1<<12 + bearingBin2;
	}

	

}
