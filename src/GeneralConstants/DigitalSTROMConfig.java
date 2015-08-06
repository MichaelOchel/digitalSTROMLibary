package GeneralConstants;

public class DigitalSTROMConfig {

	/**** Client configuration ****/
    
	//connection Configuration
	public final static int DEFAULT_CONNECTION_TIMEOUT = 4000;
	public final static int DEFAULT_READ_TIMEOUT = 10000;
	public final static String APPLICATION_NAME = "ESH";
	public static String TRUST_CERT_PATH = null;
	
	/**** ****/
	
	//Name
	public static final String EVENT_NAME = "ESH";
	public static int DEFAULT_TRASH_DEVICE_DELEATE_TIME = 7;//days after the trash devices get deleted
	
	/**** Sensordata ****/
	
	//Sensodata read config
	public final static int DEFAULT_SENSORDATA_REFRESH_INTERVAL = 10000;
	public static int SENSORDATA_REFRESH_INTERVAL = DEFAULT_SENSORDATA_REFRESH_INTERVAL;
	public final static int DEFAULT_SENSOR_READING_WAIT_TIME = 60000;
	public static int SENSOR_READING_WAIT_TIME = DEFAULT_SENSOR_READING_WAIT_TIME;
	
	//sensor data Prioritys
	public static final String REFRESH_PRIORITY_NEVER = "never";
	public static final String REFRESH_PRIORITY_LOW = "low";
	public static final String REFRESH_PRIORITY_MEDIUM = "medium";
	public static final String REFRESH_PRIORITY_HIGH = "high";

		//max sensor reading cyclic to wait
		public final static long MEDIUM_PRIORITY_FACTOR = 5;
		public final static long LOW_PRIORITY_FACTOR = 10;
	
	
	
}
