package tritechgemini.fileio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import tritechgemini.imagedata.FanPicksFromData;
import tritechgemini.imagedata.GeminiImageRecordI;
import tritechgemini.imagedata.ImageFanMaker;


public class ECDFileTest {


	// laptop
	//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";
	//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-085615.glf";
//			private static final String ecdFile = "C:\\Meygen\\DavyPierTest\\Gemini\\20201110\\100016_IMG.ecd";
	//	private static final String ecdFile = "E:\\RobSonar2021\\20210410";//
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\20211227\\log_2021-12-27-162222.glf";
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\20211227\\log_2021-12-27-143452.glf";
//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\log_2021-12-09-190821.glf";
//		private static final String ecdFile = "C:\\ProjectData\\RobRiver\\20211227";
		private static final String ecdFile = "C:\\PAMGuardTest\\glftest\\20220301\\data_2022-03-01-141949.dat";
	//	private static final String ecdFile = "C:\\ProjectData\\RobRiver\\glfexamples\\data_2021-04-10-085615.dat";
	//	private static String ecdFile = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";
	// Office desktop
	//	private static final String ecdFile = "E:\\RobSonar2021\\20210412\\data_2021-04-12-124025.ecd";
	//	private static final String ecdFile = "E:\\RobSonar2021\\20210410\\log_2021-04-10-122102.glf";

	public static void main(String[] args) {

		new ECDFileTest().run();

	}

	private void run() {
		long t1 = System.currentTimeMillis();
		GeminiFileCatalog ecdCatalog = null;
		File fileList = new File(ecdFile);
		int nFile = 0;
		int nRec = 0;
		long minTime = 0;
		long maxTime = 0;
		if (fileList.isDirectory()) {
			File[] files = fileList.listFiles();
			String[] gemFiles = new String[files.length];

			for (int i = 0; i < files.length; i++) {
				String path = files[i].getAbsolutePath();
				if (path.endsWith(".ecd") == false && path.endsWith("glf") == false)  {
					continue;
				}
				gemFiles[nFile++] = path;

				//				try {
				////					System.out.println(path);
				//					ecdCatalog = catalogFile(path);
				//				} catch (CatalogException e) {
				//					e.printStackTrace();
				//				}
				//				if (minTime == 0) {
				//					minTime = ecdCatalog.getFirstRecordTime();
				//				}
				//				maxTime = ecdCatalog.getLastRecordTime();
				//				nRec += ecdCatalog.getNumRecords();
			}
			gemFiles = Arrays.copyOf(gemFiles, nFile);
			MultiFileCatalog mfc = new MultiFileCatalog();
			mfc.catalogFiles(gemFiles);
			minTime = mfc.getRecord(0).getRecordTime();
			nRec = mfc.getTotalRecords();
			maxTime = mfc.getRecord(nRec-1).getRecordTime();
		}
		else {
			nFile = 1;
			try {
				ecdCatalog = catalogFile(ecdFile);
			} catch (CatalogException e) {
				e.printStackTrace();
			}
			nRec += ecdCatalog.getNumRecords();
			minTime = ecdCatalog.getFirstRecordTime();
			maxTime = ecdCatalog.getLastRecordTime();
		}
		long t2 = System.currentTimeMillis();
		double t = (double) (t2-t1)/1000.;
		double tPerRec = t / (double) nRec;
		double secsData = (double) (maxTime-minTime)/1000;
		System.out.printf("%d records in %d files for %3.1f secs data catalogued in %3.3fs, thats %3.3fms per record\n", nRec, nFile, secsData, t, tPerRec*1000.);

		t1 = System.currentTimeMillis();
		//		% now load those records in turn and see how long that takes.
		ImageFanMaker fanMaker = new FanPicksFromData(4);
		int nDone = 0;
		GeminiImageRecordI imRec = null;
		if (ecdCatalog != null) {
			try {
				for (int i = nRec-1; i >= 0; i--) {
					imRec = ecdCatalog.getFullRecord(i);
					byte[] imData = imRec.getImageData();
					nDone++;
					t2 = System.currentTimeMillis();
					//				System.out.printf("Fully read %d records size %dx%d then took %d milliseconds = %3.1fms/rec\n", nDone, 
					//						imRec.getnBeam(), imRec.getnRange(), t2-t1, (double) (t2-t1)/nDone);
					fanMaker.createFanData(imRec);
					imRec.freeImageData();
					//				break;
					//			break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t2 = System.currentTimeMillis();
			System.out.printf("Full read of all records then took %d milliseconds = %3.1fms/rec\n", t2-t1, (double) (t2-t1)/nRec);

		}
		System.out.println(GLFFastInputStream.readMonitor.getSummary("Reading"));
		System.out.println(GLFFastInputStream.skipMonitor.getSummary("Skipping"));
		System.out.println(GLFFastInputStream.loadMonitor.getSummary("Loading"));
		ecdCatalog = null;
		System.gc();
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private GeminiFileCatalog catalogFile(String filePath) throws CatalogException {
		return GeminiFileCatalog.getFileCatalog(filePath, true);
	}


}
