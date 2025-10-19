package aris.beams;

import java.util.ArrayList;

/**
 * info about ARIS beams for a device. These are not coded in the data, so 
 * the software needs to find the correct set of beam data for each device. 
 * @author dg50
 *
 */
public abstract class ARISBeamData {
	
	private ArrayList<ARISBeam> arisBeams = new ArrayList<>();
	
	/**
	 * @return the beamCentresRadians
	 */
	public double[] getBeamCentresRadians() {
		return beamCentresRadians;
	}

	private double[] beamCentres;

	private double[] beamCentresRadians;

	public ARISBeamData() {
		createBeamList();
		getCentres();
	}

	abstract protected void createBeamList();

	protected void DEFINE_BEAMWIDTH3(int beam, double centre, double left, double right) {
		arisBeams.add(new ARISBeam(beam, centre, left, right));
	}
	
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
	 * @return the beamCentres
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
}
