package Scenes.Scene;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import DSServerConnection.DigitalstromConnectionManager;
import DSServerConnection.Costants.JSONApiResponseKeysEnum;
import DSServerConnection.impl.JSONResponseHandler;
import DigitalSTROMDevices.DigitalSTROMStructureManager;
import Scenes.NamedScene.Constants.ApartmentSceneEnum;
import Scenes.NamedScene.Constants.SceneEnum;
import Scenes.NamedScene.Constants.ZoneSceneEnum;
import Scenes.SceneHandler.SceneStatusListener;

public class SceneDiscovery {

	//private HttpTransport transport = null;
	
	private List<InternalScene> namedScenes  = new LinkedList<InternalScene>();
	private boolean genList = false;
	
	//private DigitalSTROMStructureManager structureManager;
	//private DigitalstromConnectionManager connectionManager;
	
	private SceneManager sceneManager;
	private SceneStatusListener discovery = null; 
	
	private final String query = "/json/property/query?query=/apartment/zones/*(ZoneID)/groups/*(group)/scenes/*(scene,name)";
	private final String reachableScenesQuery = "/json/zone/getReachableScenes?id=";//1237&groupID=1";
	private final String reachableGroupsQuery = "/json/apartment/getReachableGroups?token=";
	//public final String loginQuery = "/json/system/login?user=dssadmin&password=dssadmin";
	
	public SceneDiscovery(SceneManager sceneManager){ //DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManager structureManager
		/*this.transport = new HttpTransportImpl(hostName, 
				GeneralConstants.DigitalSTROMBindingConstants.DEFAULT_CONNECTION_TIMEOUT, 
				GeneralConstants.DigitalSTROMBindingConstants.DEFAULT_READ_TIMEOUT);
		*/
		this.sceneManager = sceneManager;
		//this.connectionManager = connectionManager;
		//this.structureManager = structureManager;
		//this.transport = connectionManager.getHttpTransport();
		//this.namedScenes = new LinkedList<InternalScene>();
		
	
	}
	
	public SceneDiscovery( boolean genList){
		this.genList = genList;
	}
	
	public void generateAllScenes(DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManager structureManager){
		generateNamedScenes(connectionManager);
		generateAppartmentScence();
		generateZoneScenes(connectionManager);
		generateReachableScenes(connectionManager, structureManager);
	}
	
	public boolean generateNamedScenes(DigitalstromConnectionManager connectionManager){
		if(connectionManager.checkConnection()){
			String response = connectionManager.getHttpTransport().execute(query + "&token="+connectionManager.getSessionToken());
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
	
	public void generateAppartmentScence(){
		for(Scenes.NamedScene.Constants.ApartmentSceneEnum apartmentScene : ApartmentSceneEnum.values()){
			//System.out.println(apartmentScene.toString().toLowerCase().replace("_", " "));
			
			InternalScene scene = new InternalScene(
					null,
					null,
					(short) apartmentScene.getSceneNumber(),
					"Apartment-Scene: " + apartmentScene.toString().toLowerCase().replace("_", " "));
			if(genList){
				System.out.print("Appartmend Scene: " + scene.toString());
				if(this.namedScenes.add(scene)){
					System.out.print(" added to list\n");
				}
			} else{
				sceneDiscoverd(scene);
			}
			
			/*List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
			
			if(deviceList != null){
				scene.addReferenceDevices(deviceList);
			}
			System.out.print("Appartmend Scene: " + scene.toString());
			if(this.namedScenes.add(scene)){
				System.out.print(" added to list\n");
			}*/
		}
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
							
									/*if(this.namedScenes == null){
										this.namedScenes = new LinkedList<InternalScene>();
									}*/
									
									JSONObject sceneJsonObject = ((JSONObject) scenes.get(k));
									int zoneID = Integer.parseInt(((JSONObject) zones.get(i)).get("ZoneID").toString());
									short groupID = Short.parseShort(((JSONObject) groups.get(j)).get("group").toString());
									InternalScene scene = new InternalScene(
											zoneID,
											groupID,
											Short.parseShort(sceneJsonObject.get("scene").toString()),
											sceneJsonObject.get("name").toString());
									
									if(genList){
										System.out.print("Namend Scene: " + scene.toString());
										if(this.namedScenes.add(scene)){
											System.out.print(" added to list\n");
										}
									} else{
										sceneDiscoverd(scene);
									}
									/*
									List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
									
									if(deviceList != null){
										scene.addReferenceDevices(deviceList);
									}
									
									System.out.print("Namend Scene: " + scene.toString());
									if(this.namedScenes.add(scene)){
										System.out.print(" added to list\n");
									}*/
								}
							}
						}
					}
				}
			}
		}

	}
	
	public boolean generateZoneScenes(DigitalstromConnectionManager connectionManager){
		HashMap<Integer, List<Short>> reachableGroups = getReachableGroups(connectionManager);
		
		if(reachableGroups != null){
			for(Integer zoneID: reachableGroups.keySet()){
				if(!reachableGroups.get(zoneID).isEmpty()){
					for(ZoneSceneEnum zoneScene : ZoneSceneEnum.values()){
						InternalScene scene = new InternalScene(
								zoneID,
								null,
								(short) zoneScene.getSceneNumber(),
								"Zone-Scene: " + zoneScene.toString().toLowerCase().replace("_", " "));
						if(genList){
							System.out.print("Appartmend Scene: " + scene.toString());
							if(this.namedScenes.add(scene)){
								System.out.print(" added to list\n");
							}
						} else{
							sceneDiscoverd(scene);
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean  generateReachableScenes(DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManager structureManager){
		HashMap<Integer, List<Short>> reachableGroups = getReachableGroups(connectionManager);
		
		if(reachableGroups != null){
			for(Integer zoneID: reachableGroups.keySet()){
				List<Short> groupIDs = reachableGroups.get(zoneID);
				if(groupIDs != null){
					if(connectionManager.checkConnection()){
						for(Short groupID: groupIDs){
							String response = connectionManager.getHttpTransport().execute(this.reachableScenesQuery + zoneID + 
									"&groupID=" + groupID + 
									"&token="+connectionManager.getSessionToken());
							if(response == null) {
								return false;
							} else {
								JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
								if(JSONResponseHandler.checkResponse(responsJsonObj)){
									JSONObject resultJsonObj =JSONResponseHandler.getResultJSONObject(responsJsonObj);
									if (resultJsonObj.get(JSONApiResponseKeysEnum.ZONE_GET_REACHABLE_SCENES.getKey()) instanceof JSONArray) {
										JSONArray scenes = (JSONArray) resultJsonObj.get(JSONApiResponseKeysEnum.ZONE_GET_REACHABLE_SCENES.getKey());
										if(scenes != null){
											for (int i=0; i< scenes.size(); i++) {
												short sceneNumber = Short.parseShort(scenes.get(i).toString());
												String sceneName = null;
												if(SceneEnum.getScene(sceneNumber) != null){
													if(structureManager.getZoneName(zoneID) != null){
														sceneName = "Zone: " + structureManager.getZoneName(zoneID);
														if(structureManager.getGroupZoneName(zoneID, groupID) != null){
															sceneName = sceneName + " Group: " + structureManager.getGroupZoneName(zoneID, groupID);  
														} else{
															sceneName = sceneName + " Group: " + groupID;
														}
													} else{
														sceneName = "Zone: " + zoneID + " Group: " + groupID;
													}
													sceneName = sceneName + " Scene: " + SceneEnum.getScene(sceneNumber).toString().toLowerCase().replace("_", " ");
												}
												InternalScene scene = new InternalScene(
														zoneID,
														groupID,
														sceneNumber,
														sceneName);
													
												if(genList){
													System.out.print("Reachable Scene: " + scene.toString());
													if(this.namedScenes.add(scene)){
														System.out.print(" added to list\n");
													}
												} else{
													sceneDiscoverd(scene);
												}
												/*
												List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
												
												if(deviceList != null){
													scene.addReferenceDevices(deviceList);
												}
												
												System.out.print("Reachable Scene: " + scene.toString());
												if(this.namedScenes.add(scene)){
													System.out.print(" added to list\n");
												}
												*/
											}
										} 
									}
									
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	/*
	public boolean generateReachableScenes(DigitalstromConnectionManager connectionManager, DigitalSTROMStructureManager structureManager){
		Set<Integer> zoneIDs = structureManager.getZoneIDs();
		if(zoneIDs != null){
			for(Integer zoneID: zoneIDs){
				if(zoneID != 0) {
					Set<Short> groupIDs = structureManager.getGroupsFromZoneX(zoneID).keySet();
					if(groupIDs != null){
						if(connectionManager.checkConnection()){
							for(Short groupID: groupIDs){
								if(groupID != 0) {
									String response = connectionManager.getHttpTransport().execute(this.reachableScenesQuery + zoneID + 
											"&groupID=" + groupID + 
											"&token="+connectionManager.getSessionToken());
									if(response == null) {
										return false;
									} else {
										JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
										if(JSONResponseHandler.checkResponse(responsJsonObj)){
											JSONObject resultJsonObj =JSONResponseHandler.getResultJSONObject(responsJsonObj);
											if (resultJsonObj.get(JSONApiResponseKeysEnum.ZONE_GET_REACHABLE_SCENES.getKey()) instanceof JSONArray) {
												JSONArray scenes = (JSONArray) resultJsonObj.get(JSONApiResponseKeysEnum.ZONE_GET_REACHABLE_SCENES.getKey());
												if(scenes != null){
													for (int i=0; i< scenes.size(); i++) {
														short sceneNumber = Short.parseShort(scenes.get(i).toString());
														String sceneName = null;
														if(ZoneSceneEnum.getZoneScene(sceneNumber) != null){
															if(structureManager.getZoneName(zoneID) != null){
																sceneName = "Zone: " + structureManager.getZoneName(zoneID);
																if(structureManager.getGroupZoneName(zoneID, groupID) != null){
																	sceneName = sceneName + " Group: " + structureManager.getGroupZoneName(zoneID, groupID);  
																} else{
																	sceneName = sceneName + " Group: " + groupID;
																}
															} else{
																sceneName = "Zone: " + zoneID + " Group: " + groupID;
															}
															sceneName = sceneName + ZoneSceneEnum.getZoneScene(sceneNumber).toString().toLowerCase().replace("_", " ");
														}
														InternalScene scene = new InternalScene(
																zoneID,
																groupID,
																sceneNumber,
																sceneName);
														
														if(genList){
															System.out.print("Reachable Scene: " + scene.toString());
															if(this.namedScenes.add(scene)){
																System.out.print(" added to list\n");
															}
														} else{
															sceneDiscoverd(scene);
														}
														
														List<Device> deviceList = this.structureManager.getReferenceDeviceListFromZoneXGroupX(scene.getSceneID(), scene.getGroupID());
														
														if(deviceList != null){
															scene.addReferenceDevices(deviceList);
														}
														
														System.out.print("Reachable Scene: " + scene.toString());
														if(this.namedScenes.add(scene)){
															System.out.print(" added to list\n");
														}
														
													}
												} 
											}
										}
									}
								}
							}
						}
						//do not flooding the DSS, sleep 1 second rule XY 
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	*/
	private HashMap<Integer, List<Short>> getReachableGroups(DigitalstromConnectionManager connectionManager){
		HashMap<Integer, List<Short>> reachableGroupsMap = null;
		if(connectionManager.checkConnection()){
			String response = connectionManager.getHttpTransport().execute(this.reachableGroupsQuery + connectionManager.getSessionToken());
			if(response == null) {
				return null;
			} else {
				JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
				if(JSONResponseHandler.checkResponse(responsJsonObj)){
					JSONObject resultJsonObj =JSONResponseHandler.getResultJSONObject(responsJsonObj);
					if (resultJsonObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES.getKey()) instanceof JSONArray) {
						JSONArray zones = (JSONArray) resultJsonObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES.getKey());
						reachableGroupsMap = new HashMap<Integer, List<Short>>(zones.size());
						List<Short> groupList;
						for(int i = 0; i < zones.size(); i++){
							if (((JSONObject) zones.get(i)).get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES_GROUPS.getKey()) instanceof JSONArray) {
								JSONArray groups = (JSONArray) ((JSONObject) zones.get(i)).get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE_ZONES_GROUPS.getKey());
								groupList = new LinkedList<Short>();
								for(int k = 0; k < groups.size(); k++){
									groupList.add(Short.parseShort(groups.get(k).toString()));
								}
								reachableGroupsMap.put(Integer.parseInt(((JSONObject) zones.get(i)).
										get("zoneID").toString()), 
										groupList);
							}
						}
					}
				}
			}
		}
		return reachableGroupsMap;
	}
	
	public void sceneDiscoverd(InternalScene scene){
		if(this.discovery != null) this.discovery.onSceneAdded(scene);
		this.sceneManager.addInternalScene(scene);
	}
	
	public void registerSceneStatusListener(SceneStatusListener listener){
		this.discovery = listener;
	}
	
	public void unRegisterSceneStatusListener(){
		this.discovery = null;
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
		//System.out.println(this.namedScenes.toString());
		//return null; 
		return this.namedScenes.toString();
	}
}
