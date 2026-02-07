package tritechgemini.detect;

import java.util.ArrayList;

import tritechgemini.imagedata.SonarImageRecordI;

/**
 * Start of some other detector ideas, but they ain't going to work very well. 
 * @author Doug Gillespie
 *
 */
public class RelativeThresholdDetector implements RegionDetector {
	/**
	 * Max size in pixels for any region. 
	 */
	private int MAXSIZE = 200;
	
	/**
	 * Min and max object sizes in metres. 
	 */
	private double minObjectSize = 0;

	private double maxObjectSize = MAXSIZE;
	
	public RelativeThresholdDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setMinObjectSize(double minObjectSize) {
		this.minObjectSize = minObjectSize;
	}

	@Override
	public void setMaxObjectSize(double maxObjectSize) {
		this.maxObjectSize  = maxObjectSize;
	}

	@Override
	public ArrayList<DetectedRegion> detectRegions(SonarImageRecordI rawGeminiRecord,
			SonarImageRecordI denoisedRecord, int thHigh, int thLow, int nConnect) {

		ArrayList<DetectedRegion> detectedRegions = null;
		short[] denoised = denoisedRecord.getShortImageData();
		int nBearing = denoisedRecord.getnBeam();
		int nRange = denoisedRecord.getnRange();
		int nData = nRange*nBearing;
		/**
		 * Zero data around the edges of the image. 
		 */
		for (int i = 0, j = nBearing*nRange-1; i < nBearing; i++, j--) {
			// this gets the first and last range data
			denoised[i] = denoised[j] = 0;
		}
		for (int i = nBearing-1, j = nBearing; j < nData; i+=nBearing, j+= nBearing) {
			// this gets the edges (last and first bearing) at each range 
			denoised[i] = denoised[j] = 0;
		}
		/**
		 * Make the mask, connect 4 or connect 8.
		 */
		int[] searchMask;
		if (nConnect == 4) {
			int[] mask = {-1, 1, nBearing, -nBearing};
			searchMask = mask;
		}
		else {
			int[] mask = {-1, 1, nBearing, -nBearing, nBearing-1, nBearing+1, -nBearing-1, -nBearing+1};
			searchMask = mask;
		}
		for (int i = nBearing+1; i < nData-nBearing; i++) {
			if (denoised[i] >= thHigh) {
//				if (geminiRecord.getRecordNumber() == 99) {
//					System.out.println("Record 99");
//				}
				//	we have a detection
//				DetectedRegion newRegion = new DetectedRegion(denoisedRecord, i);
//				short currentLevel = 
//				int offThreshold = (denoised[i]*thLow)/thHigh;
//				denoised[i] = 0; // set point to zero so it's not used again
//				expandRegion(newRegion, denoised, i, offThreshold, searchMask);
//				newRegion.completeRegion();
//				if (wantRegion(newRegion)) {
//					if (detectedRegions == null) {
//						detectedRegions = new ArrayList<>();
//					}
//					detectedRegions.add(newRegion);
//				}
			}
		}
		return detectedRegions;
		
	}

	/**
	 * Recursive call to more about a point, seeing if any adjacent points are above threshold and 
	 * should be added to the region. As data are added, they are set to zero so that as the 
	 * search mask moves around, it can never add the same point twice. 
	 * @param region Growing region of data. 
	 * @param data data array
	 * @param currInd current search index in the data
	 * @param thLow threshold
	 * @param searchMask search mask for connect 4 or connect 8.  
	 */
	private void expandRegion(DetectedRegion region, byte[] data, int currInd, int thLow, int[] searchMask) {
		if (region.getRegionSize() >= MAXSIZE) {
			return;
		}
		for (int i = 0; i < searchMask.length; i++) {
			int ind = currInd+searchMask[i];
			int dat = Byte.toUnsignedInt(data[ind]);
			if (dat >= thLow) {
				region.addPoint(ind);
				data[ind] = 0;
				expandRegion(region, data, ind, thLow, searchMask);
			}
		}
	}

}
