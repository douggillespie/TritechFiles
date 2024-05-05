package tritechgemini.imagedata;

import java.util.Arrays;

/**
 * Fan maker where a LUT is used to work through each point in the fan image and take 
 * data from the GeminiRecord to populate that point. 
 * @author dg50
 *
 */
public class FanPicksFromData extends ImageFanMaker {

	private int nNearPoints = 2;
	
	private int nThread = 3;
	
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
	 * Length of gemini records used with LUT's
	 */
	private int geminiRecordLength;
	
	/**
	 * Number of points in each column actually used. 
	 */
	private int[] usedColumnLength;

	private int xCent;
	
	public FanPicksFromData() {
	}

	
	public FanPicksFromData(int nNearPoints) {
		this.nNearPoints = checkNPoints(nNearPoints);
	}
	
	@Override
	public FanImageData createFanData(GeminiImageRecordI geminiRecord, int nPixX, int nPixY, byte[] data) {
		if (geminiRecord == null || geminiRecord.getImageData() == null) {
			return null;
		}
		if (needNewLUT(geminiRecord, nPixX, nPixY)) {
			createLUTs(geminiRecord, nPixX, nPixY);
		}
		
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
						Arrays.fill(imRow, (short)-1);
						for (int p = 0; p < usedColumnLength[ix]; p++) {
							int[] pickLUTRow = pickLUT[p];
							double[] scaleRow = lutScale[p];
							double val = 0;
							for (int i = 0; i < scaleRow.length; i++) {
								val += Byte.toUnsignedInt(data[pickLUTRow[i]]) *scaleRow[i];
							}
//							Byte.toUnsignedInt(data[pickLUTRow[0]]) *scaleRow[0] +  Byte.toUnsignedInt(data[pickLUTRow[1]])*scaleRow[1];
//							if (pickLUTRow.length ==4) {
//								val += Byte.toUnsignedInt(data[pickLUTRow[2]])*scaleRow[0] +  Byte.toUnsignedInt(data[pickLUTRow[3]])*scaleRow[1];
//							}
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
		double mPerPixY = geminiRecord.getMaxRange() / nPixY;
		double mPerPixX = geminiRecord.getMaxRange() * Math.abs(Math.sin(geminiRecord.getBearingTable()[0])) / (nPixX/2);
		return new FanImageData(geminiRecord, image, mPerPixX, mPerPixY);
	}

	private boolean needNewLUT(GeminiImageRecordI geminiRecord, int nPixX, int nPixY) {
		if (geminiRecordLength != geminiRecord.getImageData().length) {
			return true;
		}
		if (dataPickLUT == null) {
			return true;
		}
		if (dataPickLUT.length != nPixX) {
			return true;
		}
		if (dataPickLUT[0].length != nPixY) {
			return true;
		}
		return false;
	}


	/**
	 * Create the lookup tables required to link points in the image to points in the data
	 * @param geminiRecord
	 * @param nPixX n x Pixels in image
	 * @param nPixY n y Pixels in image
	 */
	private void createLUTs(GeminiImageRecordI geminiRecord, int nPixX, int nPixY) {
		double[] bearingTable = geminiRecord.getBearingTable();
		if (bearingTable == null) {
			return;
		}
		int nP = checkNPoints(nNearPoints);
		dataPickLUT = new int[nPixX][nPixY][nP];
		dataPutLUT = new int[nPixX][nPixY];
		usedColumnLength = new int[nPixX];
		dataLUTScale = new double[nPixX][nPixY][nP];
		xCent = (int) Math.ceil(nPixX/2.);
		int nBearing = bearingTable.length;
		int nRange = geminiRecord.getnRange();
		double imageScaleY = (double) nRange / (double) nPixY;
		double imageScaleX = (double) nRange * Math.abs(Math.sin(bearingTable[0])) / (double) nPixX * 2;
		Thread[] threads = new Thread[nThread];
		geminiRecordLength = geminiRecord.getnBeam() * geminiRecord.getnRange();
		for (int t = 0; t < nThread; t++) {
			int pos = t;
			threads[t] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int ix = pos; ix < nPixX; ix += nThread) {
						int x = ix-xCent;
						for (int iy = 0; iy < nPixY; iy++) {
							double pixRng = Math.sqrt(iy*iy*imageScaleY*imageScaleY+x*x*imageScaleX*imageScaleX);
							if (pixRng >= nRange) {
								continue; // too far away
							}
							double pixAng = Math.atan2(-x*imageScaleX, iy*imageScaleY); // backward !
							int bearInd1 = findClosestBearings(bearingTable, pixAng);
							if (bearInd1 < 0) {
								continue; // angle out of range
							}
							int bearInd2 = bearInd1+1;
							if (bearInd2 >= bearingTable.length) {
								bearInd2 = bearingTable.length-1;
							}
							double db1 = Math.abs(pixAng-bearingTable[bearInd1]);
							double db2 = Math.abs(pixAng-bearingTable[bearInd2]);
							if (nP == 1) {
								int iR = (int) Math.round(pixRng);
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								if (iR >= nRange) {
									continue;
								}
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								if (db1 < db2) {
									dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1;									
								}
								else {
									dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1 + 1;
								}
								dataLUTScale[ix][usedColumnLength[ix]][0] = 1.;
							}
							else if (nP == 2) {
								int iR = (int) Math.round(pixRng);
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								if (iR >= nRange) {
									continue;
								}
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1;
								dataPickLUT[ix][usedColumnLength[ix]][1] = iR*nBearing + bearInd1+1;
								dataLUTScale[ix][usedColumnLength[ix]][0] = db2/(db1+db2);
								dataLUTScale[ix][usedColumnLength[ix]][1] = db1/(db1+db2);
							}
							else { // nP = 4;
								int iR = (int) Math.floor(pixRng);
								if (iR >= nRange) {
									continue;
								}
								double dr1 = pixRng-iR;
								dataPutLUT[ix][usedColumnLength[ix]] = iy;
								// first bearing, first range 
								dataPickLUT[ix][usedColumnLength[ix]][0] = iR*nBearing + bearInd1;
								// second bearing, first range 
								dataPickLUT[ix][usedColumnLength[ix]][1] = iR*nBearing + bearInd2;
								iR = (int) Math.ceil(pixRng);
								if (iR >= nRange) {
									iR--;
								}
								double dr2 = Math.abs(iR-pixRng);
								// first bearing, second range 
								dataPickLUT[ix][usedColumnLength[ix]][2] = iR*nBearing + bearInd1;
								// second bearing, second range 
								dataPickLUT[ix][usedColumnLength[ix]][3] = iR*nBearing + bearInd2;
								// weights. 
								dataLUTScale[ix][usedColumnLength[ix]][0] = db2/(db1+db2)*dr2/(dr2+dr1);
								dataLUTScale[ix][usedColumnLength[ix]][1] = db1/(db1+db2)*dr2/(dr2+dr1);
								dataLUTScale[ix][usedColumnLength[ix]][2] = db2/(db1+db2)*dr1/(dr2+dr1);;
								dataLUTScale[ix][usedColumnLength[ix]][3] = db1/(db1+db2)*dr1/(dr2+dr1);
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
	
	// check the number of points is something we can handle. 
	private int checkNPoints(int nPoints) {
		switch (nPoints) {
		case 1:
			return 1;
		case 2:
			return 2;
		case 4:
			return 4;
		default:
			return 2;
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
		dataLUTScale = null;
		usedColumnLength = null;
	}

}
