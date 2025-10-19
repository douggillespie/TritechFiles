package aris;

import java.io.DataInput;
import java.io.IOException;
import java.io.Serializable;

public class ARISFrameHeader extends ARISHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DATASIZE = 1024;

	// Frame number in file
	private int FrameIndex;

	// PC time stamp when recorded; microseconds since epoch (Jan 1st 1970)
	private long FrameTime;

	// ARIS file format version = 0x05464444
	private int Version;

	private int Status;

	// On-sonar microseconds since epoch (Jan 1st 1970)
	private long sonarTimeStamp;

	private int TS_Day;

	private int TS_Hour;

	private int TS_Minute;

	private int TS_Second;

	private int TS_Hsecond;

	private int TransmitMode;

	// Window start in meters
	float WindowStart;

	// Window length in meters
	float WindowLength;

	private int Threshold;

	private int Intensity;

	// Note: 0-24 dB
	private int ReceiverGain;

	// CPU temperature
	// Note: Celsius
	private int DegC1;

	// Power supply temperature
	// Note: Celsius
	private int DegC2;

	// % relative humidity
	private int Humidity;

	// Focus units 0-1000
	private int Focus;

	// OBSOLETE: Unused.
	private int Battery;

	public static final int NUSERVALS = 8;
	float[] UserValues = new float[NUSERVALS];

	// Platform velocity from AUV integration
	float Velocity;

	// Platform depth from AUV integration
	float Depth;

	// Platform altitude from AUV integration
	float Altitude;

	// Platform pitch from AUV integration
	float Pitch;

	// Platform pitch rate from AUV integration
	float PitchRate;

	// Platform roll from AUV integration
	float Roll;

	// Platform roll rate from AUV integration
	float RollRate;

	// Platform heading from AUV integration
	float Heading;

	// Platform heading rate from AUV integration
	float HeadingRate;

	// Sonar compass heading output
	float CompassHeading;

	// Sonar compass pitch output
	float CompassPitch;

	// Sonar compass roll output
	float CompassRoll;

	// from auxiliary GPS sensor
	double Latitude;

	// from auxiliary GPS sensor
	double Longitude;

	// Note: special for PNNL
	float SonarPosition;

	private int ConfigFlags;

	float BeamTilt;

	float TargetRange;

	float TargetBearing;

	private int TargetPresent;

	// OBSOLETE: Unused.
	private int FirmwareRevision;

	private int Flags;

	// Source file frame number for CSOT output files
	private int SourceFrame;

	// Water temperature from housing temperature sensor
	float WaterTemp;

	private int TimerPeriod;

	// Sonar X location for 3D processing
	// Note: Bluefin, external sensor data
	float SonarX;

	// Sonar Y location for 3D processing
	float SonarY;

	// Sonar Z location for 3D processing
	float SonarZ;

	// X2 pan output
	float SonarPan;

	// X2 tilt output
	float SonarTilt;

	// X2 roll output
	float SonarRoll;

	float PanPNNL;

	float TiltPNNL;

	float RollPNNL;

	// Note: special for Bluefin HAUV or other AUV integration
	double VehicleTime;

	// GPS output from NMEA GGK message
	float TimeGGK;

	// GPS output from NMEA GGK message
	private int DateGGK;

	// GPS output from NMEA GGK message
	private int QualityGGK;

	// GPS output from NMEA GGK message
	private int NumSatsGGK;

	// GPS output from NMEA GGK message
	float DOPGGK;

	// GPS output from NMEA GGK message
	float EHTGGK;

	// external sensor
	float HeaveTSS;

	private static final int NGPSDATE = 7;
	private int[] GPSTimeStamp = new int[NGPSDATE];

	// GPS minute output

	// Sonar mount location pan offset for 3D processing; meters
	float SonarPanOffset;

	// Sonar mount location tilt offset for 3D processing
	float SonarTiltOffset;

	// Sonar mount location roll offset for 3D processing
	float SonarRollOffset;

	// Sonar mount location X offset for 3D processing
	float SonarXOffset;

	// Sonar mount location Y offset for 3D processing
	float SonarYOffset;

	// Sonar mount location Z offset for 3D processing
	float SonarZOffset;

	// 3D processing transformation matrix
	private static final int MATRIXLENGTH = 16;
	float[] tmatrix = new float[MATRIXLENGTH];

	// Calculated as 1e6/SamplePeriod
	float SampleRate;

	// X-axis sonar acceleration
	float AccellX;

	// Y-axis sonar acceleration
	float AccellY;

	// Z-axis sonar acceleration
	float AccellZ;

	// ARIS ping mode
	// Note: 1..12
	private int PingMode;

	// Frequency
	// Note: 1 = HF, 0 = LF
	private int FrequencyHiLow;

	// Width of transmit pulse
	// Note: 4..100 microseconds
	private int PulseWidth;

	// Ping cycle time
	// Note: 1802..65535 microseconds
	private int CyclePeriod;

	// Downrange sample rate
	// Note: 4..100 microseconds
	private int SamplePeriod;

	// 1 = Transmit ON, 0 = Transmit OFF
	private int TransmitEnable;

	// Instantaneous frame rate between frame N and frame N-1
	// Note: microseconds
	float FrameRate;

	// Sound velocity in water calculated from water temperature depth and salinity setting
	// Note: m/s
	float SoundSpeed;

	// Number of downrange samples in each beam
	private int SamplesPerBeam;

	// 1 = 150V ON (Max Power), 0 = 150V OFF (Min Power, 12V)
	private int Enable150V;

	// Delay from transmit until start of sampling (window start) in usec, [930..65535]
	private int SampleStartDelay;

	// 1 = telephoto lens (large lens, big lens, hi-res lens) present
	private int LargeLens;

	// 1 = ARIS 3000, 0 = ARIS 1800, 2 = ARIS 1200
	private int TheSystemType;

	// Sonar serial number as labeled on housing
	private int SonarSerialNumber;

	// Reserved.
	// OBSOLETE: Obsolete
	private long ReservedEK;

	// Error flag code bits
	private int ArisErrorFlagsUint;

	// Missed packet count for Ethernet statistics reporting
	private int MissedPackets;

	// Version number of ArisApp sending frame data
	private int ArisAppVersion;

	// Reserved for future use
	private int Available2;

	// 1 = frame data already ordered into [beam,sample] array, 0 = needs reordering
	private int ReorderedSamples;

	// Water salinity code:  0 = fresh, 15 = brackish, 35 = salt
	private int Salinity;

	// Depth sensor output
	// Note: psi
	float Pressure;

	// Battery input voltage before power steering
	// Note: mV
	float BatteryVoltage;

	// Main cable input voltage before power steering
	// Note: mV
	float MainVoltage;

	// Input voltage after power steering; filtered voltage
	// Note: mV
	float SwitchVoltage;

	// Note: Added 14-Aug-2012 for AutomaticRecording
	private int FocusMotorMoving;

	// Note: Added 16-Aug (first two bits = 12V, second two bits = 150V, 00 = not changing, 01 = turning on, 10 = turning off)
	private int VoltageChanging;

	private int FocusTimeoutFault;

	private int FocusOverCurrentFault;

	private int FocusNotFoundFault;

	private int FocusStalledFault;

	private int FPGATimeoutFault;

	private int FPGABusyFault;

	private int FPGAStuckFault;

	private int CPUTempFault;

	private int PSUTempFault;

	private int WaterTempFault;

	private int HumidityFault;

	private int PressureFault;

	private int VoltageReadFault;

	private int VoltageWriteFault;

	// Focus shaft current position
	// Note: 0..1000 motor units
	private int FocusCurrentPosition;

	// Commanded pan position
	float TargetPan;

	// Commanded tilt position
	float TargetTilt;

	// Commanded roll position
	float TargetRoll;

	private int PanMotorErrorCode;

	private int TiltMotorErrorCode;

	private int RollMotorErrorCode;

	// Low-resolution magnetic encoder absolute pan position (NaN indicates no arm detected for axis since 2.6.0.8403)
	float PanAbsPosition;

	// Low-resolution magnetic encoder absolute tilt position (NaN indicates no arm detected for axis since 2.6.0.8403)
	float TiltAbsPosition;

	// Low-resolution magnetic encoder absolute roll position (NaN indicates no arm detected for axis since 2.6.0.8403)
	float RollAbsPosition;

	// Accelerometer outputs from AR2 CPU board sensor
	// Note: G
	float PanAccelX;

	// Note: G
	float PanAccelY;

	// Note: G
	float PanAccelZ;

	// Note: G
	float TiltAccelX;

	// Note: G
	float TiltAccelY;

	// Note: G
	float TiltAccelZ;

	// Note: G
	float RollAccelX;

	// Note: G
	float RollAccelY;

	// Note: G
	float RollAccelZ;

	// Cookie indices for command acknowlege in frame header
	private int AppliedSettings;

	// Cookie indices for command acknowlege in frame header
	private int ConstrainedSettings;

	// Cookie indices for command acknowlege in frame header
	private int InvalidSettings;

	// If true delay is added between sending out image data packets
	private int EnableInterpacketDelay;

	// packet delay factor in us (does not include function overhead time)
	private int InterpacketDelayPeriod;

	// Total time the sonar has been running over its lifetime.
	// Note: seconds
	private int Uptime;

	// Major version number
	private short ArisAppVersionMajor;

	// Minor version number
	private short ArisAppVersionMinor;

	// Sonar time when frame cycle is initiated in hardware
	private long GoTime;

	// AR2 pan velocity
	// Note: degrees/second
	float PanVelocity;

	// AR2 tilt velocity
	// Note: degrees/second
	float TiltVelocity;

	// AR2 roll velocity
	// Note: degrees/second
	float RollVelocity;

	// Age of the last GPS fix acquired; capped at 0xFFFFFFFF; zero if none
	// Note: microseconds
	private int GpsTimeAge;

	// bit 0 = Defender; bit 1 = Voyager
	private int SystemVariant;

	// <2 = original; 2 = 2024
	private int CompassRevision;

	// internal use
	float CompassReserved1;

	/**
	 * Position of start of header, not of data
	 */
	private long filePosition;

	// Padding to fill out to 1024 bytes
	private static final int PADDING = 280;


	public ARISFrameHeader(long filePosition) {
		this.filePosition = filePosition;
	}

	/**
	 * @return the filePosition
	 */
	public long getFilePosition() {
		return filePosition;
	}


	@Override
	public void readHeader(DataInput dis) throws IOException {
		FrameIndex = dis.readInt();
		FrameTime = dis.readLong();
		Version = dis.readInt();
		Status = dis.readInt();
		sonarTimeStamp = dis.readLong();
		TS_Day = dis.readInt();
		TS_Hour = dis.readInt();
		TS_Minute = dis.readInt();
		TS_Second = dis.readInt();
		TS_Hsecond = dis.readInt();
		TransmitMode = dis.readInt();
		WindowStart = dis.readFloat();
		WindowLength = dis.readFloat();
		Threshold = dis.readInt();
		Intensity = dis.readInt();
		ReceiverGain = dis.readInt();
		DegC1 = dis.readInt();
		DegC2 = dis.readInt();
		Humidity = dis.readInt();
		Focus = dis.readInt();
		Battery = dis.readInt();
		if (UserValues == null) {
			UserValues = new float[NUSERVALS];
		}
		for (int i = 0; i < NUSERVALS; i++) {
			UserValues[i] = dis.readFloat();
		}
		Velocity = dis.readFloat();
		Depth = dis.readFloat();
		Altitude = dis.readFloat();
		Pitch = dis.readFloat();
		PitchRate = dis.readFloat();
		Roll = dis.readFloat();
		RollRate = dis.readFloat();
		Heading = dis.readFloat();
		HeadingRate = dis.readFloat();
		CompassHeading = dis.readFloat();
		CompassPitch = dis.readFloat();
		CompassRoll = dis.readFloat();
		Latitude = dis.readDouble();
		Longitude = dis.readDouble();
		SonarPosition = dis.readFloat();
		ConfigFlags = dis.readInt();
		BeamTilt = dis.readFloat();
		TargetRange = dis.readFloat();
		TargetBearing = dis.readFloat();
		TargetPresent = dis.readInt();
		FirmwareRevision = dis.readInt();
		Flags = dis.readInt();
		SourceFrame = dis.readInt();
		WaterTemp = dis.readFloat();
		TimerPeriod = dis.readInt();
		SonarX = dis.readFloat();
		SonarY = dis.readFloat();
		SonarZ = dis.readFloat();
		SonarPan = dis.readFloat();
		SonarTilt = dis.readFloat();
		SonarRoll = dis.readFloat();
		PanPNNL = dis.readFloat();
		TiltPNNL = dis.readFloat();
		RollPNNL = dis.readFloat();
		VehicleTime = dis.readDouble();
		TimeGGK = dis.readFloat();
		DateGGK = dis.readInt();
		QualityGGK = dis.readInt();
		NumSatsGGK = dis.readInt();
		DOPGGK = dis.readFloat();
		EHTGGK = dis.readFloat();
		HeaveTSS = dis.readFloat();
		if (GPSTimeStamp == null) {
			GPSTimeStamp = new int[NGPSDATE];
		}
		for (int i = 0; i < NGPSDATE; i++) {
			GPSTimeStamp[i] = dis.readInt(); 
		}
		SonarPanOffset = dis.readFloat();
		SonarTiltOffset = dis.readFloat();
		SonarRollOffset = dis.readFloat();
		SonarXOffset = dis.readFloat();
		SonarYOffset = dis.readFloat();
		SonarZOffset = dis.readFloat();
		if (tmatrix == null) {
			tmatrix = new float[MATRIXLENGTH];
		}
		for (int i = 0; i < MATRIXLENGTH; i++) {
			tmatrix[i] = dis.readFloat();
		}
		SampleRate = dis.readFloat();
		AccellX = dis.readFloat();
		AccellY = dis.readFloat();
		AccellZ = dis.readFloat();
		PingMode = dis.readInt();
		FrequencyHiLow = dis.readInt();
		PulseWidth = dis.readInt();
		CyclePeriod = dis.readInt();
		SamplePeriod = dis.readInt();
		TransmitEnable = dis.readInt();
		FrameRate = dis.readFloat();
		SoundSpeed = dis.readFloat();
		SamplesPerBeam = dis.readInt();
		Enable150V = dis.readInt();
		SampleStartDelay = dis.readInt();
		LargeLens = dis.readInt();
		TheSystemType = dis.readInt();
		SonarSerialNumber = dis.readInt();
		ReservedEK = dis.readLong();
		ArisErrorFlagsUint = dis.readInt();
		MissedPackets = dis.readInt();
		ArisAppVersion = dis.readInt();
		Available2 = dis.readInt();
		ReorderedSamples = dis.readInt();
		Salinity = dis.readInt();
		Pressure = dis.readFloat();
		BatteryVoltage = dis.readFloat();
		MainVoltage = dis.readFloat();
		SwitchVoltage = dis.readFloat();
		FocusMotorMoving = dis.readInt();
		VoltageChanging = dis.readInt();
		FocusTimeoutFault = dis.readInt();
		FocusOverCurrentFault = dis.readInt();
		FocusNotFoundFault = dis.readInt();
		FocusStalledFault = dis.readInt();
		FPGATimeoutFault = dis.readInt();
		FPGABusyFault = dis.readInt();
		FPGAStuckFault = dis.readInt();
		CPUTempFault = dis.readInt();
		PSUTempFault = dis.readInt();
		WaterTempFault = dis.readInt();
		HumidityFault = dis.readInt();
		PressureFault = dis.readInt();
		VoltageReadFault = dis.readInt();
		VoltageWriteFault = dis.readInt();
		FocusCurrentPosition = dis.readInt();
		TargetPan = dis.readFloat();
		TargetTilt = dis.readFloat();
		TargetRoll = dis.readFloat();
		PanMotorErrorCode = dis.readInt();
		TiltMotorErrorCode = dis.readInt();
		RollMotorErrorCode = dis.readInt();
		PanAbsPosition = dis.readFloat();
		TiltAbsPosition = dis.readFloat();
		RollAbsPosition = dis.readFloat();
		PanAccelX = dis.readFloat();
		PanAccelY = dis.readFloat();
		PanAccelZ = dis.readFloat();
		TiltAccelX = dis.readFloat();
		TiltAccelY = dis.readFloat();
		TiltAccelZ = dis.readFloat();
		RollAccelX = dis.readFloat();
		RollAccelY = dis.readFloat();
		RollAccelZ = dis.readFloat();
		AppliedSettings = dis.readInt();
		ConstrainedSettings = dis.readInt();
		InvalidSettings = dis.readInt();
		EnableInterpacketDelay = dis.readInt();
		InterpacketDelayPeriod = dis.readInt();
		Uptime = dis.readInt();
		ArisAppVersionMajor = dis.readShort();
		ArisAppVersionMinor = dis.readShort();
		GoTime = dis.readLong();
		PanVelocity = dis.readFloat();
		TiltVelocity = dis.readFloat();
		RollVelocity = dis.readFloat();
		GpsTimeAge = dis.readInt();
		SystemVariant = dis.readInt();
		CompassRevision = dis.readInt();
		CompassReserved1 = dis.readFloat();
		dis.skipBytes(PADDING);
	}


	/**
	 * @return the frameIndex
	 */
	public int getFrameIndex() {
		return FrameIndex;
	}


	/**
	 * @return the frameTime
	 */
	public long getFrameTime() {
		return FrameTime;
	}


	/**
	 * @return the version
	 */
	public int getVersion() {
		return Version;
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return Status;
	}


	/**
	 * @return the sonarTimeStamp
	 */
	public long getSonarTimeStamp() {
		return sonarTimeStamp;
	}


	/**
	 * @return the tS_Day
	 */
	public int getTS_Day() {
		return TS_Day;
	}


	/**
	 * @return the tS_Hour
	 */
	public int getTS_Hour() {
		return TS_Hour;
	}


	/**
	 * @return the tS_Minute
	 */
	public int getTS_Minute() {
		return TS_Minute;
	}


	/**
	 * @return the tS_Second
	 */
	public int getTS_Second() {
		return TS_Second;
	}


	/**
	 * @return the tS_Hsecond
	 */
	public int getTS_Hsecond() {
		return TS_Hsecond;
	}


	/**
	 * @return the transmitMode
	 */
	public int getTransmitMode() {
		return TransmitMode;
	}


	/**
	 * @return the windowStart
	 */
	public float getWindowStart() {
		return WindowStart;
	}


	/**
	 * @return the windowLength
	 */
	public float getWindowLength() {
		return WindowLength;
	}


	/**
	 * @return the threshold
	 */
	public int getThreshold() {
		return Threshold;
	}


	/**
	 * @return the intensity
	 */
	public int getIntensity() {
		return Intensity;
	}


	/**
	 * @return the receiverGain
	 */
	public int getReceiverGain() {
		return ReceiverGain;
	}


	/**
	 * @return the degC1
	 */
	public int getDegC1() {
		return DegC1;
	}


	/**
	 * @return the degC2
	 */
	public int getDegC2() {
		return DegC2;
	}


	/**
	 * @return the humidity
	 */
	public int getHumidity() {
		return Humidity;
	}


	/**
	 * @return the focus
	 */
	public int getFocus() {
		return Focus;
	}


	/**
	 * @return the battery
	 */
	public int getBattery() {
		return Battery;
	}


	/**
	 * @return the nuservals
	 */
	public static int getNuservals() {
		return NUSERVALS;
	}


	/**
	 * @return the userValues
	 */
	public float[] getUserValues() {
		return UserValues;
	}


	/**
	 * @return the velocity
	 */
	public float getVelocity() {
		return Velocity;
	}


	/**
	 * @return the depth
	 */
	public float getDepth() {
		return Depth;
	}


	/**
	 * @return the altitude
	 */
	public float getAltitude() {
		return Altitude;
	}


	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return Pitch;
	}


	/**
	 * @return the pitchRate
	 */
	public float getPitchRate() {
		return PitchRate;
	}


	/**
	 * @return the roll
	 */
	public float getRoll() {
		return Roll;
	}


	/**
	 * @return the rollRate
	 */
	public float getRollRate() {
		return RollRate;
	}


	/**
	 * @return the heading
	 */
	public float getHeading() {
		return Heading;
	}


	/**
	 * @return the headingRate
	 */
	public float getHeadingRate() {
		return HeadingRate;
	}


	/**
	 * @return the compassHeading
	 */
	public float getCompassHeading() {
		return CompassHeading;
	}


	/**
	 * @return the compassPitch
	 */
	public float getCompassPitch() {
		return CompassPitch;
	}


	/**
	 * @return the compassRoll
	 */
	public float getCompassRoll() {
		return CompassRoll;
	}


	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return Latitude;
	}


	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return Longitude;
	}


	/**
	 * @return the sonarPosition
	 */
	public float getSonarPosition() {
		return SonarPosition;
	}


	/**
	 * @return the configFlags
	 */
	public int getConfigFlags() {
		return ConfigFlags;
	}


	/**
	 * @return the beamTilt
	 */
	public float getBeamTilt() {
		return BeamTilt;
	}


	/**
	 * @return the targetRange
	 */
	public float getTargetRange() {
		return TargetRange;
	}


	/**
	 * @return the targetBearing
	 */
	public float getTargetBearing() {
		return TargetBearing;
	}


	/**
	 * @return the targetPresent
	 */
	public int getTargetPresent() {
		return TargetPresent;
	}


	/**
	 * @return the firmwareRevision
	 */
	public int getFirmwareRevision() {
		return FirmwareRevision;
	}


	/**
	 * @return the flags
	 */
	public int getFlags() {
		return Flags;
	}


	/**
	 * @return the sourceFrame
	 */
	public int getSourceFrame() {
		return SourceFrame;
	}


	/**
	 * @return the waterTemp
	 */
	public float getWaterTemp() {
		return WaterTemp;
	}


	/**
	 * @return the timerPeriod
	 */
	public int getTimerPeriod() {
		return TimerPeriod;
	}


	/**
	 * @return the sonarX
	 */
	public float getSonarX() {
		return SonarX;
	}


	/**
	 * @return the sonarY
	 */
	public float getSonarY() {
		return SonarY;
	}


	/**
	 * @return the sonarZ
	 */
	public float getSonarZ() {
		return SonarZ;
	}


	/**
	 * @return the sonarPan
	 */
	public float getSonarPan() {
		return SonarPan;
	}


	/**
	 * @return the sonarTilt
	 */
	public float getSonarTilt() {
		return SonarTilt;
	}


	/**
	 * @return the sonarRoll
	 */
	public float getSonarRoll() {
		return SonarRoll;
	}


	/**
	 * @return the panPNNL
	 */
	public float getPanPNNL() {
		return PanPNNL;
	}


	/**
	 * @return the tiltPNNL
	 */
	public float getTiltPNNL() {
		return TiltPNNL;
	}


	/**
	 * @return the rollPNNL
	 */
	public float getRollPNNL() {
		return RollPNNL;
	}


	/**
	 * @return the vehicleTime
	 */
	public double getVehicleTime() {
		return VehicleTime;
	}


	/**
	 * @return the timeGGK
	 */
	public float getTimeGGK() {
		return TimeGGK;
	}


	/**
	 * @return the dateGGK
	 */
	public int getDateGGK() {
		return DateGGK;
	}


	/**
	 * @return the qualityGGK
	 */
	public int getQualityGGK() {
		return QualityGGK;
	}


	/**
	 * @return the numSatsGGK
	 */
	public int getNumSatsGGK() {
		return NumSatsGGK;
	}


	/**
	 * @return the dOPGGK
	 */
	public float getDOPGGK() {
		return DOPGGK;
	}


	/**
	 * @return the eHTGGK
	 */
	public float getEHTGGK() {
		return EHTGGK;
	}


	/**
	 * @return the heaveTSS
	 */
	public float getHeaveTSS() {
		return HeaveTSS;
	}


	/**
	 * @return the ngpsdate
	 */
	public static int getNgpsdate() {
		return NGPSDATE;
	}


	/**
	 * @return the gPSTimeStamp
	 */
	public int[] getGPSTimeStamp() {
		return GPSTimeStamp;
	}


	/**
	 * @return the sonarPanOffset
	 */
	public float getSonarPanOffset() {
		return SonarPanOffset;
	}


	/**
	 * @return the sonarTiltOffset
	 */
	public float getSonarTiltOffset() {
		return SonarTiltOffset;
	}


	/**
	 * @return the sonarRollOffset
	 */
	public float getSonarRollOffset() {
		return SonarRollOffset;
	}


	/**
	 * @return the sonarXOffset
	 */
	public float getSonarXOffset() {
		return SonarXOffset;
	}


	/**
	 * @return the sonarYOffset
	 */
	public float getSonarYOffset() {
		return SonarYOffset;
	}


	/**
	 * @return the sonarZOffset
	 */
	public float getSonarZOffset() {
		return SonarZOffset;
	}


	/**
	 * @return the matrixlength
	 */
	public static int getMatrixlength() {
		return MATRIXLENGTH;
	}


	/**
	 * @return the tmatrix
	 */
	public float[] getTmatrix() {
		return tmatrix;
	}


	/**
	 * @return the sampleRate
	 */
	public float getSampleRate() {
		return SampleRate;
	}


	/**
	 * @return the accellX
	 */
	public float getAccellX() {
		return AccellX;
	}


	/**
	 * @return the accellY
	 */
	public float getAccellY() {
		return AccellY;
	}


	/**
	 * @return the accellZ
	 */
	public float getAccellZ() {
		return AccellZ;
	}


	/**
	 * @return the pingMode
	 */
	public int getPingMode() {
		return PingMode;
	}


	/**
	 * @return the frequencyHiLow
	 */
	public int getFrequencyHiLow() {
		return FrequencyHiLow;
	}


	/**
	 * @return the pulseWidth
	 */
	public int getPulseWidth() {
		return PulseWidth;
	}


	/**
	 * @return the cyclePeriod
	 */
	public int getCyclePeriod() {
		return CyclePeriod;
	}


	/**
	 * @return the samplePeriod
	 */
	public int getSamplePeriod() {
		return SamplePeriod;
	}


	/**
	 * @return the transmitEnable
	 */
	public int getTransmitEnable() {
		return TransmitEnable;
	}


	/**
	 * @return the frameRate
	 */
	public float getFrameRate() {
		return FrameRate;
	}


	/**
	 * @return the soundSpeed
	 */
	public float getSoundSpeed() {
		return SoundSpeed;
	}


	/**
	 * @return the samplesPerBeam
	 */
	public int getSamplesPerBeam() {
		return SamplesPerBeam;
	}


	/**
	 * @return the enable150V
	 */
	public int getEnable150V() {
		return Enable150V;
	}


	/**
	 * @return the sampleStartDelay
	 */
	public int getSampleStartDelay() {
		return SampleStartDelay;
	}


	/**
	 * @return the largeLens
	 */
	public int getLargeLens() {
		return LargeLens;
	}


	/**
	 * @return the theSystemType
	 */
	public int getTheSystemType() {
		return TheSystemType;
	}


	/**
	 * @return the sonarSerialNumber
	 */
	public int getSonarSerialNumber() {
		return SonarSerialNumber;
	}


	/**
	 * @return the reservedEK
	 */
	public long getReservedEK() {
		return ReservedEK;
	}


	/**
	 * @return the arisErrorFlagsUint
	 */
	public int getArisErrorFlagsUint() {
		return ArisErrorFlagsUint;
	}


	/**
	 * @return the missedPackets
	 */
	public int getMissedPackets() {
		return MissedPackets;
	}


	/**
	 * @return the arisAppVersion
	 */
	public int getArisAppVersion() {
		return ArisAppVersion;
	}


	/**
	 * @return the available2
	 */
	public int getAvailable2() {
		return Available2;
	}


	/**
	 * @return the reorderedSamples
	 */
	public int getReorderedSamples() {
		return ReorderedSamples;
	}


	/**
	 * @return the salinity
	 */
	public int getSalinity() {
		return Salinity;
	}


	/**
	 * @return the pressure
	 */
	public float getPressure() {
		return Pressure;
	}


	/**
	 * @return the batteryVoltage
	 */
	public float getBatteryVoltage() {
		return BatteryVoltage;
	}


	/**
	 * @return the mainVoltage
	 */
	public float getMainVoltage() {
		return MainVoltage;
	}


	/**
	 * @return the switchVoltage
	 */
	public float getSwitchVoltage() {
		return SwitchVoltage;
	}


	/**
	 * @return the focusMotorMoving
	 */
	public int getFocusMotorMoving() {
		return FocusMotorMoving;
	}


	/**
	 * @return the voltageChanging
	 */
	public int getVoltageChanging() {
		return VoltageChanging;
	}


	/**
	 * @return the focusTimeoutFault
	 */
	public int getFocusTimeoutFault() {
		return FocusTimeoutFault;
	}


	/**
	 * @return the focusOverCurrentFault
	 */
	public int getFocusOverCurrentFault() {
		return FocusOverCurrentFault;
	}


	/**
	 * @return the focusNotFoundFault
	 */
	public int getFocusNotFoundFault() {
		return FocusNotFoundFault;
	}


	/**
	 * @return the focusStalledFault
	 */
	public int getFocusStalledFault() {
		return FocusStalledFault;
	}


	/**
	 * @return the fPGATimeoutFault
	 */
	public int getFPGATimeoutFault() {
		return FPGATimeoutFault;
	}


	/**
	 * @return the fPGABusyFault
	 */
	public int getFPGABusyFault() {
		return FPGABusyFault;
	}


	/**
	 * @return the fPGAStuckFault
	 */
	public int getFPGAStuckFault() {
		return FPGAStuckFault;
	}


	/**
	 * @return the cPUTempFault
	 */
	public int getCPUTempFault() {
		return CPUTempFault;
	}


	/**
	 * @return the pSUTempFault
	 */
	public int getPSUTempFault() {
		return PSUTempFault;
	}


	/**
	 * @return the waterTempFault
	 */
	public int getWaterTempFault() {
		return WaterTempFault;
	}


	/**
	 * @return the humidityFault
	 */
	public int getHumidityFault() {
		return HumidityFault;
	}


	/**
	 * @return the pressureFault
	 */
	public int getPressureFault() {
		return PressureFault;
	}


	/**
	 * @return the voltageReadFault
	 */
	public int getVoltageReadFault() {
		return VoltageReadFault;
	}


	/**
	 * @return the voltageWriteFault
	 */
	public int getVoltageWriteFault() {
		return VoltageWriteFault;
	}


	/**
	 * @return the focusCurrentPosition
	 */
	public int getFocusCurrentPosition() {
		return FocusCurrentPosition;
	}


	/**
	 * @return the targetPan
	 */
	public float getTargetPan() {
		return TargetPan;
	}


	/**
	 * @return the targetTilt
	 */
	public float getTargetTilt() {
		return TargetTilt;
	}


	/**
	 * @return the targetRoll
	 */
	public float getTargetRoll() {
		return TargetRoll;
	}


	/**
	 * @return the panMotorErrorCode
	 */
	public int getPanMotorErrorCode() {
		return PanMotorErrorCode;
	}


	/**
	 * @return the tiltMotorErrorCode
	 */
	public int getTiltMotorErrorCode() {
		return TiltMotorErrorCode;
	}


	/**
	 * @return the rollMotorErrorCode
	 */
	public int getRollMotorErrorCode() {
		return RollMotorErrorCode;
	}


	/**
	 * @return the panAbsPosition
	 */
	public float getPanAbsPosition() {
		return PanAbsPosition;
	}


	/**
	 * @return the tiltAbsPosition
	 */
	public float getTiltAbsPosition() {
		return TiltAbsPosition;
	}


	/**
	 * @return the rollAbsPosition
	 */
	public float getRollAbsPosition() {
		return RollAbsPosition;
	}


	/**
	 * @return the panAccelX
	 */
	public float getPanAccelX() {
		return PanAccelX;
	}


	/**
	 * @return the panAccelY
	 */
	public float getPanAccelY() {
		return PanAccelY;
	}


	/**
	 * @return the panAccelZ
	 */
	public float getPanAccelZ() {
		return PanAccelZ;
	}


	/**
	 * @return the tiltAccelX
	 */
	public float getTiltAccelX() {
		return TiltAccelX;
	}


	/**
	 * @return the tiltAccelY
	 */
	public float getTiltAccelY() {
		return TiltAccelY;
	}


	/**
	 * @return the tiltAccelZ
	 */
	public float getTiltAccelZ() {
		return TiltAccelZ;
	}


	/**
	 * @return the rollAccelX
	 */
	public float getRollAccelX() {
		return RollAccelX;
	}


	/**
	 * @return the rollAccelY
	 */
	public float getRollAccelY() {
		return RollAccelY;
	}


	/**
	 * @return the rollAccelZ
	 */
	public float getRollAccelZ() {
		return RollAccelZ;
	}


	/**
	 * @return the appliedSettings
	 */
	public int getAppliedSettings() {
		return AppliedSettings;
	}


	/**
	 * @return the constrainedSettings
	 */
	public int getConstrainedSettings() {
		return ConstrainedSettings;
	}


	/**
	 * @return the invalidSettings
	 */
	public int getInvalidSettings() {
		return InvalidSettings;
	}


	/**
	 * @return the enableInterpacketDelay
	 */
	public int getEnableInterpacketDelay() {
		return EnableInterpacketDelay;
	}


	/**
	 * @return the interpacketDelayPeriod
	 */
	public int getInterpacketDelayPeriod() {
		return InterpacketDelayPeriod;
	}


	/**
	 * @return the uptime
	 */
	public int getUptime() {
		return Uptime;
	}


	/**
	 * @return the arisAppVersionMajor
	 */
	public short getArisAppVersionMajor() {
		return ArisAppVersionMajor;
	}


	/**
	 * @return the arisAppVersionMinor
	 */
	public short getArisAppVersionMinor() {
		return ArisAppVersionMinor;
	}


	/**
	 * @return the goTime
	 */
	public long getGoTime() {
		return GoTime;
	}


	/**
	 * @return the panVelocity
	 */
	public float getPanVelocity() {
		return PanVelocity;
	}


	/**
	 * @return the tiltVelocity
	 */
	public float getTiltVelocity() {
		return TiltVelocity;
	}


	/**
	 * @return the rollVelocity
	 */
	public float getRollVelocity() {
		return RollVelocity;
	}


	/**
	 * @return the gpsTimeAge
	 */
	public int getGpsTimeAge() {
		return GpsTimeAge;
	}


	/**
	 * @return the systemVariant
	 */
	public int getSystemVariant() {
		return SystemVariant;
	}


	/**
	 * @return the compassRevision
	 */
	public int getCompassRevision() {
		return CompassRevision;
	}


	/**
	 * @return the compassReserved1
	 */
	public float getCompassReserved1() {
		return CompassReserved1;
	}


	/**
	 * @return the padding
	 */
	public static int getPadding() {
		return PADDING;
	}

}
