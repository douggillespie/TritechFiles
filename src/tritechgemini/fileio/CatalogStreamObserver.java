package tritechgemini.fileio;

import tritechgemini.imagedata.GLFStatusData;
import tritechgemini.imagedata.GeminiImageRecordI;

public interface CatalogStreamObserver {

	void newImageRecord(GeminiImageRecordI glfImage);

	void newStatusData(GLFStatusData statusData);

}
