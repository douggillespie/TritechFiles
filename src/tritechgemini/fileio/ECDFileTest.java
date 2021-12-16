package tritechgemini.fileio;

import java.io.IOException;

import tritechgemini.imagedata.ECDImageRecord;
import tritechgemini.imagedata.FanPicksFromData;
import tritechgemini.imagedata.GeminiImageRecord;
import tritechgemini.imagedata.GeminiImageRecordI;
import tritechgemini.imagedata.ImageFanMaker;

public class ECDFileTest {


	// laptop
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-085615.glf";
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\glfexamples\\data_2021-04-10-085615.dat";
	private static String ecdFile = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";
	// Office desktop
//	private static final String ecdFile = "E:\\RobSonar2021\\20210412\\data_2021-04-12-124025.ecd";
//	private static final String ecdFile = "E:\\RobSonar2021\\20210410\\log_2021-04-10-122102.glf";

	public static void main(String[] args) {

		long t1 = System.currentTimeMillis();
		GeminiFileCatalog ecdCatalog = GeminiFileCatalog.getFileCatalog(ecdFile, true);
		long t2 = System.currentTimeMillis();
		int nRec = ecdCatalog.getNumRecords();
		double t = (double) (t2-t1)/1000.;
		double tPerRec = t / (double) nRec;
		System.out.printf("%d records catalogued in %3.3fs, thats %3.1fms per record\n", nRec, t, tPerRec*1000.);

		t1 = System.currentTimeMillis();
		//		% now load those records in turn and see how long that takes.
		ImageFanMaker fanMaker = new FanPicksFromData();
		int nDone = 0;
		try {
			for (int i = nRec-1; i >= 0; i--) {
				GeminiImageRecordI imRec = ecdCatalog.getFullRecord(i);
				byte[] imData = imRec.getImageData();
				nDone++;
				t2 = System.currentTimeMillis();
				imRec.freeImageData();
//				System.out.printf("Fully read %d records then took %d milliseconds = %3.1fms/rec\n", nDone, t2-t1, (double) (t2-t1)/nDone);
//				fanMaker.createFanData(imRec);
				//			break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t2 = System.currentTimeMillis();
		System.out.printf("Full read of all records then took %d milliseconds = %3.1fms/rec\n", t2-t1, (double) (t2-t1)/nRec);
		System.out.println(GLFFastInputStream.readMonitor.getSummary("Reading"));
		System.out.println(GLFFastInputStream.skipMonitor.getSummary("Skipping"));
		System.out.println(GLFFastInputStream.loadMonitor.getSummary("Loading"));
	}

}
