package tritechgemini.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * Catalog of information that's in a single Gemini ECD of GLF file. 
 * @author dg50
 *
 */
public abstract class GeminiFileCatalog<RecordClass extends GeminiImageRecordI> implements Serializable {


	private static final long serialVersionUID = 1L;

	private String filePath;
	
	private ArrayList<RecordClass> imageRecords = null;
	
	private transient Exception catalogException;
	
	/**
	 * Hash map of sonars, identified by the sonar Id (not it's index)
	 */
	private HashMap<Integer, CatalogSonarInfo> sonarMap = new HashMap<>();
	
	public static final String ECDEND = ".ecd"; 
	public static final String GLFEND = ".glf"; 
	public static final String DATEND = ".dat"; 
		
	public GeminiFileCatalog(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * 
	 * Preferred way of getting a file catalogue, since it will automatically 
	 * handle ECD, GLF and DAT files. 
	 * @param filePath 
	 * @param create build the catalogue for the file immediately. 
	 * @return file catalogue of null if the file doesn't exist or is an unknown type. 
	 * @throws CatalogException if file is null or there is a failure cataloguing it. 
	 */
	public static GeminiFileCatalog getFileCatalog(String filePath, boolean create)  throws CatalogException {
		
		GeminiFileCatalog exCatalog = readSerializedCatalog(filePath);
		if (exCatalog != null) {
			return exCatalog;
		}
		
		File file = new File(filePath);
		if (file.exists() == false) {
			throw new CatalogException("File " + filePath + " does not exist");
		}
		String fEnd = filePath.substring(filePath.length()-4, filePath.length());
		GeminiFileCatalog fileCatalog = null;
		switch (fEnd) {
		case ECDEND:
			fileCatalog = new ECDFileCatalog(filePath);
			break;
		case GLFEND:
		case DATEND:
			fileCatalog = new GLFFileCatalog(filePath);
			break;
		}
		if (fileCatalog != null && create) {
			fileCatalog.createCatalogue();
			writeSerializedCatalog(filePath, fileCatalog);
		}
		return fileCatalog;
	}
	
	/**
	 * Read a Gemini file catalogue record for the given data file
	 * @param filePath path of data (ecd or glf) file
	 * @return catalog information read from file. 
	 */
	public static GeminiFileCatalog readSerializedCatalog(String filePath) {
		File catFile = new File(getCatalogName(filePath));
		if (catFile.exists() == false) {
			return null;
		}
		Object obj = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(catFile));
			obj = ois.readObject();
			ois.close();
		} catch (IOException e) {
			System.out.println("Unable to open Gemini catalogue file: " + e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to read Gemini catalogue object: " + e.getMessage());
			return null;
		}
		if (obj instanceof GeminiFileCatalog) {
			return (GeminiFileCatalog) obj;
		}
		return null;
	}

	/**
	 * Write a Gemini file Catalogue
	 * @param filePath path of data file (.ecd or .glf)
	 * @param fileCatalog file catalogue to write
	 * @return true if successful write. 
	 */
	public static boolean writeSerializedCatalog(String filePath, GeminiFileCatalog fileCatalog) {
		File catFile = new File(getCatalogName(filePath));
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(catFile));
			oos.writeObject(fileCatalog);
			oos.close();
		} catch (IOException e) {
			System.out.println("Unable to write Gemini catalogue object: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Get a name for a serialized catalog file. this is just the normal file 
	 * name with .cat on the end. 
	 * @param dataFileName Data file name. 
	 * @return name of catalog file.
	 */
	public static String getCatalogName(String dataFileName) {
		return dataFileName + "x";
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
		analyseCatalog();
		return ok;
	}
	
	/**
	 * Get the number of sonars, their id's and types, etc. 
	 */
	private void analyseCatalog() {
		if (imageRecords == null) {
			return;
		}
				
		int iRec = 0;
		for (RecordClass aRec : imageRecords) {
			int deviceId = aRec.getDeviceId();
			int devIndex = aRec.getSonarIndex();
//			System.out.printf("id %d no %d\n", deviceId, devNo);
			CatalogSonarInfo sonarInfo = sonarMap.get(deviceId);
			if (sonarInfo == null) {
				sonarInfo = new CatalogSonarInfo(devIndex, deviceId, iRec);
				sonarMap.put(deviceId, sonarInfo);
			}
			sonarInfo.addFrame();
			
			iRec++;
		}
		
		Set<Integer> keySet = sonarMap.keySet();
//		for (Integer key : keySet) {
//			CatalogSonarInfo sonarInfo = sonarMap.get(key);
//			System.out.println(sonarInfo.toString());
//		}
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
	 * the record. Also note, that this is an overall index and you might be 
	 * better off using the getSonarRecord functions
	 * @param recordIndex
	 * @return Gemini record. 
	 */
	public RecordClass getRecord(int recordIndex) {
		RecordClass rec = imageRecords.get(recordIndex);
		return rec;
	}
	
	/**
	 * Get a record for a specified sonar
	 * @param sonarID
	 * @param sonarRecord
	 * @return
	 */
	public RecordClass getSonarRecord(int sonarID, int sonarRecord) {
		// just incase they are not sequential, work through from the start and count
		int n = 0;
		for (RecordClass aRec : imageRecords) {
			if (aRec.getDeviceId() != sonarID) {
				continue;
			}
			if (n++ == sonarRecord) {
				return aRec;
			}
		}
		return null;
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

	public static long cDateToMillis(double cDate) {
//		cDate is ref's to 1980 in secs, Java in millis from 1970. 
		int days = 3652;
		int secsPerDay = 3600*24;
		return (long) ((cDate+days*secsPerDay)*1000.);
	}
	
	/**
	 * Get the number of sonars in the catalogue. 
	 * @return number of sonars in the catalogue. 
	 */
	public int getNumSonars() {
		return sonarMap.size();
	}
	
	/**
	 * 
	 * @return The maximum number of frames for any single sonar
	 */
	public int getMaxSonarFrames() {
		Collection<CatalogSonarInfo> sonars = sonarMap.values();
		int n = 0;
		for (CatalogSonarInfo inf : sonars) {
			n = Math.max(n, inf.getnFrames());
		}
		return n;
	}
	
	/**
	 * Get the summary info for a single sonar. 
	 * @param sonarID
	 * @return summary info for a single conar. 
	 */
	public CatalogSonarInfo getSonarInfo(int sonarID) {
		return sonarMap.get(sonarID);
	}
	
	/**
	 * Get the ID's of the sonars in this catalogue. 
	 * @return array of IDs (these are the things written on the sonar, not their indexes)
	 */
	public int[] getSonarIDs() {
		Collection<CatalogSonarInfo> sonars = sonarMap.values();
		int[] ids = new int[sonars.size()];
		int i = 0;
		for (CatalogSonarInfo inf : sonars) {
			ids[i++] = inf.getSonarId();
		}
		return ids;
	}

	/**
	 * find the closest record to the given time for the sonar ID
	 * @param sonarID sonar ID
	 * @param timeMillis time in milliseconds 
	 * @return closest record or null
	 */
	public GeminiImageRecordI findRecordForTime(int sonarID, long timeMillis) {
		long dT = Long.MIN_VALUE;
		RecordClass bestRec = null;
		for (RecordClass aRec : imageRecords) {
			if (aRec.getDeviceId() != sonarID) {
				continue;
			}
			long t = aRec.getRecordTime()-timeMillis;
			if (Math.abs(t) < Math.abs(dT)) {
				bestRec = aRec;
				dT = t;
			}
			if (t > 0) {
				break;
			}
		}
		if (bestRec == null) {
			return null;
		}
		if (bestRec.isFullyLoaded() == false) {
			try {
				loadFullRecord(bestRec);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bestRec;
	}
	
	
}
