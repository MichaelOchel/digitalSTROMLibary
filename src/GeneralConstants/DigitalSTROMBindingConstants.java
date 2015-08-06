package GeneralConstants;
/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */




/**
 * The {@link DigitalSTROMBinding} class defines common constants, which are 
 * used across the whole binding.
 * 
 * @author Alex Maier - Initial contribution
 */
public class DigitalSTROMBindingConstants {

    public static final String BINDING_ID = "digitalstrom";
    
    // List of all Thing Type Ids
    public static final String THING_TYPE_ID_DSS_BRIDGE = "dssBridge";
    public static final String THING_TYPE_ID_GE_KM200 = "GE-KM200";
    public static final String THING_TYPE_ID_GE_KL200 = "GE-KL200";
    
    public static final String THING_TYPE_ID_SCENE = "scene";
    
    // List of all Thing Type UIDs
/*    public final static ThingTypeUID THING_TYPE_DSS_BRIDGE = new ThingTypeUID(BINDING_ID, THING_TYPE_ID_DSS_BRIDGE);
    public final static ThingTypeUID THING_TYPE_GE_KM200 = new ThingTypeUID(BINDING_ID, THING_TYPE_ID_GE_KM200);
    public final static ThingTypeUID THING_TYPE_GE_KL200 = new ThingTypeUID(BINDING_ID, THING_TYPE_ID_GE_KL200);
    
    public final static ThingTypeUID THING_TYPE_SCENE = new ThingTypeUID(BINDING_ID, THING_TYPE_ID_SCENE);
    
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_DSS_BRIDGE);
  */  
    /**** List of all Channels ****/
    
    //Light
   	public static final String CHANNEL_BRIGHTNESS = "brightness";
   	public static final String CHANNEL_LIGHT_SWITCH = "lightSwitch";
   	//shade
   	public static final String CHANNEL_SHADE = "shade";
   	//scene
   	public static final String CHANNEL_SCENE = "scene";
   	//plug adapter
   	public static final String CHANNEL_PLUG_ADAPTER = "plugAdapter";
   	//sensor
   	public static final String CHANNEL_ELECTRIC_METER = "electricMeterValue";
   	public static final String CHANNEL_ENERGY_METER = "energyMeterValue";
   	public static final String CHANNEL_POWER_CONSUMPTION = "powerConsumption";
    
    /**** Bridge config properties ****/
   	
    public static final String HOST = "ipAddress";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String APPLICATION_TOKEN = "applicationToken";
	public static final String DS_ID = "dSID";
	public static final String DS_NAME = "dsName";
	public static final String SENSOR_DATA_UPDATE_INTERVALL = "sensorDataUpdateIntervall";
	public static final String DEFAULT_TRASH_DEVICE_DELEATE_TIME_KEY = "defaultTrashBinDeleateTime";
	public static final String TRUST_CERT_PATH_KEY = "trustCertPath";
	
	//public static int DEFAULT_TRASH_DEVICE_DELEATE_TIME = 7;//days after the trash devices get deleted
	//public static String TRUST_CERT_PATH = null;
	
    /**** Device config properties ****/
	
	public static final String DEVICE_UID = "dSUID";
	public static final String DEVICE_NAME = "deviceName";
	public static final String DEVICE_DSID = "dSID";
	public static final String DEVICE_HW_INFO = "hwInfo";
	public static final String DEVICE_ZONE_ID = "zoneID";
	public static final String DEVICE_GROUPS = "groups";
	public static final String DEVICE_OUTPUT_MODE = "outputmode";
	public static final String DEVICE_FUNCTIONAL_COLOR_GROUP = "funcColorGroup";
	public static final String DEVICE_METER_ID = "meterDSID";
	
	// Device properties scene
	public static final String DEVICE_SCENE = "scene"; //+ number of scene
	
	//Sensor data channel properties
    public static final String POWER_CONSUMTION_REFRESH_PRIORITY = "PowerConsumptionRefreshPriority";
    public static final String ELECTRIC_METER_REFRESH_PRIORITY = "ElectricMeterRefreshPriority";
    public static final String ENERGY_METER_REFRESH_PRIORITY = "EnergyMeterRefreshPriority";
    	//options
    	public static final String REFRESH_PRIORITY_NEVER = "never";
    	public static final String REFRESH_PRIORITY_LOW = "low";
    	public static final String REFRESH_PRIORITY_MEDIUM = "medium";
    	public static final String REFRESH_PRIORITY_HIGH = "high";

    	//public final static int DEFAULT_SENSOR_READING_WAIT_TIME = 60000;

    	//max sensor reading cyclic to wait
    	//public final static long MEDIUM_PRIORITY_FACTOR = 5;
    	//public final static long LOW_PRIORITY_FACTOR = 10;
    	
	/**** Scene config ****/
    public static final String SCENE_NAME = "sceneName";
    public static final String SCENE_ZONE_ID = "zoneID";
    public static final String SCENE_GROUP_ID = "groupID";
    public static final String SCENE_ID = "sceneID";
    	
	/**** Client configuration ****/
    
	//connection Configuration
//	public final static int DEFAULT_CONNECTION_TIMEOUT = 4000;
	//public final static int DEFAULT_READ_TIMEOUT = 10000;
	//public final static String APPLICATION_NAME = "ESH";

	
	//SensorData
	//public static int DEFAULT_SENSORDATA_REFRESH_INTERVAL = 10000; //namen ändern
	
	//Name
	//public static final String EVENT_NAME = "ESH";
}
