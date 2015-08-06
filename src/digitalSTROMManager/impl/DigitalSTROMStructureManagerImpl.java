package digitalSTROMManager.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import digitalSTROMManager.DigitalSTROMStructureManager;
import digitalSTROMManager.DigitalstromConnectionManager;
import digitalSTROMServerConnection.impl.JSONResponseHandler;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;

public class DigitalSTROMStructureManagerImpl implements DigitalSTROMStructureManager {

	private Map<Integer, HashMap<Short, List<Device>>> zoneGroupDeviceMap;
	private Map<DSID, Device> deviceMap;
	private Map<String, DSID> dSUIDToDSIDMap;
	
	private Map<Integer, Object[]> zoneGroupNameMap = null;
	
	
	public DigitalSTROMStructureManagerImpl(List<Device> referenceDeviceList){
		//zoneGroupDeviceMap = Collections.synchronizedMap(new HashMap<Integer, HashMap<Short, List<Device>>>());
		handleStructure(referenceDeviceList);
	}
	public DigitalSTROMStructureManagerImpl() {
		
	}
	
	/**
	 * Build the Device {@link HashMap} with the dSID of the digitalSTROM device as key, 
	 * this method also build a {@link HashMap} with the dSUID as key and the dSID as value 
	 * to get a Device by the dSUID.  
	 * 
	 * @param deviceList
	 */
	/*private void putDevicesToHashMap(List<Device> deviceList){
		if(!deviceList.isEmpty()){
			for(Device device:deviceList){
				putDeviceToHashMap(device);
			}
		}
	};*/
	
	private final String ZONE_GROUP_NAMES = "/json/property/query?query=/apartment/zones/*(ZoneID,name)/groups/*(group,name)";  
	
	@Override
	public boolean generateZoneGroupNames(DigitalstromConnectionManager connectionManager){
		if(connectionManager.checkConnection()){
			String response = connectionManager.getHttpTransport().execute(this.ZONE_GROUP_NAMES +
						"&token="+connectionManager.getSessionToken());
				if(response == null) {					
					return false;
				} else {
					JSONObject responsJsonObj = JSONResponseHandler.toJSONObject(response);
					if(JSONResponseHandler.checkResponse(responsJsonObj)){
						JSONObject resultJsonObj = JSONResponseHandler.getResultJSONObject(responsJsonObj);
						if (resultJsonObj.get("zones") instanceof JSONArray) {
							JSONArray zones = (JSONArray) resultJsonObj.get("zones");
							if(this.zoneGroupNameMap == null){
								this.zoneGroupNameMap = new HashMap<Integer, Object[]>(zones.size());
							}
							if(zones != null){	
								for(int i = 0 ; i < zones.size(); i++){
									if (((JSONObject) zones.get(i)).get("groups") instanceof JSONArray) {
										JSONArray groups = (JSONArray) ((JSONObject) zones.get(i)).get("groups");
										if(!groups.isEmpty()){
											Object[] zoneNameGroups = new Object[2];
											int zoneID = Integer.parseInt(((JSONObject) zones.get(i)).get("ZoneID").toString());
											zoneNameGroups[0] = ((JSONObject) zones.get(i)).get("name").toString();
											HashMap<Short, String> groupNames = new HashMap<Short, String>();
											for(int k = 0 ; k < groups.size(); k++){
												short groupID = Short.parseShort(((JSONObject) groups.get(k)).get("group").toString());
												String groupName = ((JSONObject) groups.get(k)).get("name").toString();
												groupNames.put(groupID, groupName);
											}
											zoneNameGroups[1] = groupNames;
											this.zoneGroupNameMap.put(zoneID, zoneNameGroups);
										}
										
									}
								}
							}
						}
					}
				}
		}
		return true;
	}
	
	@Override
	public String getZoneName(int zoneID){
		if(this.zoneGroupNameMap == null) return null;
		return this.zoneGroupNameMap.get(zoneID) != null ? (String) this.zoneGroupNameMap.get(zoneID)[0] : null;  
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getGroupZoneName(int zoneID, short groupID){
		if(this.zoneGroupNameMap == null) return null;
		if(this.zoneGroupNameMap.get(zoneID) != null){
			if(this.zoneGroupNameMap.get(zoneID)[1] != null){
				if(((HashMap<Short, String>) this.zoneGroupNameMap.get(zoneID)[1]).get(groupID) != null){
					return ((HashMap<Short, String>) this.zoneGroupNameMap.get(zoneID)[1]).get(groupID);
				}
			}
		}
		return null;  
	}
	
	@Override
	public Map<DSID, Device> getDeviceMap(){
		return this.deviceMap;
	}
	
	private void putDeviceToHashMap(Device device){
		if(deviceMap == null){
			deviceMap = Collections.synchronizedMap(new HashMap<DSID, Device>()); //deviceList.size()
		}
		if(dSUIDToDSIDMap == null){
			dSUIDToDSIDMap = Collections.synchronizedMap(new HashMap<String, DSID>()); //deviceList.size()
		}
		if(device.getDSID() != null){
				deviceMap.put(device.getDSID(), device);
				dSUIDToDSIDMap.put(device.getDSUID(), device.getDSID());
		}
	}
	
	/**
	 * This method build the digitalSTROM structure as an {@link HashMap} with the zone id as key
	 * and an {@link HashMap} as value. This {@link HashMap} has the group id as key and a {@link List} 
	 * with all digitalSTROM {@link Device}s. 
	 * 
	 * Note: the zone id 0 is the broadcast address and the group id 0 too.
	 *   
	 */
	private void handleStructure(List<Device> deviceList){
		HashMap<Short, List<Device>> groupXHashMap = new HashMap<Short, List<Device>>();
		groupXHashMap = new HashMap<Short, List<Device>>();
		groupXHashMap.put((short) 0, deviceList);
		
		if(this.zoneGroupDeviceMap == null){
			zoneGroupDeviceMap = Collections.synchronizedMap(new HashMap<Integer, HashMap<Short, List<Device>>>());
		}
		
		this.zoneGroupDeviceMap.put(0, groupXHashMap);
		
		for(Device device : deviceList){
			addDeviceToStructure(device);
		}
	}
	
	@Override
	public Map<DSID, Device> getDeviceHashMapReference(){
		return this.deviceMap;
	}
	
	@Override
	public Map<Integer, HashMap<Short, List<Device>>> getStructureReference(){
		return this.zoneGroupDeviceMap;
	}
	
	@Override
	public HashMap<Short, List<Device>> getGroupsFromZoneX(int zoneID){
		return this.zoneGroupDeviceMap == null? null: this.zoneGroupDeviceMap.get(zoneID);
	}
	
	@Override
	public List<Device> getReferenceDeviceListFromZoneXGroupX(int zoneID, short groupID){
		return getGroupsFromZoneX(zoneID) == null? null:this.zoneGroupDeviceMap.get(zoneID).get(groupID);
	}
	
	@Override
	public Device getDeviceByDSID(String dSID){
		return getDeviceByDSID(new DSID(dSID));
	}
	
	@Override
	public Device getDeviceByDSID(DSID dSID){
		return this.zoneGroupDeviceMap == null? null:this.deviceMap.get(dSID);
	}
	
	@Override
	public Device getDeviceByDSUID(String dSUID){
		if(this.deviceMap != null && this.dSUIDToDSIDMap != null){
			return this.dSUIDToDSIDMap.get(dSUID) == null? null: this.getDeviceByDSID(this.dSUIDToDSIDMap.get(dSUID)); 
		}
		return null;
	}
	
	@Override
	public void updateDevice(int oldZone, List<Short> oldGroups, Device device){
		if(oldZone == -1){
			oldZone = device.getZoneId();
		}
		deleteDevice(oldZone, oldGroups, device);
		addDeviceToStructure(device);
	}
	
	@Override
	public void updateDevice(Device device){
		if(device != null){
			int oldZoneID = -1;
			List<Short> oldGroups = null;
			Device internalDevice = this.getDeviceByDSID(device.getDSID());
			if(device.getZoneId() != internalDevice.getZoneId()){
				oldZoneID = internalDevice.getZoneId();
				internalDevice.setZoneId(device.getZoneId());
			}
			
			if(!internalDevice.getGroups().equals(device.getGroups())){
				oldGroups = internalDevice.getGroups();
				internalDevice.setGroups(device.getGroups());
			}
			
			if(deleteDevice(oldZoneID, oldGroups, internalDevice)){
				addDeviceToStructure(internalDevice);
			}
		}
	}
	
	@Override
	public void deleteDevice(Device device){
		dSUIDToDSIDMap.remove(device.getDSUID());
		deviceMap.remove(device.getDSID());
		deleteDevice(device.getZoneId(), device.getGroups(), device);
	}
	
	private boolean deleteDevice(int zoneID, List<Short> groups, Device device){
		if(groups != null || zoneID >= 0){
			if(groups == null) groups = device.getGroups();
			if(zoneID == -1) zoneID = device.getZoneId();
			for(Short groupID : groups){
				List<Device> deviceList = getReferenceDeviceListFromZoneXGroupX(zoneID, groupID);
				if(deviceList != null){
					deviceList.remove(device);
				}
				deviceList = getReferenceDeviceListFromZoneXGroupX(0, groupID);
				if(deviceList != null){
					deviceList.remove(device);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void addDeviceToStructure(Device device){
		putDeviceToHashMap(device);
		
		int zoneID = device.getZoneId();
		addDevicetoZoneXGroupX(zoneID, (short) 0, device);
		
		for(Short groupID : device.getGroups()){
			addDevicetoZoneXGroupX(zoneID, groupID, device);
			
			if(groupID <= 16){
				addDevicetoZoneXGroupX(0, groupID, device);
			}
		}
	}
	
	private void addDevicetoZoneXGroupX(int zoneID, short groupID, Device device){
		HashMap<Short, List<Device>> groupXHashMap = this.zoneGroupDeviceMap.get(zoneID);
		if(groupXHashMap == null){
			groupXHashMap = new HashMap<Short, List<Device>>();
			if(zoneGroupDeviceMap == null){
				zoneGroupDeviceMap = Collections.synchronizedMap(new HashMap<Integer, HashMap<Short, List<Device>>>());
			}
			this.zoneGroupDeviceMap.put(zoneID, groupXHashMap);
		}
		List<Device> groupDeviceList = groupXHashMap.get(groupID);
		if(groupDeviceList == null){
			groupDeviceList = new LinkedList<Device>();
			groupDeviceList.add(device);
			groupXHashMap.put(groupID, groupDeviceList);
		} else{
			if(!groupDeviceList.contains(device)){
				groupDeviceList.add(device);
			}
		}
	}
	
	@Override
	public Set<Integer> getZoneIDs(){
		//System.out.println(this.zoneGroupDeviceMap.size());
		return this.zoneGroupDeviceMap != null? this.zoneGroupDeviceMap.keySet():null;
	}
	
}
