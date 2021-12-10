package tritechgemini.fileio;

import java.io.IOException;

import tritechgemini.imagedata.ECDImageRecord;
import tritechgemini.imagedata.FanPicksFromData;
import tritechgemini.imagedata.ImageFanMaker;

public class ECDFileTest {


	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";

	public static void main(String[] args) {

		long t1 = System.currentTimeMillis();
		ECDFileCatalog ecdCatalog = new ECDFileCatalog(ecdFile);
		boolean catOK = ecdCatalog.createCatalogue();
		long t2 = System.currentTimeMillis();
		int nRec = ecdCatalog.getNumRecords();
		double t = (double) (t2-t1)/1000.;
		double tPerRec = t / (double) nRec;
		System.out.printf("%d records catalogues in %3.1fs, thats %3.1fms per record\n", nRec, t, tPerRec*1000.);

		t1 = System.currentTimeMillis();
		//		% now load those records in turn and see how long that takes.
		ImageFanMaker fanMaker = new FanPicksFromData();
		try {
			for (int i = 0; i < nRec; i++) {
				ECDImageRecord imRec = ecdCatalog.getFullRecord(i);
				byte[] imData = imRec.getImageData();
				fanMaker.createFanData(imRec);
				//			break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2 = System.currentTimeMillis();
		System.out.printf("Full read of all records then took %d milliseconds = %3.1fms/rec\n", t2-t1, (double) (t2-t1)/nRec);
	}

}
