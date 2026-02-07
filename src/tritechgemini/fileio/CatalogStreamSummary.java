package tritechgemini.fileio;

/**
 * Lump of data to send back from a GeminiCatalog.streamCatalog function which 
 * will give (or attempt to give) the state of the streaming, which may not
 * be complete, or may have been aborted for some reason. 
 * @author Doug Gillespie
 *
 */
public class CatalogStreamSummary {

	public static final int PROCESSSTOP = 1;
	public static final int FILEEND = 2;
	public static final int READERROR = 3;
	public static final int DATAGAP =4;
	
	public long firstRecordTime;
	public long lastRecordTime;
	public int recordsStreamed;
	public int endReason;
	
	public CatalogStreamSummary(int recordsStreamed, long firstRecordTime, long lastRecordTime, int endReason) {
		super();
		this.recordsStreamed = recordsStreamed;
		this.firstRecordTime = firstRecordTime;
		this.lastRecordTime = lastRecordTime;
		this.endReason = endReason;
	}

	
}
