package tritechgemini.fileio;

public interface CatalogObserver {

	public static final int BUILDING = 0;
	public static final int COMPLETE = 1;
	public static final int CLEARED = 2;
	
	/**
	 * Notification sent as a catalog is being built. 
	 * @param offlineCatalogProgress info on last single file catalog added to multifilecatalog. 
	 */
	public void catalogChanged(OfflineCatalogProgress offlineCatalogProgress);
	
}
