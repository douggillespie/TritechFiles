package tritechgemini.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Functions to check and if necessary create the catalogue files for 
 * glf files and the dat file with them. 
 * @author dg50
 *
 */
public class GLFCatalogCheck {
	
	public static final int ADD_GLFFASTCATALOG = 1;
	public static final int ADD_DATFILECATALOG = 2;

	public GLFCatalogCheck() {
	}
	
	/**
	 * Check a file with a specific name. 
	 * @param filePath full path to file
	 * @return -1 for error, 0, 1, 2, or 3 as a bitmap of whether it was 
	 * necessary to create the glf and or the dat catalogues. 
	 */
	public int checkCatalogues(String filePath) {
		int added = 0;
		File glfFile = new File(filePath);
		if (glfFile.exists() == false) {
			return -1;
		}
		boolean isGLF = filePath.toLowerCase().endsWith(".glf");
		boolean isECD = filePath.toLowerCase().endsWith(".ecd");
		if (!(isGLF || isECD)) {
			return -1;
		}
		if (isGLF) {
			added += checkGLFFastCatalog(glfFile);
		}
		
		String glfCatPath = GeminiFileCatalog.getCatalogName(filePath);
		File gemCatFile = new File(glfCatPath);
		if (gemCatFile.exists() == false) {
			try {
				GeminiFileCatalog geminiCatalog = GeminiFileCatalog.getFileCatalog(filePath, true);
			} catch (CatalogException e) {
				e.printStackTrace();
				return -1;
			}
//			geminiCatalog.freeAllImageData();
			added |= ADD_DATFILECATALOG;
		}
		
		return added;
	}
	
	/**
	 * Check the catalogue of the zipped glf file. 
	 * @param glfFile
	 * @return -1 for error, 0 or 1 if catalogue added. 
	 */
	public int checkGLFFastCatalog(File glfFile) {
		int added = 0;
		File fastCatFile = GLFFastInputStream.getGlfFastFile(glfFile);
		if (fastCatFile.exists() == false) {
			try {
				GLFFastInputStream fis = new GLFFastInputStream(glfFile);
				fis.close();
				added |= ADD_GLFFASTCATALOG;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return added;
	}

}
