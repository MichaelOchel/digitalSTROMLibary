package digitalSTROMConfiguration;


public class DigitalSTROMConfig {

	/* Client configuration */
    
	//connection Configuration
	public static final  String APPLICATION_NAME = "ESH";
	public static final  int DEFAULT_CONNECTION_TIMEOUT = 4000;
	public static final  int DEFAULT_READ_TIMEOUT = 10000;
	public static String TRUST_CERT_PATH = null;
	
	public static final String EVENT_NAME = "ESH";
	
	/* Internal Configurations */
	
	//Trash Bin Config
	public static final int DEFAULT_TRASH_DEVICE_DELEATE_TIME = 7;//days after the trash devices get deleted
	public static int TRASH_DEVICE_DELEATE_TIME = DEFAULT_TRASH_DEVICE_DELEATE_TIME;//days after the trash devices get deleted

	public static final int DEFAULT_BIN_CHECK_TIME = 360000; //in milliseconds
	public static int BIN_CHECK_TIME = DEFAULT_BIN_CHECK_TIME; //in milliseconds
	
	//Device update config
	public static final int DEFAULT_POLLING_FREQUENCY = 1; // in seconds
	public static int POLLING_FREQUENCY = DEFAULT_POLLING_FREQUENCY; // in seconds
	
	
	/* Sensordata */
	
	//Sensodata read config
	public static final int DEFAULT_SENSORDATA_REFRESH_INTERVAL = 10000;
	public static int SENSORDATA_REFRESH_INTERVAL = DEFAULT_SENSORDATA_REFRESH_INTERVAL;
	
	public static final int DEFAULT_SENSOR_READING_WAIT_TIME = 60000;
	public static int SENSOR_READING_WAIT_TIME = DEFAULT_SENSOR_READING_WAIT_TIME;
	
	//sensor data Prioritys
	public static final String REFRESH_PRIORITY_NEVER = "never";
	public static final String REFRESH_PRIORITY_LOW = "low";
	public static final String REFRESH_PRIORITY_MEDIUM = "medium";
	public static final String REFRESH_PRIORITY_HIGH = "high";

		//max sensor reading cyclic to wait
		public static final  long MEDIUM_PRIORITY_FACTOR = 5;
		public static final  long LOW_PRIORITY_FACTOR = 10;
	
	
	
}
