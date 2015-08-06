package digitalSTROMManager.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import digitalSTROMListener.SceneStatusListener;
import digitalSTROMManager.DigitalSTROMSceneManager;
import digitalSTROMManager.DigitalstromConnectionManager;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;
import digitalSTROMStructure.digitalSTROMScene.InternalScene;
import digitalSTROMStructure.digitalSTROMScene.SceneDiscovery;
import digitalSTROMStructure.digitalSTROMScene.constants.EventPropertyEnum;
import digitalSTROMStructure.digitalSTROMScene.sceneEvent.EventItem;
import digitalSTROMStructure.digitalSTROMScene.sceneEvent.EventListener;

public class DigitalSTROMSceneManagerImpl implements DigitalSTROMSceneManager {

	private List<String> echoBox = Collections
			.synchronizedList(new LinkedList<String>());
	private Map<String, InternalScene> internalSceneMap = Collections.synchronizedMap(new HashMap<String, InternalScene>()); 
	
	private EventListener eventListener;
	private DigitalSTROMStructureManagerImpl structureManager;
	private DigitalstromConnectionManager connectionManager;
	private SceneDiscovery discovery;
	
	
	public DigitalSTROMSceneManagerImpl(DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManagerImpl structureManager){
		this.structureManager = structureManager;
		this.connectionManager = connectionManager;
	}
	
	public void start(){
		eventListener = new EventListener(connectionManager, this);
		this.eventListener.start();
	}
	
	public void stop(){
		this.eventListener.shutdown();
		this.eventListener = null;
	}
	
	@Override
	public void handleEvent(EventItem eventItem) {
		if (eventItem != null) {
			boolean isCallScene = false;
			String isCallStr = eventItem.getProperties().get(
					EventPropertyEnum.EVENT_NAME);
			if (isCallStr != null) {
				isCallScene = isCallStr.equals("callScene");
			}
			
			boolean isDeviceCall = false;
			
			String deviceCallStr = eventItem.getProperties().get(
					EventPropertyEnum.IS_DEVICE_CALL);
			if (deviceCallStr != null) {
				isDeviceCall = deviceCallStr.equals("true");
			}
			
			if(isDeviceCall){
				String dsidStr = null;
				dsidStr = eventItem.getProperties().get(EventPropertyEnum.DSID);
				short sceneId = -1;
				String sceneStr = eventItem.getProperties().get(
						EventPropertyEnum.SCENEID);
				if (sceneStr != null) {
					try {
						sceneId = Short.parseShort(sceneStr);
					} catch (java.lang.NumberFormatException e) {
						System.err.println("NumberFormatException by handling event at parsing sceneId: "
								+ sceneStr);
					}
				}
				
				if (!isEcho(dsidStr, sceneId)) {
					if(isCallScene){
						this.callDeviceScene(new DSID(dsidStr), sceneId);
					} else{
						this.undoDeviceScene(new DSID(dsidStr));
					}
				}
				
			} else {
				String intSceneID = null;
				
				String zoneIDStr = eventItem.getProperties().get(
						EventPropertyEnum.ZONEID);
				String sceneIDStr = eventItem.getProperties().get(
						EventPropertyEnum.SCENEID);
				String groupIDStr = eventItem.getProperties().get(
						EventPropertyEnum.GROUPID);
	
				if (zoneIDStr != null && sceneIDStr != null && groupIDStr != null) {
					intSceneID = zoneIDStr + "-" + groupIDStr + "-" + sceneIDStr;
					if(!isEcho(intSceneID)){
						if(isCallScene){
							this.callInternalScene(intSceneID);
						} else{
							this.undoInternalScene(intSceneID);
						}
					}
				}
			}
		}
		
	}

	private boolean isEcho(String dsid, short sceneId) {
		String echo = dsid + "-" + sceneId;
		return isEcho(echo);
	}
	
	private boolean isEcho(String echoID) {
		if (echoBox.contains(echoID)) {
			echoBox.remove(echoID);
			return true;
		}
		
		return false;
	}
	
	// ... we want to ignore own 'command-echos'
	@Override
	public void addEcho(String dsid, short sceneId) {
		addEcho(dsid + "-" + sceneId);
	}
	
	// ... we want to ignore own 'command-echos'
	@Override
	public void addEcho(String internalSceneID) {
		echoBox.add(internalSceneID);
	}
		
	@Override
	public void callInternalScene(InternalScene scene) {
		//eig. unnötig
		InternalScene intScene = this.internalSceneMap.get(scene.getID());
		if(intScene != null){
			intScene.activateScene();
		} else{
			scene.addReferenceDevices(this.structureManager.
					getReferenceDeviceListFromZoneXGroupX(scene.getZoneID(), scene.getGroupID()));
			this.internalSceneMap.put(scene.getID(), scene);
		}
	}

	@Override
	public void callInternalScene(String sceneID) {
		InternalScene intScene = this.internalSceneMap.get(sceneID);
		if(intScene != null){
			intScene.activateScene();
		} else{
			intScene = createNewScene(sceneID);
			//addInternalScene(intScene);
			//discovery oder hier listener?
			
			if(intScene != null){
				discovery.sceneDiscoverd(intScene);
				intScene.activateScene();
			}
		}
		
	}
	
	@Override
	public void addInternalScene(InternalScene intScene){
		if(!this.internalSceneMap.containsKey(intScene.getID())){
			intScene.addReferenceDevices(this.structureManager.
				getReferenceDeviceListFromZoneXGroupX(intScene.getZoneID(), intScene.getGroupID()));
			this.internalSceneMap.put(intScene.getID(), intScene);
			
		}
	}
	
	private InternalScene createNewScene(String sceneID){
		String[] sceneData = sceneID.split("-");
		if(sceneData.length == 3){
			InternalScene intScene = new InternalScene(Integer.parseInt(sceneData[0]), 
					Short.parseShort(sceneData[1]), 
					Short.parseShort(sceneData[2]), 
					null);
			
			return intScene;
		} else{
			//Fehlermeldung
			return null;
		}
	}

	@Override
	public void callDeviceScene(DSID dSID, Short sceneID) {
		Device device = this.structureManager.getDeviceByDSID(dSID);
		if(device != null){
			device.callScene(sceneID);
		} else{
			//Fehlermeldung
		}
		
	}

	@Override
	public void callDeviceScene(Device device, Short sceneID) {
		if(device != null){
			callDeviceScene(device.getDSID(),sceneID);
		}
		
	}

	@Override
	public void undoInternalScene(InternalScene scene) {
		if(scene != null){
			undoInternalScene(scene.getID());
		}
	}

	@Override
	public void undoInternalScene(String sceneID) {
		InternalScene intScene = this.internalSceneMap.get(sceneID);
		if(intScene != null){
			intScene.deactivateScene();
		} else{
			intScene = createNewScene(sceneID);
			if(intScene != null) intScene.deactivateScene();
		}
		
	}

	@Override
	public void undoDeviceScene(DSID dSID) {
		Device device = this.structureManager.getDeviceByDSID(dSID);
		if(device != null){
			device.undoScene();
		}
		
	}

	@Override
	public void undoDeviceScene(Device device) {
		if(device != null){
			undoDeviceScene(device.getDSID());
		}
	}

	@Override
	public void registerSceneListener(SceneStatusListener sceneListener) {
		if(sceneListener != null){
			String id = sceneListener.getID();
			if(id.equals(SceneStatusListener.SCENE_DESCOVERY)){
				this.discovery = new SceneDiscovery(this);
				discovery.registerSceneStatusListener(sceneListener);
				discovery.generateAllScenes(connectionManager, structureManager);
			} else {
				InternalScene intScene = this.internalSceneMap.get(sceneListener.getID());
				if(intScene != null){
					intScene.registerSceneListener(sceneListener);
				} else{
					//TODO: Fehlermeldung
				}
			}
		} else{
			//TODO: Fehlermeldung
		}
		
	}

	@Override
	public void unregisterSceneListener(SceneStatusListener sceneListener) {
		if(sceneListener != null){
			String id = sceneListener.getID();
			if(id.equals(SceneStatusListener.SCENE_DESCOVERY)){
				this.discovery.unRegisterSceneStatusListener();
			} else {
				InternalScene intScene = this.internalSceneMap.get(sceneListener.getID());
				if(intScene != null){
					intScene.unregisterSceneListener();
				} else{
					//TODO:Fehlermeldung
				}
			}
		} else{
			//TODO: Fehlermeldung
		}
		
	}
	
	
}
