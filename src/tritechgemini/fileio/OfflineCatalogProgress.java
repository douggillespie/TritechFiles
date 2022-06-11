package tritechgemini.fileio;

/**
 * Progress info sent to observes when the offline file catalogue is being
 * updated. This is done from a multifilecatalog. 
 * @author dg50
 *
 */
public class OfflineCatalogProgress {

	private int state;
	private int currentFile;
	private String lastFile;
	private int totalFiles;
	private GeminiFileCatalog newCatalog;
	
	public OfflineCatalogProgress(int state, int totalFiles, int currentFile, String lastFile, GeminiFileCatalog newCatalog) {
		super();
		this.state = state;
		this.totalFiles = totalFiles;
		this.currentFile = currentFile;
		this.lastFile = lastFile;
		this.newCatalog = newCatalog;
	}
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @return the nFiles
	 */
	public int getCurrentFile() {
		return currentFile;
	}
	/**
	 * @return the lastFile
	 */
	public String getLastFileName() {
		return lastFile;
	}
	/**
	 * @return the totalFiles
	 */
	public int getTotalFiles() {
		return totalFiles;
	}
	/**
	 * A single file catalogue that's been added to a MultiFileCatalogue
	 * @return the newCatalog
	 */
	public GeminiFileCatalog getNewCatalog() {
		return newCatalog;
	}
	

}
