package tritechgemini.fileio;

import java.io.File;
import java.io.FileFilter;


public class SonarFileFilter implements FileFilter {
	
	String[] ends = {".glf", ".ecd", ".aris"};

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String name = f.getName();
		name = name.toLowerCase();
		for (int i = 0; i < ends.length; i++) {
			if (name.endsWith(ends[i])) {
				return true;
			}
		}
		return false;
	}

}
