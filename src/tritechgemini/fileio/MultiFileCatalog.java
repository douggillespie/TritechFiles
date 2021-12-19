package tritechgemini.fileio;

import java.io.IOException;
import java.util.ArrayList;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Catalog of gemini data which can take multiple files. 
 * @author dg50
 *
 */
public class MultiFileCatalog {

	private ArrayList<GeminiFileCatalog> catalogList;
	
	private ArrayList<CatalogObserver> catalogObservers;

	public MultiFileCatalog() {
		super();
		catalogList = new ArrayList<>();
		catalogObservers = new ArrayList<>();
	}
	
	public void catalogFiles(String[] fileList) {
		catalogList.clear();
		for (int i = 0; i < fileList.length; i++) {
			GeminiFileCatalog cat = null;
			try {
				cat = GeminiFileCatalog.getFileCatalog(fileList[i], true);
			} catch (CatalogException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cat != null) {
				catalogList.add(cat);
			}
		}
		notifyObservers();
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
	 * Get the ith record from the total catalog.  Load full record. 
	 * @param iRecord record index. 
	 * @return ith record or null if it doesn't exit. 
	 */
	public GeminiImageRecordI getRecord(int iRecord) {
		int n = 0;
		int counted1 = 0, counted2;
		for (int i = 0; i < catalogList.size(); i++) {
			counted2 = counted1 + catalogList.get(i).getNumRecords();
			if (iRecord >= counted1 && iRecord < counted2) {
				try {
					return catalogList.get(i).getFullRecord(iRecord-counted1);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			counted1 = counted2;
		}
		return null;
		
	}
	
	public void addObserver(CatalogObserver observer) {
		this.catalogObservers.add(observer);
	}
	
	private void notifyObservers() {
		for (int i = 0; i < catalogObservers.size(); i++) {
			catalogObservers.get(i).catalogChanged();
		}
	}
	
	
}
