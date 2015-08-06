package digitalSTROMStructure.digitalSTROMScene;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import digitalSTROMListener.SceneStatusListener;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.impl.TestDevice;

public class InternalScene {

	private final Short SCENE_ID;
	private final Short GROUP_ID;
	private final Integer ZONE_ID;
	private String SceneName;
	private final String NAMED_SCENE_ID;
	private boolean active = false;
	
	private List<Device> devices = Collections.synchronizedList(new LinkedList<Device>());
	private SceneStatusListener listener = null;
	
	public InternalScene(Integer zoneID, Short groupID, Short sceneID, String sceneName){
		if(sceneID == null) throw new IllegalArgumentException("The parameter sceneID can't be null!");
		this.SCENE_ID = sceneID;
		
		if(groupID == null){
			this.GROUP_ID = 0;
		}else{
			this.GROUP_ID = groupID;
		}
		
		if(zoneID == null){
			this.ZONE_ID = 0;
		}else{
			this.ZONE_ID = zoneID;
		}
		
		this.NAMED_SCENE_ID = this.ZONE_ID + "-" + this.GROUP_ID + "-" + this.SCENE_ID; 
		if(sceneName == null){
			this.SceneName =  this.NAMED_SCENE_ID;
		}else{
			this.SceneName = sceneName;
		}
	}

	/**
	 * Activate this Scene.
	 */
	public void activateScene(){
		this.active = true;
		informListener();
		if(this.devices != null){
			for(Device device: this.devices){
				device.callNamedScene(this);
			}
		}
	}
	
	/**
	 * Deactivate this Scene.
	 */
	public void deactivateScene(){
		this.active = false;
		informListener();
		if(this.devices != null){
			for(Device device: this.devices){
				device.undoScene();
			}
		}
	}
	
	private void informListener(){
		if(this.listener != null){
			listener.onSceneStateChanged(this.active);
		}
	}
	
	/**
	 * Returned true if this Scene is active, otherwise false.
	 * 
	 * @return active? (true = yes | false = no) 
	 */
	public boolean isActive(){
		return this.active;
	}
	
	public void addDevice(Device device){
		if(!this.devices.contains(device)){
			this.devices.add(device);
		}
		int prio = 0;
		if(this.listener == null){
			prio = 1000;
		} else{
			prio = 2000;
		}
		device.checkSceneConfig(SCENE_ID, prio);
	}
	
	public void addReferenceDevices(List<Device> deviceList){
		this.devices = deviceList;
		checkDeviceSceneConfig();
	}
	
	public void checkDeviceSceneConfig(){
		int prio = 0;
		if(this.listener == null){
			prio = 1000;
		} else{
			prio = 2000;
		}
		for(Device device: devices){
			device.checkSceneConfig(SCENE_ID, prio);
		}
	}
	public List<Device> getDeviceList(){
		return this.devices;
		
	}
	public void addDevices(List<Device> deviceList){
		for(Device device:deviceList){
			addDevice(device);
		}
	}
	
	public void removeDevice(TestDevice device){
		this.devices.remove(device);
	}
	
	public void updateDeviceList(List<Device> deviceList){
		if(!this.devices.equals(deviceList)){
			this.devices.clear();
			addDevices(deviceList);
		}
	}
	
	/**
	 * This method have a device to call if this scene was active and the device state has change. 
	 * 
	 * @param sceneNumber
	 */
	public void deviceSceneChanged(short sceneNumber){
		if(this.SCENE_ID != sceneNumber){
			this.active = false;
			informListener();
		}
	}
	/**
	 * Returns the Scene name.
	 * 
	 * @return scene name
	 */
	public String getSceneName() {
		return SceneName;
	}

	/**
	 * Sets the Scene name to the given scene name.
	 * 
	 * @param sceneName
	 */
	public void setSceneName(String sceneName) {
		SceneName = sceneName;
	}

	/**
	 * Returns the Scene id of this scene call.
	 * 
	 * @return scene id
	 */
	public Short getSceneID() {
		return SCENE_ID;
	}

	/**
	 * Returns the group id of this scene call.
	 * 
	 * @return group id
	 */
	public Short getGroupID() {
		return GROUP_ID;
	}

	/**
	 * Returns the zone id of this scene call.
	 * 
	 * @return zone id
	 */
	public Integer getZoneID() {
		return ZONE_ID;
	}

	/**
	 * Returns the id of this scene call.
	 * 
	 * @return scene call id
	 */
	public String getID() {
		return NAMED_SCENE_ID;
	}
	
	public synchronized void registerSceneListener(SceneStatusListener listener){
		this.listener = listener;
		this.listener.onSceneAdded(this);
		checkDeviceSceneConfig();
		
	}
	
	public synchronized void unregisterSceneListener(){
		this.listener.onSceneRemoved(this);
		this.listener = null;
	}

	@Override
	public String toString() {
		return "NamedScene [SceneName=" + SceneName + ", NAMED_SCENE_ID="
				+ NAMED_SCENE_ID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((NAMED_SCENE_ID == null) ? 0 : NAMED_SCENE_ID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InternalScene))
			return false;
		InternalScene other = (InternalScene) obj;
		if (NAMED_SCENE_ID == null) {
			if (other.getID() != null)
				return false;
		} else if (!NAMED_SCENE_ID.equals(other.getID()))
			return false;
		return true;
	}
	
	
	
}
