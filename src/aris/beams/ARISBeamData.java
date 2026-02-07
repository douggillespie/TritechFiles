package aris.beams;

import java.util.ArrayList;

/**
 * info about ARIS beams for a device. These are not coded in the data, so 
 * the software needs to find the correct set of beam data for each device. 
 * Might need to flip these for compatibility with Tritech data:
 * https://github.com/SoundMetrics/aris-file-sdk/blob/master/docs/understanding-aris-data.md
 * "in ARIS acoustic sample data beam 0 is the right-most beam, and beams are numbered from right-to-left."
 * @author Doug Gillespie
 *
 */
public abstract class ARISBeamData {
	
	private ArrayList<ARISBeam> arisBeams = new ArrayList<>();
	
	private double[] beamCentres;

	private double[] beamCentresRadians;

	public ARISBeamData() {
		createBeamList();
		getCentres();
	}

	/**
	 * Create the beam list. This is done through repeated calls
	 * to DEFINE_BEAMWIDTH3, once for each beam, copying the C code
	 */
	abstract protected void createBeamList();

	/**
	 * Add a beam using the same function call as the name of the def in 
	 * the source header files so it's easy to copy paste the code from 
	 * the C headers into Java classes. 
	 * @param beam
	 * @param centre
	 * @param left
	 * @param right
	 */
	protected void DEFINE_BEAMWIDTH3(int beam, double centre, double left, double right) {
		arisBeams.add(new ARISBeam(beam, centre, left, right));
	}
	
	/**
	 * From the array list of detail, make a simple double array of
	 * the beam centres in degrees and in radians. 
	 */
	protected void getCentres() {
		if (arisBeams == null || arisBeams.size() == 0) {
			return;
		}
		double[] c = new double[arisBeams.size()];
		double[] cR = new double[arisBeams.size()];
		for (int i = 0; i < arisBeams.size(); i++) {
			c[i] = arisBeams.get(i).centre;
			cR[i] = c[i] * Math.PI/180.;
		}
		beamCentres = c;
		beamCentresRadians = cR;
	}

	/**
	 * @return the beamCentres in degrees
	 */
	public double[] getBeamCentres() {
		return beamCentres;
	}
	
	/**
	 * Get the right set of beam data based on the number of beams. 
	 * @param nBeams
	 * @return
	 */
	public static ARISBeamData getBeamData(int nBeams) {
		switch (nBeams) {
		case 48:
			return new ARISBeams_ARIS1800_1200_48();
		case 64:
			return new ARISBeams_ARIS3000_64();
		case 96:
			return new ARISBeams_ARIS1800_96();
		case 128:
			return new ARISBeams_ARIS3000_128();
		}
		return null;
	}

	/**
	 * Get the beam centres in radians. 
	 * @return the beamCentresRadians
	 */
	public double[] getBeamCentresRadians() {
		return beamCentresRadians;
	}
}
