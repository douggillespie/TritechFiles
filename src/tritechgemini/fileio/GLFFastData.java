package tritechgemini.fileio;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class of serializable data that can be used to quick catalog 
 * the overall content of a glf file. 
 * @author dg50
 *
 */
public class GLFFastData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String cfgFileName, datFileName, xmlFileName;
	protected long cfgFilePos, datFilePos, xmlFilePos;
	protected long cfgFileLen, datFileLen, xmlFileLen;

	/**
	 * Start position of every block in the data file. Note that this
	 * is the data position, i.e. the position in the unpacked dat file. The 
	 * actual position in the file will always be (5*(blockNo+1)) further into 
	 * the actual input file plus the file start offset datFilePos. 
	 */
	protected ArrayList<GLFFastBlockData> datBlockStarts = new ArrayList<GLFFastBlockData>();
}
