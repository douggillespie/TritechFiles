package tritechgemini.fileio;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A repeat of a lot of stuff inthe PAMGUArd class OfflineFileList, but needed
 * in the standalone package, so recreating here. 
 * @author Doug Gillespie
 *
 */
public class SonarFileList {

	private boolean subFolders;
	private String root;
	private ArrayList<File> fileList;
	private SonarFileFilter sonarFileFilter = new SonarFileFilter();
//	
//	public static void main(String[] args) {
//		String tstFolder = "E:\\GLFYear6\\Trip2";
//		SonarFileList fl = new SonarFileList(tstFolder, true);
//		String[] names = fl.getFiles();
//		System.out.println("Found files: " + names.length);
//		
//	}

	public SonarFileList(String folder, boolean subFolders) {
		this.root = folder;
		this.subFolders = subFolders;
		fileList = new ArrayList<>();
		listFiles(new File(root), fileList, subFolders);
		fileList.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				String n1 = o1.getName();
				String n2 = o2.getName();
				return n1.compareTo(n2);
			}
		});
	}
	
	/**
	 * Get all the files as an array of strings
	 * @return
	 */
	public String[] getFiles() {
		String[] files = new String[fileList.size()];
		for (int i = 0; i < files.length; i++) {
			files[i] = fileList.get(i).getAbsolutePath();
		}
		return files;
	}

	private void listFiles(File root, ArrayList<File> fileList, boolean subFolders) {
		File[] files = root.listFiles(sonarFileFilter);
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File aFile = files[i];
			if (aFile.isDirectory()) {
				listFiles(aFile, fileList, subFolders);
			}
			else {
				fileList.add(aFile);
			}
		}
	}
	

}
