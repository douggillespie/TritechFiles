package tritechgemini.fileio;

import tritechgemini.imagedata.GLFStatusData;
import tritechgemini.imagedata.SonarImageRecordI;

public interface CatalogStreamObserver {

	/**
	 * Process a new image record. 
	 * @param glfImage
	 * @return true if process streaming should continue, false otherwise. 
	 */
	boolean newImageRecord(SonarImageRecordI glfImage);

	/**
	 * Process new status data
	 * @param statusData
	 * @return true if process streaming should continue, false otherwise. 
	 */
	boolean newStatusData(GLFStatusData statusData);

}
