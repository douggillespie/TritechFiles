package aris.fileio;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import aris.ARISFileHeader;
import aris.ARISFrameHeader;
import aris.ARISImageRecord;
import aris.beams.ARISBeamData;
import tritechgemini.fileio.CatalogException;
import tritechgemini.fileio.CatalogStreamObserver;
import tritechgemini.fileio.CatalogStreamSummary;
import tritechgemini.fileio.CountingInputStream;
import tritechgemini.fileio.GeminiFileCatalog;
import tritechgemini.fileio.LittleEndianDataInputStream;
import tritechgemini.imagedata.GLFImageRecord;
import tritechgemini.imagedata.GeminiImageRecordI;

/**
 * File catalog for ARIS sonar files. file format is very simple compared
 * to Tritech files. Details are at https://github.com/SoundMetrics/aris-file-sdk/tree/master
 * @author dg50
 *
 */
public class ARISFileCatalog extends GeminiFileCatalog<ARISImageRecord> {

	private static final long serialVersionUID = 1L;
	
	private ARISFileHeader fileHeader;
	private volatile boolean continueStream;

	public ARISFileCatalog(String filePath) {
		super(filePath);
	}

	@Override
	protected void checkDeserialisedCatalog(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean buildCatalogue(ArrayList<ARISImageRecord> imageRecords) throws Exception {
		InputStream is = new BufferedInputStream(new FileInputStream(getFilePath()));
		CountingInputStream cis;
		DataInput dis = new LittleEndianDataInputStream(cis = new CountingInputStream(is));
		fileHeader = new ARISFileHeader(getFilePath());
		fileHeader.readHeader(dis);
		ARISBeamData beamData = ARISBeamData.getBeamData(fileHeader.getNumRawBeams());
		double[] bearingTable = beamData.getBeamCentresRadians();
		int nFrames = 0;
		while (true) {
			try {
				ARISFrameHeader frameHeader = new ARISFrameHeader(cis.getPos());
				frameHeader.readHeader(dis);
				ARISImageRecord im = new ARISImageRecord(fileHeader, frameHeader, bearingTable);
				imageRecords.add(im);
				int dataSize = frameHeader.getSamplesPerBeam() * fileHeader.getNumRawBeams();
				dis.skipBytes(dataSize);
				nFrames ++;
			}
			catch (EOFException e) {
				//			e.printStackTrace();
				break;
			}
		}
		//	System.out.printf("%d frames read from file %s\n", nFrames, fileName);
		is.close();
		return true;
	}

	@Override
	public boolean loadFullRecord(ARISImageRecord arisRecord) throws IOException {
		/*
		 * Gets a little complex since the ARIS data often doesn't start until some min
		 * distance (unlike Tritech - though I think Tritech support this too !). 
		 * So for now, pack the data at low values to fill the fan, but do something more
		 * clever later on. 
		 */
		InputStream is = new BufferedInputStream(new FileInputStream(getFilePath()));
		DataInput dataInput = new LittleEndianDataInputStream(is);
		ARISFrameHeader frameHead = arisRecord.getFrameHeader();
		dataInput.skipBytes((int) frameHead.getFilePosition());
		readFullRecord(arisRecord, dataInput);
		is.close();
		return true;
	}
	private boolean readFullRecord(ARISImageRecord arisRecord, DataInput dataInput) throws IOException {
		long nStart = System.nanoTime();
		ARISFileHeader fileHead = arisRecord.getFileHeader();
		ARISFrameHeader frameHead = arisRecord.getFrameHeader();

		frameHead.readHeader(dataInput);
		int nBeam = fileHead.getNumRawBeams();
		int dataSize = frameHead.getSamplesPerBeam() * nBeam;
		/*
		 * sort out any packing that's needed. 
		 */
		double rPerM =  frameHead.getSamplesPerBeam() / (frameHead.getWindowLength());
		int extras = (int) (frameHead.getWindowStart() * rPerM);
		int extraBytes = extras * nBeam;

		byte[] data = new byte[dataSize + extraBytes];
		dataInput.readFully(data,extraBytes,dataSize);
		arisRecord.setExtraRanges(extras);
		arisRecord.setImageData(data);
		long nEnd = System.nanoTime();
		arisRecord.setLoadTime(nEnd-nStart);
		return true;
	}

	@Override
	public CatalogStreamSummary streamCatalog(CatalogStreamObserver streamObserver) throws CatalogException {
		int nFrames = 0;
		long firstTime = 0, lastTime = 0;
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(getFilePath()));
			CountingInputStream cis;
			DataInput dis = new LittleEndianDataInputStream(cis = new CountingInputStream(is));
			fileHeader = new ARISFileHeader(getFilePath());
			fileHeader.readHeader(dis);
			ARISBeamData beamData = ARISBeamData.getBeamData(fileHeader.getNumRawBeams());
			double[] bearingTable = beamData.getBeamCentresRadians();
			continueStream = true;
			ArrayList<ARISImageRecord> catalogRecords = new ArrayList<ARISImageRecord>();
			setImageRecords(catalogRecords);
			while (continueStream) {
				try {
					ARISFrameHeader frameHeader = new ARISFrameHeader(cis.getPos());
					ARISImageRecord imageRecord = new ARISImageRecord(fileHeader, frameHeader, bearingTable);
					readFullRecord(imageRecord, dis);
					if (streamObserver != null) {
						continueStream = streamObserver.newImageRecord(imageRecord);
					}
					ARISImageRecord clonedRecord = imageRecord.clone();
					clonedRecord.freeImageData();
					catalogRecords.add(clonedRecord);
					if (firstTime == 0) {
						firstTime = imageRecord.getRecordTime();
					}
					lastTime = imageRecord.getRecordTime();
					nFrames ++;
				}
				catch (EOFException e) {
					break;
				}
			}
		}
		catch (Exception e) {
			throw new CatalogException(e);
		}
		CatalogStreamSummary cs;
		if (continueStream == false) {
			// if we told it to stop. If it was EOF, continueStream will still be true
			cs = new CatalogStreamSummary(nFrames, firstTime, lastTime, CatalogStreamSummary.DATAGAP);
		}
		else {
			analyseCatalog();
			writeSerializedCatalog(getFilePath(), this);
			cs = new CatalogStreamSummary(nFrames, firstTime, lastTime, CatalogStreamSummary.FILEEND);
		}

		return cs;
	}

	@Override
	public void stopCatalogStream() {
		continueStream = false;
	}

}
