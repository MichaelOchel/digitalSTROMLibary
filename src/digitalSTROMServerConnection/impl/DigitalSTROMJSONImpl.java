package digitalSTROMServerConnection.impl;
/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import digitalSTROMServerConnection.DigitalSTROMAPI;
import digitalSTROMServerConnection.HttpTransport;
import digitalSTROMServerConnection.constants.JSONApiResponseKeysEnum;
import digitalSTROMServerConnection.constants.JSONRequestConstants;
import digitalSTROMStructure.Apartment;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.CachedMeteringValue;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceConfig;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceParameterClassEnum;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceSceneSpec;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.JSONCachedMeteringValueImpl;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.JSONDeviceConfigImpl;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.JSONDeviceSceneSpecImpl;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.MeteringTypeEnum;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.MeteringUnitsEnum;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.SensorIndexEnum;
import digitalSTROMStructure.digitalSTROMDevices.impl.JSONDeviceImpl;
import digitalSTROMStructure.digitalSTROMScene.constants.Scene;
import digitalSTROMStructure.digitalSTROMScene.constants.ZoneSceneEnum;
import digitalSTROMStructure.impl.JSONApartmentImpl;


/**
 * @author 	Alexander Betker
 * @author Alex Maier
 * @since 1.3.0
 * @version	digitalSTROM-API 1.14.5
 */
public class DigitalSTROMJSONImpl implements DigitalSTROMAPI{
	
	//private static final Logger logger = LoggerFactory.getLogger(DigitalSTROMJSONImpl.class);
	
	private HttpTransport transport = null;
	
	public DigitalSTROMJSONImpl(HttpTransport transport) {
		this.transport = transport;
	}
	
	public DigitalSTROMJSONImpl(String uri, int connectTimeout, int readTimeout) {
		this.transport = new HttpTransportImpl(uri, connectTimeout, readTimeout);
	}

	private boolean withParameterGroupId(int groupID) {
		return (groupID > -1);
	}

	@Override
	public boolean callApartmentScene(String token, int groupID, String groupName,
			Scene sceneNumber, boolean force) {
		if (sceneNumber != null && isValidApartmentSceneNumber(sceneNumber.getSceneNumber())) {
			String response = null;
			
			if (groupName != null) {
				if (withParameterGroupId(groupID)) {
					if (force) {
						response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
								JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
					}
					else {
						response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
					}
				}
				else
				{
					if (force) {
						response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
								JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
					}
					else {
						response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
					}
				}
				
			}
			else if (withParameterGroupId(groupID)) {
				if (force) {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
							JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
				}
				else {
				response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
						JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
				}
			}
			else {
				if (force) {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
							JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
				}
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean undoApartmentScene(String token, int groupID, String groupName, Scene sceneNumber) {
		if (sceneNumber != null && isValidApartmentSceneNumber(sceneNumber.getSceneNumber())) {
			String response = null;
			
			if (groupName != null) {
				if (withParameterGroupId(groupID)) {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
							JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
				}
			}
			else if (withParameterGroupId(groupID)) {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
			}
			else {
					response = transport.execute(JSONRequestConstants.JSON_APARTMENT_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}
	
	private boolean isValidApartmentSceneNumber(int sceneNumber) {
		return (sceneNumber > -1 && sceneNumber < 256);
	}

	@Override
	public Apartment getApartmentStructure(String token) {
		String response = null;
		
		response = transport.execute(JSONRequestConstants.JSON_APARTMENT_GET_STRUCTURE+JSONRequestConstants.PARAMETER_TOKEN+token);
		
		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject apartObj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			if (apartObj != null && apartObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE.getKey()) != null) {
				return new JSONApartmentImpl((JSONObject) apartObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_STRUCTURE.getKey()));
			}
		}
			
		return null;
	}

	@Override
	public List<Device> getApartmentDevices(String token, boolean unassigned) {
		String response = null;
			
		if (unassigned) {
			response = transport.execute(JSONRequestConstants.JSON_APARTMENT_GET_DEVICES+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_UNASSIGNED_TRUE);
		}
		else { 
			response = transport.execute(JSONRequestConstants.JSON_APARTMENT_GET_DEVICES+JSONRequestConstants.PARAMETER_TOKEN+token);
		}
		
		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj) && responseObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_DEVICES.getKey()) instanceof JSONArray) {
			JSONArray array = (JSONArray)responseObj.get(JSONApiResponseKeysEnum.APARTMENT_GET_DEVICES.getKey());
			
			List<Device> deviceList = new LinkedList<Device>();
				
			for (int i=0; i< array.size(); i++) {
				if (array.get(i) instanceof org.json.simple.JSONObject) {
					deviceList.add(new JSONDeviceImpl((JSONObject)array.get(i)));
				}
			}
			return deviceList;	
			
		}
			
		return new LinkedList<Device>();
	}

	private boolean withParameterZoneId(int id) {
		return (id > -1);
	}

	@Override
	public boolean callZoneScene(String token, int id, String name, int groupID,
			String groupName, ZoneSceneEnum sceneNumber, boolean force) {
		if (sceneNumber != null && validZoneScene(sceneNumber.getSceneNumber(), id) && (withParameterZoneId(id) || name != null)) {
				String response = null;
				
				if (withParameterZoneId(id)) {
					if (name != null) {
						if (withParameterGroupId(groupID)) {
							if (groupName != null) {
								if (force) {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
											JSONRequestConstants.INFIX_PARAMETER_ID+id+
											JSONRequestConstants.INFIX_PARAMETER_NAME+name+
											JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
											JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
								}
								else {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
								}
							}
							else {
								if (force) {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
											JSONRequestConstants.INFIX_PARAMETER_ID+id+
											JSONRequestConstants.INFIX_PARAMETER_NAME+name+
											JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
											JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
								}
								else {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
								}
							}
						}
						else if (groupName != null) {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+id+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
							}
						}
						else {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+id+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
							}
						}
						
					}
					else {
						if (withParameterGroupId(groupID)) {
							if (groupName != null) {
								if (force) {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
											JSONRequestConstants.INFIX_PARAMETER_ID+id+
											JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
											JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
								}
								else {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
								}
							}
							else {
								if (force) {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
											JSONRequestConstants.INFIX_PARAMETER_ID+id+
											JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
											JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
											JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
								}
								else {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
								}
							}
						}
						else if (groupName != null) {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+id+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
							}
						}
						else {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+id+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+id+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
							}
								
						}
						
					}
						
				}
				else if (name != null) {
					if (withParameterGroupId(groupID)) {
						if (groupName != null) {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
							}
						}
						else {
							if (force) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_NAME+name+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
							}
						}
						
					}
					else if (groupName != null) {
						if (force) {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName+
									JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
						}
						else {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_NAME+name+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
						}
					}
					else {
						if (force) {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+name+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
						}
						else {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_NAME+name+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
						}
					}
					
				}
				
				if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
					return true;
				}
			
			}
		
		return false;
	}
	
	@Override
	public boolean undoZoneScene(String token, int zoneID, String zoneName, int groupID,
			String groupName, ZoneSceneEnum sceneNumber) {
		if (sceneNumber != null && validZoneScene(sceneNumber.getSceneNumber(), zoneID) && 
				(withParameterZoneId(zoneID) || zoneName != null)) {
				String response = null;
				
				if (withParameterZoneId(zoneID)) {
					if (zoneName != null) {
						if (withParameterGroupId(groupID)) {
							if (groupName != null) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
							}
						}
						else if (groupName != null) {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
									JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
						}
						else {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
									JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
						}
					}
					else {
						if (withParameterGroupId(groupID)) {
							if (groupName != null) {
									response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
							}
							else {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
							}
						}
						else if (groupName != null) {
								response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
										JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
						}
						else {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
										JSONRequestConstants.INFIX_PARAMETER_ID+zoneID+
										JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());	
						}
						
					}
						
				}
				else if (zoneName != null) {
					if (withParameterGroupId(groupID)) {
						if (groupName != null) {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
						}
						else {
							response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
									JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
									JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
									JSONRequestConstants.INFIX_PARAMETER_GROUP_ID+groupID);
						}
						
					}
					else if (groupName != null) {
						response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
								JSONRequestConstants.INFIX_PARAMETER_GROUP_NAME+groupName);
					}
					else {
						response = transport.execute(JSONRequestConstants.JSON_ZONE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_NAME+zoneName+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
					}
				}
				
				if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
					return true;
				}
			
			}
		
		return false;
	}

	private boolean validZoneScene(int sceneNumber, int zoneId) {
		if (zoneId == 0) {
			return (sceneNumber > -1 && sceneNumber < 256);
		}
		else {
			return (sceneNumber > -1 && sceneNumber < 64);
		}
	}

	@Override
	public boolean turnDeviceOn(String token, DSID dsid, String name) {
		if (((dsid != null && dsid.getValue() != null) || name != null)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_ON+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_ON+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue());
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_ON+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name);
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean turnDeviceOff(String token, DSID dsid, String name) {
		if (((dsid != null && dsid.getValue() != null) || name != null)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_OFF+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_OFF+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue());
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_TURN_OFF+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name);
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}
		
	@Override
	public DeviceConfig getDeviceConfig(String token, DSID dsid, String name, DeviceParameterClassEnum class_, int index) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && class_ != null && withParameterIndex(index)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_CONFIG+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_CLASS+class_.getClassIndex()+
							JSONRequestConstants.INFIX_PARAMETER_INDEX+index);
				}
				else { response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_CONFIG+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
						JSONRequestConstants.INFIX_PARAMETER_CLASS+class_.getClassIndex()+
						JSONRequestConstants.INFIX_PARAMETER_INDEX+index);
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_CONFIG+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_CLASS+class_.getClassIndex()+
						JSONRequestConstants.INFIX_PARAMETER_INDEX+index); 
			}
			
			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			
			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject configObject = JSONResponseHandler.getResultJSONObject(responseObj);
				
				if (configObject != null) {
					return new JSONDeviceConfigImpl(configObject);
				}
			}
			
		}
		return null;
	}
	
	private boolean withParameterIndex(int index) {
		return (index > -1);
	}

	@Override
	public int getDeviceOutputValue(String token, DSID dsid, String name,
			int offset) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && withParameterOffset(offset)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset);
				}
				else { response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
						JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset);
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset);
			}
			
			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			
			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject valueObject = JSONResponseHandler.getResultJSONObject(responseObj);
				
				if (valueObject != null && valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_OUTPUT_VALUE.getKey()) != null) {
					int value = -1;
					try {
						value = Integer.parseInt(valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_OUTPUT_VALUE.getKey()).toString());
					}
					catch (java.lang.NumberFormatException e) {
						//logger.error
						System.err.println("NumberFormatException by getDeviceOutputValue: "+valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_OUTPUT_VALUE.getKey()).toString());
					}
					return value;
				}
			}
			
		}
		return -1;
	}
	
	private boolean withParameterOffset(int offset) {
		return (offset > -1);
	}
	
	private boolean withParameterValue(int value) {
		return (value > -1);
	}

	@Override
	public boolean setDeviceOutputValue(String token, DSID dsid, String name,
			int offset, int value) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && offset > -1 && withParameterValue(value)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset+
							JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
						JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset+
						JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_OUTPUT_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_OFFSET+offset+
						JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public DeviceSceneSpec getDeviceSceneMode(String token, DSID dsid, String name,
			short sceneID) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && sceneID >-1 ) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SCENE_MODE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_ID+sceneID);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SCENE_MODE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
						JSONRequestConstants.INFIX_PARAMETER_SCENE_ID+sceneID);
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SCENE_MODE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_SCENE_ID+sceneID);
			}
			
			
			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			
			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject sceneSpec = JSONResponseHandler.getResultJSONObject(responseObj);
				
				if (sceneSpec != null) {
					return new JSONDeviceSceneSpecImpl(sceneSpec);
				}
				
			}
			
		}
		return null;
	}

	@Override
	public short getDeviceSensorValue(String token, DSID dsid, String name, SensorIndexEnum sensorIndex) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && sensorIndex != null ) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SENSOR_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_SENSOR_INDEX+sensorIndex.getIndex());
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SENSOR_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
						JSONRequestConstants.INFIX_PARAMETER_SENSOR_INDEX+sensorIndex.getIndex());
				}
			}
			else if (name != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_GET_SENSOR_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_SENSOR_INDEX+sensorIndex.getIndex());
			}
			
			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			
			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject valueObject = JSONResponseHandler.getResultJSONObject(responseObj);
				
				if (valueObject != null && valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_SENSOR_VALUE_SENSOR_VALUE.getKey()) != null) {
					short value = -1;
					try {
						value = Short.parseShort(valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_SENSOR_VALUE_SENSOR_VALUE.getKey()).toString());
					}
					catch (java.lang.NumberFormatException e) {
						//logger.error
						System.err.println("NumberFormatException by getDeviceSensorValue: "+valueObject.get(JSONApiResponseKeysEnum.DEVICE_GET_SENSOR_VALUE_SENSOR_VALUE.getKey()).toString());
					}
					
					return value;
				}
			}
			
		}
		return -1;
	}

	@Override
	public boolean callDeviceScene(String token, DSID dsid, String name, Scene sceneNumber,
			boolean force) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && sceneNumber != null) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
					if (force) {
						response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
							JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
						}
					else {
						response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
								JSONRequestConstants.INFIX_PARAMETER_NAME+name+
								JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
					}
				}
				else {
					if (force) {
						response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
							JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
					}
					else {
						response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
					}
				}
						
			}
			else if (name != null) {
				if (force) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber()+
							JSONRequestConstants.INFIX_PARAMETER_FORCE_TRUE);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_CALLSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_NAME+name+
						JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
				}
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean undoDeviceScene(String token, DSID dsid, Scene sceneNumber) {
		if (((dsid != null && dsid.getValue() != null)) && sceneNumber != null) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				response = transport.execute(JSONRequestConstants.JSON_DEVICE_UNDOSCENE+JSONRequestConstants.PARAMETER_TOKEN+token+
					JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
					JSONRequestConstants.INFIX_PARAMETER_SCENE_NUMBER+sceneNumber.getSceneNumber());
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public boolean subscribeEvent(String token, String name, int subscriptionID, int connectionTimeout, int readTimeout) {
		if (name != null && !name.trim().equals("") && withParameterSubscriptionID(subscriptionID)) {
			String response = null;
			
			response = transport.execute(JSONRequestConstants.JSON_EVENT_SUBSCRIBE+JSONRequestConstants.PARAMETER_TOKEN+token+
					JSONRequestConstants.INFIX_PARAMETER_NAME+name+
					JSONRequestConstants.INFIX_PARAMETER_SUBSCRIPTION_ID+subscriptionID,
					connectionTimeout, readTimeout);
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public boolean unsubscribeEvent(String token, String name, int subscriptionID, int connectionTimeout, int readTimeout) {
		if (name != null && !name.trim().equals("") && withParameterSubscriptionID(subscriptionID)) {
			String response = null;
			
			response = transport.execute(JSONRequestConstants.JSON_EVENT_UNSUBSCRIBE+JSONRequestConstants.PARAMETER_TOKEN+token+
					JSONRequestConstants.INFIX_PARAMETER_NAME+name+
					JSONRequestConstants.INFIX_PARAMETER_SUBSCRIPTION_ID+subscriptionID,
					connectionTimeout, readTimeout);
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	private boolean withParameterSubscriptionID(int subscriptionID) {
		return (subscriptionID > -1);
	}

	@Override
	public String getEvent(String token, int subscriptionID, int timeout) {
		if (withParameterSubscriptionID(subscriptionID) && withParameterTimeout(timeout)) {
			return transport.execute(JSONRequestConstants.JSON_EVENT_GET+JSONRequestConstants.PARAMETER_TOKEN+token+
					JSONRequestConstants.INFIX_PARAMETER_SUBSCRIPTION_ID+subscriptionID+
					JSONRequestConstants.INFIX_PARAMETER_TIMEOUT+timeout);
		}
		return null;
	}
	
	private boolean withParameterTimeout(int timeout) {
		return (timeout > -1);
	}

	@Override
	public int getTime(String token) {
		String response = null;
		
		response = transport.execute(JSONRequestConstants.JSON_SYSTEM_TIME+JSONRequestConstants.PARAMETER_TOKEN+token);
		
		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			if (obj != null && obj.get(JSONApiResponseKeysEnum.SYSTEM_GET_TIME.getKey()) != null) {
				int time = -1;
				try {
					time = Integer.parseInt(obj.get(JSONApiResponseKeysEnum.SYSTEM_GET_TIME.getKey()).toString());
				}catch (java.lang.NumberFormatException e) {
					//logger.error
					System.err.println("NumberFormatException by getTime: "+obj.get(JSONApiResponseKeysEnum.SYSTEM_GET_TIME.getKey()).toString());
				}
				return time;
			}
		}
			
		return -1;
	}


	private boolean valueInRange(int value) {
		return (value > -1 && value < 256);
	}
	
	@Override
	public List<Integer> getResolutions(String token) {
		String response = null;
		
		response = transport.execute(JSONRequestConstants.JSON_METERING_GET_RESOLUTIONS+JSONRequestConstants.PARAMETER_TOKEN+token);
		
		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject resObj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			if (resObj != null && resObj.get(JSONApiResponseKeysEnum.METERING_GET_RESOLUTIONS.getKey()) instanceof JSONArray) {
				JSONArray array = (JSONArray) resObj.get(JSONApiResponseKeysEnum.METERING_GET_RESOLUTIONS.getKey());
				
				List<Integer> resolutionList = new LinkedList<Integer>();
				
				for (int i=0; i< array.size(); i++) {
					if (array.get(i) instanceof org.json.simple.JSONObject) {
						JSONObject jObject = (JSONObject)array.get(i);
					
						if (jObject.get(JSONApiResponseKeysEnum.METERING_GET_RESOLUTION.getKey()) != null) {
							int val = -1;
							try {
								val = Integer.parseInt(jObject.get(JSONApiResponseKeysEnum.METERING_GET_RESOLUTION.getKey()).toString());
							}
							catch (java.lang.NumberFormatException e) {
								//logger.error
								System.err.println("NumberFormatException in getResolutions: "+jObject.get(JSONApiResponseKeysEnum.METERING_GET_RESOLUTION.getKey()).toString());
							}
							if (val != -1) {
								resolutionList.add(val);
							}
						}
					}
				}
				return resolutionList;
			}
		}
		
		return null;
	}

	@Override
	public List<CachedMeteringValue> getLatest(String token, MeteringTypeEnum type, String from,
			MeteringUnitsEnum unit) {
		if (type != null && from != null) {
			String response = null;
			
			if (unit != null && type != MeteringTypeEnum.consumption) {
				response = transport.execute(JSONRequestConstants.JSON_METERING_GET_LATEST+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_TYPE+type.name()+
						JSONRequestConstants.INFIX_PARAMETER_FROM+from+
						JSONRequestConstants.INFIX_PARAMETER_UNIT+unit.name());
			}
			else {
				response = transport.execute(JSONRequestConstants.JSON_METERING_GET_LATEST+JSONRequestConstants.PARAMETER_TOKEN+token+
						JSONRequestConstants.INFIX_PARAMETER_TYPE+type.name()+
						JSONRequestConstants.INFIX_PARAMETER_FROM+from);
			}
			
			
			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			System.out.println(response);
			System.out.println(JSONResponseHandler.checkResponse(responseObj));
			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject latestObj = JSONResponseHandler.getResultJSONObject(responseObj);
				
				if (latestObj != null && latestObj.get(JSONApiResponseKeysEnum.METERING_GET_LATEST.getKey()) instanceof JSONArray) {
					JSONArray array = (JSONArray) latestObj.get(JSONApiResponseKeysEnum.METERING_GET_LATEST.getKey());
					
					List<CachedMeteringValue> list = new LinkedList<CachedMeteringValue>();
					
					for (int i=0; i< array.size(); i++) {
						if (array.get(i) instanceof JSONObject) {
							list.add(new JSONCachedMeteringValueImpl((JSONObject)array.get(i)));
						}
						
					}
					return list;
				}
			}
			
		}
		return null;
	}
	
	@Override
	public boolean setDeviceValue(String token, DSID dsid, String name, int value) {
		if (((dsid != null && dsid.getValue() != null) || name != null) && valueInRange(value)) {
			String response = null;
			
			if (dsid != null && dsid.getValue() != null) {
				if (name != null) {
						response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
								JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
								JSONRequestConstants.INFIX_PARAMETER_NAME+name+
								JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
				}
				else {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_DSID+dsid.getValue()+
							JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
				}
						
			}
			else if (name != null) {
					response = transport.execute(JSONRequestConstants.JSON_DEVICE_SET_VALUE+JSONRequestConstants.PARAMETER_TOKEN+token+
							JSONRequestConstants.INFIX_PARAMETER_NAME+name+
							JSONRequestConstants.INFIX_PARAMETER_VALUE+value);
			}
			
			if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
				return true;
			}
			
		}
		return false;
	}

	@Override
	public List<String> getMeterList(String token) {
		List<String> meterList = new LinkedList<String>();
			
		String response = transport.execute(JSONRequestConstants.JSON_PROPERTY_QUERY+JSONRequestConstants.PARAMETER_TOKEN+token+
				JSONRequestConstants.INFIX_PARAMETER_QUERY+JSONRequestConstants.QUERY_GET_METERLIST);
			
		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
			
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
				
			if (obj != null && obj.get(JSONApiResponseKeysEnum.DS_METER_QUERY.getKey()) instanceof JSONArray) {
				JSONArray array = (JSONArray) obj.get(JSONApiResponseKeysEnum.DS_METER_QUERY.getKey());
					
				for (int i=0; i< array.size(); i++) {
					if (array.get(i) instanceof JSONObject) {
						JSONObject elem = (JSONObject) array.get(i);
						
						@SuppressWarnings("unchecked")
						Collection<String> k = elem.values();
							
						for (String s: k) {
							meterList.add(s);
						}
					}
				}
			}
		}
		return meterList;
	}

	@Override
	public String loginApplication(String loginToken) {
		if (loginToken != null && !loginToken.trim().equals("")) {
			String response = null;

			response = transport
					.execute(JSONRequestConstants.JSON_SYSTEM_LOGIN_APPLICATION
							+ loginToken);

			JSONObject responseObj = JSONResponseHandler.toJSONObject(response);

			if (JSONResponseHandler.checkResponse(responseObj)) {
				JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
				
				String tokenStr = null;
				
				if (obj != null && obj.get(JSONApiResponseKeysEnum.SYSTEM_LOGIN
						.getKey()) != null) {
					tokenStr = obj.get(JSONApiResponseKeysEnum.SYSTEM_LOGIN
							.getKey()).toString();
					
				}
				
				if (tokenStr != null) {
					return tokenStr;
				}
			}
		}
		return null;
	}

	@Override
	public String login(String user, String password) {
		String response = null;

		response = transport
				.execute(JSONRequestConstants.JSON_SYSTEM_LOGIN
						+ JSONRequestConstants.PARAMETER_USER + user
						+ JSONRequestConstants.INFIX_PARAMETER_PASSWORD
						+ password);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			String tokenStr = null;
			
			if (obj != null && obj.get(JSONApiResponseKeysEnum.SYSTEM_LOGIN
							.getKey()) != null) {
				tokenStr = obj.get(JSONApiResponseKeysEnum.SYSTEM_LOGIN
						.getKey()).toString();
			}
			
			if (tokenStr != null) {
				return tokenStr;
			}
		}
		return null;
	}

	@Override
	public boolean logout() {
		String response = transport
				.execute(JSONRequestConstants.JSON_SYSTEM_LOGOUT);

		if (JSONResponseHandler.checkResponse(JSONResponseHandler.toJSONObject(response))) {
			return true;
		}
		return false;
	}

	@Override
	public String getDSID(String token){
		 String response = transport
				.execute(JSONRequestConstants.JSON_SYSTEM_GET_DSID+token);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			if(obj != null){
				String dsID = obj.get(JSONApiResponseKeysEnum.SYSTEM_DSID
						.getKey()).toString();
			
				if (dsID != null) return dsID;
			}
		}
		return null;
	}
	
	@Override
	public boolean enableApplicationToken(String applicationToken, String sessionToken){
		String response = null;

		response = transport
				.execute("/json/system/enableToken?applicationToken="+applicationToken+"&token="+sessionToken);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		return JSONResponseHandler.checkResponse(responseObj);
	}

	@Override
	public String requestAppplicationToken(String applicationName) {
		String response = transport
				.execute("/json/system/requestApplicationToken?applicationName="+applicationName);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			if(obj != null){
				String aplicationToken = obj.get(JSONApiResponseKeysEnum.SYSTEM_APPLICATION_TOKEN
						.getKey()).toString();
			
				if (aplicationToken != null) return aplicationToken;
			}
		}
		return null;
	}

	@Override
	public boolean revokeToken(String applicationToken) {
		String response = null;

		response = transport
				.execute("/json/system/revokeToken?applicationToken="+applicationToken);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		return JSONResponseHandler.checkResponse(responseObj);
		
	}
	
	@Override
	public int checkConnection(String token){
		return transport.checkConnection("/json/apartment/getName?token="+token);
	}

	@Override
	public int getSceneValue(String token, DSID dsid, short sceneId) {
		String response = null;

		response = transport
				.execute("json/device/getSceneValue?dsid=" + dsid.toString() + "&sceneID=" + sceneId + "&token" + token);

		JSONObject responseObj = JSONResponseHandler.toJSONObject(response);
		
		if (JSONResponseHandler.checkResponse(responseObj)) {
			JSONObject obj = JSONResponseHandler.getResultJSONObject(responseObj);
			
			String valueString = null;
			
			if (obj != null && obj.get("value") != null) {
				valueString = obj.get("value").toString();
			}
			
			if (valueString != null) {
				return Integer.parseInt(valueString);
			}
		}
		return -1;
	}

	@Override
	public boolean increaseValue(String sessionToken, DSID dsid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreaseValue(String sessionToken, DSID dsid) {
		// TODO Auto-generated method stub
		return false;
	}
}