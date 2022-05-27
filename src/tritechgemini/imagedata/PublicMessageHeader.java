package tritechgemini.imagedata;

import java.io.Serializable;

import tritechgemini.fileio.GLFGenericHeader;

public class PublicMessageHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	public GLFGenericHeader genericHeader;

	public PublicMessageHeader(GLFGenericHeader genericHeader) {
		super();
		this.genericHeader = genericHeader;
	}
	

}
