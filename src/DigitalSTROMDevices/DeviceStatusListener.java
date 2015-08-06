package DigitalSTROMDevices;

import DigitalSTROMDevices.DeviceParameters.ChangeableDeviceConfigEnum;
import DigitalSTROMDevices.DeviceParameters.DeviceStateUpdate;


/**
 * The {@link DeviceStatusListener} is notified when a device status has changed, a scene configuration is added to a device 
 * or a device has been removed or added.
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DeviceStatusListener {

	public final static String DEVICE_DESCOVERY = "DeviceDiscovey";
	
	/**
     * This method is called whenever the state of the given device has changed. The new state can be obtained by {@link Device#getNextESHThingUpdateStates()}.
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
     * This method is called whenever a configuration of an device has changed.
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
