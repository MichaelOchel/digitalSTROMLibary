package digitalSTROMListener;

import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.ChangeableDeviceConfigEnum;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceStateUpdate;


/**
 * The {@link DeviceStatusListener} is notified when a {@link Device} status has changed, a scene configuration is added to a {@link Device} 
 * or a device has been removed or added.
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DeviceStatusListener {

	/**
	 * Id of the device discovery listener.
	 */
	public final static String DEVICE_DESCOVERY = "DeviceDiscovey";
	
	/**
     * This method is called whenever the state of the given device has changed. The new state can be obtained by {@link #getNextESHThingUpdateStates()}.
     * 
     * @param deviceStateUpdate
     * 
     */
    public void onDeviceStateChanged(DeviceStateUpdate deviceStateUpdate);
    
    /**
     * This method is called whenever a device is removed.
     * 
     * @param device
     * 
     */
    public void onDeviceRemoved(Device device);

    /**
     * This method is called whenever a device is added.
     * 
     * @param device
     * 
     */
    public void onDeviceAdded(Device device);
    
    /**
     * This method is called whenever a configuration of an {@link Device} has changed. 
     * For which configuration are able have a look at {@link ChangeableDeviceConfigEnum}. 
     * 
     * @param whichConfig
     * 
     */
    public void onDeviceConfigChanged(ChangeableDeviceConfigEnum whichConfig);
    
    /**
     * This method is called whenever a scene configuration is added to a device 
     * 
     * @param sceneId 
     */
    public void onSceneConfigAdded(short sceneId);
    
    /**
     * Return the id of this {@link DeviceStatusListener}.
     * 
     * @return id
     */
    public String getID();

    
}
