package DigitalSTROMDevices.DeviceParameters;

/**
 * Represents a device state update for lights, shades and sensor data. 
 * 
 * @author Michael Ochel - Initial contribution
 * @author Matthias Siegele - Initial contribution
 *
 */
public interface DeviceStateUpdate {

	//Update types
	
	//light
	public final static String UPDATE_BRIGHTNESS = "brightness";
	public final static String UPDATE_ON_OFF = "OnOff";
	public final static String UPDATE_BRIGHTNESS_INCREASE = "brightnessIncrese";
	public final static String UPDATE_BRIGHTNESS_DECREASE = "brightnessDecrese";
	public final static String UPDATE_BRIGHTNESS_STOP = "brightnessStop";
	public final static String UPDATE_BRIGHTNESS_MOVE = "brightnessMove";
	
	//shades
	public final static String UPDATE_SLATPOSITION = "slatposition";
	public final static String UPDATE_SLAT_INCREASE = "slatIncrese";
	public final static String UPDATE_SLAT_DECREASE = "slatDecrese";
	public final static String UPDATE_OPEN_CLOSE = "OpenClose";
	public final static String UPDATE_SLAT_MOVE = "slatMove";
	public final static String UPDATE_SLAT_STOP = "slatStop";
	
	//sensor data
	public final static String UPDATE_POWER_CONSUMPTION = "powerConsumption";
	public final static String UPDATE_ENERGY_METER_VALUE = "energyMeterValue";
	public final static String UPDATE_ELECTRIC_METER_VALUE = "electricMeterValue";
	public final static String UPDATE_OUTPUT_VALUE = "outputValue";
	
	//scene
	/**
	 * A scene call can have the value between 0 and 127.  
	 */
	public final static String UPDATE_CALL_SCENE = "callScene";
	public final static String UPDATE_UNDO_SCENE = "undoScene";
	public final static String UPDATE_SCENE_OUTPUT = "sceneOutput";
	public final static String UPDATE_SCENE_CONFIG = "sceneConfig";
	
	/**
	 * Returns the state update value. 
	 * 
	 * NOTE: - For the OnOff-type is the value for off < 0 and for on > 0. 
	 * 		 - For all Increase- and Decrease-types is the value the new output value.
	 * 		 - For SceneCall-type is the value between 0 and 127 a scene call. 
	 * 		 - For all SceneUndo-types is the value the new output value.
	 * 
	 * @return new state value
	 */
	public int getValue();
	
	/**
	 * Returns the state update type.
	 * 
	 * @return state update type
	 */
	public String getType();
}
