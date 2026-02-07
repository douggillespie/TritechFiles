package aris;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import aris.beams.ARISBeamData;
import tritechgemini.fileio.CountingInputStream;
import tritechgemini.fileio.LittleEndianDataInputStream;

/**
 * Test reading of ARIS data files.
 * See https://github.com/SoundMetrics/aris-file-sdk/blob/master/docs/understanding-aris-data.md
 * @author Doug Gillespie
 *
 */
public class ARISFile {

	
	private String fileName;
	private CountingInputStream cis;
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the fileHeader
	 */
	public ARISFileHeader getFileHeader() {
		return fileHeader;
	}

	private ARISFileHeader fileHeader;

	public ARISFile(String fileName) {
		this.fileName = fileName;
	}

	public static void main(String[] args) {
		String fn = "C:\\ProjectData\\RobRiver\\ARIS\\A Salmon (3).aris";
		try {
			new ARISFile(fn).unpack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void unpack() throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(fileName));
		DataInput dis = new LittleEndianDataInputStream(cis = new CountingInputStream(is));
		fileHeader = new ARISFileHeader(fileName);
		fileHeader.readHeader(dis);
		ARISBeamData beamData = ARISBeamData.getBeamData(fileHeader.getNumRawBeams());
		int nFrames = 0;
		ArrayList<ARISFrameHeader> headers = new ArrayList<>();
		while (true) {
			try {
				ARISFrameHeader frameHeader = new ARISFrameHeader(cis.getPos());
				frameHeader.readHeader(dis);
				headers.add(frameHeader);
				int dataSize = frameHeader.getSamplesPerBeam() * fileHeader.getNumRawBeams();
				dis.skipBytes(dataSize);
				nFrames ++;
			}
			catch (EOFException e) {
//				e.printStackTrace();
				break;
			}
		}
		System.out.printf("%d frames read from file %s\n", nFrames, fileName);
		is.close();
	}

}
