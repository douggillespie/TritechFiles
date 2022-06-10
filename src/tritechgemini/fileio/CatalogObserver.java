package tritechgemini.fileio;

public interface CatalogObserver {

	public static final int BUILDING = 0;
	public static final int COMPLETE = 1;
	public static final int CLEARED = 2;
	
	/**
	 * Notification sent as a catalog is being built. 
	 * @param state will be one of BUILDING, COMPLETE or CLEARED
	 * @param nFiles numbe of files currently in catalog. 
	 * @param lastFile name of last file added. 
	 */
	public void catalogChanged(int state, int nFiles, String lastFile);
	
}
