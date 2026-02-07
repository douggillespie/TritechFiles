package aris.beams;

/**
 * Data on a single beam. Note that angles are in degrees. 
 * @author Doug Gillespie
 *
 */
public class ARISBeam {
	
	int beam;
	double centre;
	double left;
	double right;
	
	public ARISBeam(int beam, double centre, double left, double right) {
		super();
		this.beam = beam;
		this.centre = centre;
		this.left = left;
		this.right = right;
	}
	

}
