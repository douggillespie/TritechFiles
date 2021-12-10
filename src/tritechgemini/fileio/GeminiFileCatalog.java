package tritechgemini.fileio;

import java.io.IOException;
import java.util.ArrayList;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Catalog of information that's in a single Gemini ECD of GLF file. 
 * @author dg50
 *
 */
public abstract class GeminiFileCatalog<RecordClass extends GeminiImageRecordI> {

	private String filePath;
	
	private ArrayList<RecordClass> imageRecords = null;
	
	private Exception catalogException;
		
	public GeminiFileCatalog(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Catalogue the file. i.e. go through the file and get the times and 
	 * file positions of every record in the file. 
	 * @param imageRecords 
	 * @return true if catalog created successfully 
	 */
	abstract public boolean buildCatalogue(ArrayList<RecordClass> imageRecords) throws Exception;
	
	/**
	 * Catalogue the file. i.e. go through the file and get the times and 
	 * file positions of every record in the file. 
	 * @return true if catalog created successfully 
	 */
	public boolean createCatalogue() {
		boolean ok = true;
		if (imageRecords == null) {
			try {
				imageRecords = new ArrayList();
				ok = buildCatalogue(imageRecords);
			}
			catch (Exception e) {
				ok = false;
				catalogException = e;
			}
		}
		return ok;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public Exception getCatalogException() {
		return catalogException;
	}

	/**
	 * fully load a record (if it isn't already). This may involve going back
	 * to the file and getting and unpacking the raw data. 
	 * @param geminiRecord 
	 * @return true if load sucessful. 
	 * @throws IOException 
	 */
	abstract boolean loadFullRecord(RecordClass geminiRecord) throws IOException;
	
	/**
	 * Get the time of the first record
	 * @return the time of the first record
	 */
	public long getFirstRecordTime() {
		if (imageRecords == null || imageRecords.size() == 0) {
			return Long.MIN_VALUE;
		}
		return imageRecords.get(0).getRecordTime();
	}

	/**
	 * Get the time of the last record
	 * @return the time of the first record
	 */
	public long getLastRecordTime() {
		if (imageRecords == null || imageRecords.size() == 0) {
			return Long.MIN_VALUE;
		}
		return imageRecords.get(imageRecords.size()-1).getRecordTime();
	}
	
	/**
	 * 
	 * @return total number of records in the file
	 */
	public int getNumRecords() {
		if (imageRecords == null) {
			return 0;
		}
		return imageRecords.size();
	}
	
	/**
	 * Find the index of the closest record to the given time. 
	 * @param recordTime
	 * @return index of closest record to given time. 
	 */
	public int findRecordIndex(long recordTime) {
		if (imageRecords == null || imageRecords.size() == 0) {
			return -1;
		}
		long bestT = Math.abs(recordTime-imageRecords.get(0).getRecordTime());
		int closest = 0;
		for (int i = 1; i < imageRecords.size(); i++) {
			GeminiImageRecordI imRec = imageRecords.get(i);
			if (imRec.getRecordTime() < recordTime) {
				// if the records are before our record, then we always want it. 
				bestT = imRec.getRecordTime();
				closest = i;
				continue;
			}
			// otherwise we're after our time, so only want to look at one more. 	
			long dT = Math.abs(recordTime-imageRecords.get(0).getRecordTime());
			if (dT < bestT) {
				closest = i;
			}
			break; // always get out, since it's only going to get further away noe. 
		}
		
		return closest;
	}
	
	/**
	 * Get a record. Note that this does NOT automatically fully load 
	 * the record. 
	 * @param recordIndex
	 * @return Gemini record. 
	 */
	public RecordClass getRecord(int recordIndex) {
		RecordClass rec = imageRecords.get(recordIndex);
		return rec;
	}

	/**
	 * Get a record at given index, loading full data if required. 
	 * @param recordIndex
	 * @return fully loaded record. 
	 * @throws IOException 
	 */
	public RecordClass getFullRecord(int recordIndex) throws IOException {
		RecordClass rec = imageRecords.get(recordIndex);
		if (rec.isFullyLoaded() == false) {
			loadFullRecord(rec);
		}
		return rec;
	}
	
	
	
}
