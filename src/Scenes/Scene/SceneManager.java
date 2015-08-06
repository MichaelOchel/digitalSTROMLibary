package Scenes.Scene;

import DigitalSTROMDevices.Device;
import DigitalSTROMDevices.DeviceParameters.DSID;
import Scenes.SceneHandler.EventItem;
import Scenes.SceneHandler.SceneStatusListener;



public interface SceneManager {

	
	/**
	 * only works on openhabEvent! please copy "openhab/openhab.js" to your dSS
	 * server (/usr/share/dss/add-ons/) and "openhab.xml" to
	 * /usr/share/dss/data/subscriptions.d/ than you need to restart your dSS
	 * 
	 * If you don't, you will not get detailed infos about, what exactly
	 * happened (for example: which device was turned on by a browser or handy
	 * app )
	 * 
	 * @param eventItem
	 */
	public void handleEvent(EventItem eventItem);
	
	public void callInternalScene(InternalScene scene);
	
	public void callInternalScene(String sceneID);
	
	public void callDeviceScene(DSID dSID, Short sceneID);
	
	public void callDeviceScene(Device device, Short sceneID);
	
	public void undoInternalScene(InternalScene scene);
	
	public void undoInternalScene(String sceneID);
	
	public void undoDeviceScene(DSID dSID);
	
	public void undoDeviceScene(Device device);
	
	public void registerSceneListener(SceneStatusListener sceneListener);
	
	public void unregisterSceneListener(SceneStatusListener sceneListener);

	void addInternalScene(InternalScene intScene);

	void addEcho(String dsid, short sceneId);

	void addEcho(String internalSceneID);
}
