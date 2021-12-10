package tritechgemini.imagedata;


/**
 * Fan maker where a LUT is used to work through each point in the fan image and take 
 * data from the GeminiRecord to populate that point. 
 * @author dg50
 *
 */
public class FanPicksFromData extends ImageFanMaker {

	private int nNearPoints = 2;
	
	private int nThread = 2;
	
	/**
	 * LUT which tells us where to take data from in the raw data array. 
	 */
	private int[][][] dataPickLUT;
	/**
	 * LUT which tells us where to put data in the image. Need to test speed to 
	 * see if we're better off making this one or 2D. I think the plan is 
	 * to make it 2D and then have lots of threads working on different columms 
	 */
	private int[][] dataPutLUT;
	
	/**
	 * Scale for adding in the data. 
	 */
	private double[][][] dataLUTScale;
	
	/**
	 * Number of points in each column actually used. 
	 */
	private int[] usedColumnLength;

	private int xCent;
	
	public FanPicksFromData() {
	}

	
	public FanPicksFromData(int nNearPoints) {
		nNearPoints = 2;
	}
	
	@Override
	public FanImageData createFanData(GeminiImageRecordI geminiRecord, int nPixX, int nPixY) {
		if (dataPickLUT == null) {
			createLUTs(geminiRecord, nPixX, nPixY);
		}
		
		byte[] data = geminiRecord.getImageData();
		short[][] image = new short[nPixX][nPixY];

		Thread[] threads = new Thread[nThread];
		for (int t = 0; t < nThread; t++) {
			int pos = t;
			threads[t] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int ix = pos; ix < nPixX; ix += nThread) {
						int[][] pickLUT = dataPickLUT[ix];
						int[] putLUT = dataPutLUT[ix];
						double[][] lutScale = dataLUTScale[ix];
						short[] imRow = image[ix]; 
						for (int p = 0; p < usedColumnLength[ix]; p++) {
							int[] pickLUTRow = pickLUT[p];
							double[] scaleRow = lutScale[p];
							int val = Byte.toUnsignedInt(data[pickLUTRow[0]]) +  Byte.toUnsignedInt(data[pickLUTRow[1]]);
							if (pickLUTRow.length ==4) {
								val += Byte.toUnsignedInt(data[pickLUTRow[2]]) +  Byte.toUnsignedInt(data[pickLUTRow[3]]);
							}
							imRow[putLUT[p]] = (short) val;
						}
					}
				}
				
			});
			threads[t].start();
		}
		for (int t = 0; t < nThread; t++) {
			try {
				threads[t].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * Create the lookup tables required to link points in the image to points in the data
	 * @param geminiRecord
	 * @param nPixX n x Pixels in image
	 * @param nPixY n y Pixels in image
	 */
	private void createLUTs(GeminiImageRecordI geminiRecord, int nPixX, int nPixY) {
		int nP = nNearPoints == 2 ? 2 : 4;
		dataPickLUT = new int[nPixX][nPixY][nP];
		dataPutLUT = new int[nPixX][nPixY];
		usedColumnLength = new int[nPixX];
		dataLUTScale = new double[nPixX][nPixY][2];
		double[] bearingTable = geminiRecord.getBearingTable();
		xCent = (int) Math.ceil(nPixX/2.);
		int nBearing = bearingTable.length;
		int nRange = geminiRecord.getnRange();
		double imageScale = (double) nRange / (double) nPixY ;
		Thread[] threads = new Thread[nThread];
		int totalData = geminiRecord.getnBeam() * geminiRecord.getnRange();
		for (int t = 0; t < nThread; t++) {
			int pos = t;
			threads[t] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int ix = pos; ix < nPixX; ix += nThread) {
						int x = ix-xCent;
						for (int iy = 0; iy < nPixY; iy++) {
							double pixRng = Math.sqrt(iy*iy+x*x)*imageScale;
							if (pixRng >= nRange) {
								continue; // too far away
							}
							double pixAng = Math.atan2(x, iy); // backward !
							int bearInd1 = findClosestBearings(bearingTable, pixAng);
							if (bearInd1 < 0) {
								continue; // angle out of range
							}
							double db1 = Math.abs(pixAng-bearingTable[bearInd1]);
							double db2 = Math.abs(pixAng-bearingTable[bearInd1]);
							dataLUTScale[ix][usedColumnLength[ix]][0] = db2/(db1+db2);
							dataLUTScale[ix][usedColumnLength[ix]][1] = db1/(db1+db2);
							if (nP == 2) {
								int iR = (int) Math.round(pixRng);
								if (iR >= nRange) {
									continue;
								}
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1;
								dataPickLUT[ix][usedColumnLength[ix]][1] = iR*nBearing + bearInd1+1;
							}
							else {
								int iR = (int) Math.floor(pixRng);
								if (iR >= nRange) {
									continue;
								}
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1;
								dataPickLUT[ix][usedColumnLength[ix]][1] = iR*nBearing + bearInd1+1;
								iR = (int) Math.ceil(pixRng);
								if (iR >= nRange) {
									iR--;
								}
								dataPickLUT[ix][usedColumnLength[ix]][2] = iR*nBearing + bearInd1;
								dataPickLUT[ix][usedColumnLength[ix]][3] = iR*nBearing + bearInd1+1;
							}
							usedColumnLength[ix]++;
						}
					}					
				}
			});
			threads[t].start();
		}
		for (int t = 0; t < nThread; t++) {
			try {
				threads[t].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Find the closest pair of bearings. Return -1 if it's in part of the 
	 * image out of range. 
	 * @param bearingTable
	 * @param bearing
	 * @return two element vector of the bearing prior to bearing, 
	 * so can always use this and the one after. 
	 */
	private int findClosestBearings(double[] bearingTable, double bearing) {
		// the bearing table is in ascending order. (I think they are always other way around).
		int nBear = bearingTable.length;
		boolean isAsc = bearingTable[nBear-1] > bearingTable[0];
//		double m = isAsc ? 1. : -1.;
//		double minB, maxB;
//		minB = bearingTable[0]*m;
//		maxB = bearingTable[nBear-1]*m;
//		if (bearing < minB || bearing > maxB) {
//			return -1;
//		}
		/*
		 * This should fine a position where a bearing is exactly equal to the one
		 * we want, or we have one > and one < giving opposite signs. 
		 */
		for (int i = 0; i < nBear-1; i++) {
			if ((bearingTable[i]-bearing) * (bearingTable[i+1]-bearing) <= 0) {
				return i;
			}
		}
		return -1;
	}


	@Override
	public void clearTables() {
		dataPickLUT = null;
		dataPutLUT = null;
	}

}
