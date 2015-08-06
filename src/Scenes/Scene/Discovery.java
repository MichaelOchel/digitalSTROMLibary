package Scenes.Scene;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import DSServerConnection.DigitalstromConnectionManager;
import DSServerConnection.HttpTransport;
import DSServerConnection.Costants.JSONApiResponseKeysEnum;
import DSServerConnection.impl.JSONResponseHandler;
import DigitalSTROMDevices.Device;
import DigitalSTROMDevices.DigitalSTROMStructureManager;
import Scenes.NamedScene.Constants.ApartmentSceneEnum;

public class Discovery {

	private HttpTransport transport = null;
	
	private List<InternalScene> namedScenes;
	
	DigitalSTROMStructureManager structureManager;
	DigitalstromConnectionManager connectionManager;
	
	private final String query = "/json/property/query?query=/apartment/zones/*(ZoneID)/groups/*(group)/scenes/*(scene,name)";
	//public final String loginQuery = "/json/system/login?user=dssadmin&password=dssadmin";
	
	public Discovery(DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManager structureManager){
		/*this.transport = new HttpTransportImpl(hostName, 
				GeneralConstants.DigitalSTROMBindingConstants.DEFAULT_CONNECTION_TIMEOUT, 
				GeneralConstants.DigitalSTROMBindingConstants.DEFAULT_READ_TIMEOUT);
		*/
		this.connectionManager = connectionManager;
		this.structureManager = structureManager;
		this.transport = connectionManager.getHttpTransport();
		this.namedScenes = new LinkedList<InternalScene>();
		for(Scenes.NamedScene.Constants.ApartmentSceneEnum apartmentScene : ApartmentSceneEnum.values()){
			//System.out.println(apartmentScene.toString().toLowerCase().replace("_", " "));
			
			InternalScene scene = new InternalScene(
					null,
					null,
					(short) apartmentScene.getSceneNumber(),
					"Apartment-Scene: " + apartmentScene.toString().toLowerCase().replace("_", " "));
			
			List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
			
			if(deviceList != null){
				scene.addReferenceDevices(deviceList);
			}
			this.namedScenes.add(scene);
		}
	}
	
	public boolean generateNamedScenes(){
		if(this.connectionManager.checkConnection()){
			String response = transport.execute(query + "&token="+this.connectionManager.getSessionToken());
			if(response == null) {
				return false;
			} else {
				JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
				if(JSONResponseHandler.checkResponse(responsJsonObj)){
					addScenesToList(JSONResponseHandler.getResultJSONObject(responsJsonObj));
					return true;
				}
			}
		}
		return false;
	}
	
	private void addScenesToList(JSONObject resultJsonObj){
		if (resultJsonObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES.getKey()) instanceof JSONArray) {
			JSONArray zones = (JSONArray) resultJsonObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES.getKey());
			for (int i=0; i< zones.size(); i++) {
				
				if (((JSONObject) zones.get(i)).get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES_GROUPS.getKey()) instanceof org.json.simple.JSONArray) {
					
					JSONArray groups = (JSONArray) ((JSONObject) zones.get(i)).get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES_GROUPS.getKey());
					
					for (int j=0; j< groups.size(); j++) {
						
						if (((JSONObject) groups.get(j)).get("scenes") instanceof org.json.simple.JSONArray) {
					
							JSONArray scenes = (JSONArray) ((JSONObject) groups.get(j)).get("scenes");
							for (int k=0; k < scenes.size(); k++) {
								if (scenes.get(k) instanceof org.json.simple.JSONObject) {
							
									if(this.namedScenes == null){
										this.namedScenes = new LinkedList<InternalScene>();
									}
									
									JSONObject sceneJsonObject = ((JSONObject) scenes.get(k));
									int zoneID = Integer.parseInt(((JSONObject) zones.get(i)).get("ZoneID").toString());
									short groupID = Short.parseShort(((JSONObject) groups.get(j)).get("group").toString());
									InternalScene scene = new InternalScene(
											zoneID,
											groupID,
											Short.parseShort(sceneJsonObject.get("scene").toString()),
											sceneJsonObject.get("name").toString());
									
									List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
									
									if(deviceList != null){
										scene.addReferenceDevices(deviceList);
									}
									
									this.namedScenes.add(scene);
								}
							}
						}
					}
				}
			}
		}

	}
	/*
	//server to low, 1 second delay
	public List<Device> generateDeviceList(int zoneID, short groupID){
		//System.out.println(zoneID + " " + groupID + " = " + buildSceneDeviceQuery(zoneID, groupID));
		String response = transport.execute(buildSceneDeviceQuery(zoneID, groupID) + "&token="+getSessionToken());
		List<Device> deviceList = null;
		if(response != null) {
			JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
			if(JSONResponseHandler.checkResponse(responsJsonObj)){
				JSONObject resultJsonObj = JSONResponseHandler.getResultJSONObject(responsJsonObj);
				if (resultJsonObj.get("devices") instanceof org.json.simple.JSONArray) {
					//System.out.println("1");
					JSONArray devices = (JSONArray) resultJsonObj.get("devices");
					if(!devices.isEmpty()){
						boolean otherAlgo = false;
						if(zoneID == 0 && groupID != 0){
							otherAlgo = true;
						} 
						
						deviceList = new LinkedList<Device>();
						for (int i=0; i < devices.size(); i++) {
							JSONDeviceImpl device = null;
							if(otherAlgo){
								if(((JSONObject) devices.get(i)).get("group"+groupID) instanceof JSONArray){
									if(!((JSONArray) ((JSONObject) devices.get(i)).get("group"+groupID)).isEmpty()){
										//device = new AbstractDevice(((JSONObject) devices.get(i)).get("dSUID").toString());
										device = new JSONDeviceImpl((JSONObject) devices.get(i));
									}
								}
							} else{
								//device = new AbstractDevice(((JSONObject) devices.get(i)).get("dSUID").toString());
								device = new JSONDeviceImpl((JSONObject) devices.get(i));
							}
							
							if(device != null){
								deviceList.add(device);
							}
						
						}
					}
				}
				//
				
			}
		}
		return deviceList;
	}
	
	public String buildSceneDeviceQuery(int zoneID, short groupID){
		if(zoneID == 0){
			if(groupID == 0){
				//System.out.println(zoneID + " " + groupID + " =  /json/property/query?query=/apartment/zones/zone0/devices/*(dSUID)");
				return "/json/property/query?query=/apartment/zones/zone0/devices/*(dSUID)"; 
			} else {
				return "/json/property/query?query=/apartment/zones/zone0/devices/*(dSUID)/groups/group"+groupID+"1(*)";
			}
		} else{
			return "/json/property/query?query=/apartment/zones/zone"+zoneID+"/groups/group"+groupID + "/devices/*(dSUID)";
		}
	}
	
	public String getSessionToken(){
		String response = transport.execute(loginQuery);
		
		JSONObject jObject = JSONResponseHandler.toJSONObject(response);
		if(JSONResponseHandler.checkResponse(jObject)){
			jObject = JSONResponseHandler.getResultJSONObject(jObject);
			return jObject.get("token").toString();
		}
		return null;
	}*/
	
	public List<InternalScene> getNamedSceneList(){
		return this.namedScenes;
	}
	
	@Override
	public String toString(){
		return this.namedScenes.toString();
	}
}
