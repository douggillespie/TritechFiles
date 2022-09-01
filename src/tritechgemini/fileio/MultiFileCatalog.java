package tritechgemini.fileio;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Catalog of gemini data which can take multiple files. 
 * @author dg50
 *
 */
public class MultiFileCatalog implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<GeminiFileCatalog> catalogList;
	
	private transient ArrayList<CatalogObserver> catalogObservers;
	
	private HashMap<Integer, CatalogSonarInfo> allSonarInfo = new HashMap<Integer, CatalogSonarInfo>();

	private volatile boolean stopCataloging;

	/**
	 * constructor doesn't actually do any cataloging. 
	 * Call catalogFiles(...) to build it. 
	 */
	public MultiFileCatalog() {
		super();
		catalogList = new ArrayList<>();
		catalogObservers = new ArrayList<>();
	}
	
	/**
	 * Build a catalog from a list of files. 
	 * @param fileList
	 */
	public void catalogFiles(String[] fileList) {
		catalogList.clear();
		allSonarInfo.clear();
//		if (fileList.length> 0) {
//			notifyObservers(CatalogObserver.BUILDING, catalogList.size(), 1, fileList[0]);
//		}
		for (int i = 0; i < fileList.length; i++) {
			GeminiFileCatalog cat = null;
			try {
//				System.out.println("Catalog " + fileList[i]);
				cat = GeminiFileCatalog.getFileCatalog(fileList[i], true);
				// send notification after building so can send the cat 
				// information for a gradual increase in the data map. 
				notifyObservers(new OfflineCatalogProgress(CatalogObserver.BUILDING, fileList.length, i+1, fileList[i], cat));
			} catch (CatalogException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cat != null) {
				catalogList.add(cat);
				int[] sonars = cat.getSonarIDs();
				for (int is = 0; is < sonars.length; is++) {
					CatalogSonarInfo sonarInf = cat.getSonarInfo(sonars[is]);
					CatalogSonarInfo exInfo = allSonarInfo.get(sonarInf.getSonarId());
					if (exInfo == null) {
						exInfo = sonarInf.clone();
						allSonarInfo.put(sonars[is], exInfo);
					}
					else {
						exInfo.addFrameCount(sonarInf.getnFrames());
					}
				}
				
			}
			if (stopCataloging) {
				break;
			}
		}
		
		// sort out summary info of all sonars present. 
//		for (GeminiFileCatalog aCat : catalogList) {
//			int[] sonars = aCat.getSonarIDs();
//			for (int i = 0; i < sonars.length; i++) {
//				CatalogSonarInfo sonarInf = aCat.getSonarInfo(sonars[i]);
//				CatalogSonarInfo exInfo = allSonarInfo.get(sonarInf.getSonarId());
//				if (exInfo == null) {
//					exInfo = sonarInf.clone();
//					allSonarInfo.put(sonars[i], exInfo);
//				}
//				else {
//					exInfo.addFrameCount(sonarInf.getnFrames());
//				}
//			}
//		}
		
		notifyObservers(new OfflineCatalogProgress(CatalogObserver.COMPLETE, catalogList.size(), catalogList.size(), null, null));
	}
	
	/**
	 * Get the total number of records. 
	 * @return
	 */
	public int getTotalRecords() {
		int n = 0;
		for (int i = 0; i < catalogList.size(); i++) {
			n += catalogList.get(i).getNumRecords();
		}
		return n;
	}
	
	/**
	 * Get max records for a single sonar. 
	 * @return max records for a single sonar. 
	 */
	public int getMaxDeviceRecords() {
		Collection<CatalogSonarInfo> catSet = allSonarInfo.values();
		int n = 0;
		for (CatalogSonarInfo inf : catSet) {
			n = Math.max(n, inf.getnFrames());
		}
		return n;
	}
	
	/**
	 * Get the ith record from the total catalog.  Load full record. 
	 * @param iRecord record index. 
	 * @return ith record or null if it doesn't exit. 
	 */
	public GeminiImageRecordI getRecord(int iRecord) {
//		int n = 0;
//		int counted1 = 0, counted2;
//		for (int i = 0; i < catalogList.size(); i++) {
//			counted2 = counted1 + catalogList.get(i).getNumRecords();
//			if (iRecord >= counted1 && iRecord < counted2) {
//				try {
//					return catalogList.get(i).getFullRecord(iRecord-counted1);
//				} catch (IOException e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
//			counted1 = counted2;
//		}
//		return null;
		return getRecord(iRecord, true);
	}

	/**
	 * Get the ith record from the total catalog.. 
	 * @param iRecord record index. 
	 * @param loadFully load the full record data (don't do this for too many units at once)
	 * @return ith record or null if it doesn't exit. 
	 */
	public GeminiImageRecordI getRecord(int iRecord, boolean loadFully) {
		int n = 0;
		int counted1 = 0, counted2;
		for (int i = 0; i < catalogList.size(); i++) {
			counted2 = counted1 + catalogList.get(i).getNumRecords();
			if (iRecord >= counted1 && iRecord < counted2) {
				if (loadFully) {
				try {
					return catalogList.get(i).getFullRecord(iRecord-counted1);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				}
				else {
					return catalogList.get(i).getRecord(iRecord-counted1);
				}
			}
			counted1 = counted2;
		}
		return null;
	}
	
	/**
	 * 
	 * Get the ith record for the specified sonar ...
	 */
	public GeminiImageRecordI getSonarRecord(int sonarID, int iRecord) {
		int n = 0;
		int counted1 = 0, counted2;
		for (int i = 0; i < catalogList.size(); i++) {
			GeminiFileCatalog catalog = catalogList.get(i);
			CatalogSonarInfo devInfo = catalog.getSonarInfo(sonarID);
			if (devInfo == null) {
				continue;
			}
			counted2 = counted1 + devInfo.getnFrames();
			if (iRecord >= counted1 && iRecord < counted2) {
				try {
					GeminiImageRecordI record = catalog.getSonarRecord(sonarID, iRecord-counted1);
					if (record == null) {
						return null;
					}
					if (record.isFullyLoaded() == false) {
						catalog.timedLoadFullRecord(record);
					}
					return record;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			counted1 = counted2;
		}
		return null;
	}
	
	/**
	 * Find the closest record for the given sonar id to the time in milliseconds
	 * @param sonarID sonar ID
	 * @param timeMillis time milliseconds
	 * @return record or null
	 */
	public GeminiImageRecordI findRecordForTime(int sonarID, long timeMillis) {

		for (int i = 0; i < catalogList.size(); i++) {
			GeminiFileCatalog catalog = catalogList.get(i);
			long firstTime = catalog.getFirstRecordTime() ;
			long lastTime = catalog.getLastRecordTime() ;
			
			if (timeMillis < firstTime ) {
				continue;
			}
			if (timeMillis > lastTime) {
				continue;
			}
			return catalog.findRecordForIDandTime(sonarID, timeMillis);
		}
		return null;
	}

	/**
	 * Used when scrolling by record number. Allows to take the current time, then move by a small number 
	 * of records forwards or backwards. 
	 * @param sonarID
	 * @param timeMillis
	 * @param recordOffest
	 * @return relative image record in list. 
	 */
	public GeminiImageRecordI findRelativeRecord(GeminiImageRecordI baseRecord, int recordOffset) {
		if (baseRecord == null) {
			return null;
		}
		int currentCatalogIndex = -1;
		long timeMillis = baseRecord.getRecordTime();
		for (int i = 0; i < catalogList.size(); i++) {
			GeminiFileCatalog catalog = catalogList.get(i);
			long firstTime = catalog.getFirstRecordTime() ;
			long lastTime = catalog.getLastRecordTime() ;
			
			if (timeMillis < firstTime ) {
				continue;
			}
			if (timeMillis > lastTime) {
				continue;
			}
			currentCatalogIndex = i;
			break;
		}
		if (currentCatalogIndex < 0) {
			return null;
		}
		GeminiFileCatalog currentCatalog = catalogList.get(currentCatalogIndex);
		int currInd = currentCatalog.getRecordIndex(baseRecord);
		if (currInd < 0) {
			return null;
		}
		currInd += recordOffset;
		while (currInd < 0) {
			currentCatalogIndex--;
			if (currentCatalogIndex < 0) {
				return null;
			}
			currentCatalog = catalogList.get(currentCatalogIndex);
			currInd += currentCatalog.getNumRecords();
		}
		while (currInd >= currentCatalog.getNumRecords()) {
			currInd -= currentCatalog.getNumRecords();
			currentCatalogIndex++;
			if (currentCatalogIndex >= catalogList.size()) {
				return null;
			}
			currentCatalog = catalogList.get(currentCatalogIndex);
		}
		return currentCatalog.getRecordByIndex(currInd);
	}
	
	/**
	 * Get the ID's of the sonars in this catalogue. 
	 * @return array of IDs (these are the things written on the sonar, not their indexes)
	 */
	public int[] getSonarIDs() {
		Collection<CatalogSonarInfo> sonars = allSonarInfo.values();
		int[] ids = new int[sonars.size()];
		int i = 0;
		for (CatalogSonarInfo inf : sonars) {
			ids[i++] = inf.getSonarId();
		}
		return ids;
	}
	
	public ArrayList<GeminiFileCatalog> getCatalogList() {
		return catalogList;
	}

	public void addObserver(CatalogObserver observer) {
		this.catalogObservers.add(observer);
	}
	
	public void removeObserver(CatalogObserver observer) {
		this.catalogObservers.remove(observer);
	}
	
	private void notifyObservers(OfflineCatalogProgress offlineCatalogProgress) {
		for (int i = 0; i < catalogObservers.size(); i++) {
			catalogObservers.get(i).catalogChanged(offlineCatalogProgress);
		}
	}
	
	/**
	 * Free image data from all records, but with a window around the
	 * time of interest. 
	 * @param currentTime current time (in Viewer ?)
	 * @param timeWinMillis time window about current time. 
	 */
	public void freeImageData(long currentTime, long timeWinMillis) {
		for (int i = 0; i < catalogList.size(); i++) {
			GeminiFileCatalog catalog = catalogList.get(i);
			catalog.freeImageData(currentTime, timeWinMillis);
		}
	}

	public void stopCataloging() {
		stopCataloging = true;
	}
	
	
}
