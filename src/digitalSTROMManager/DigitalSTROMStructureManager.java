package digitalSTROMManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;

/**
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DigitalSTROMStructureManager {

	/**
	 * 
	 * @param connectionManager
	 * @return
	 */
	public boolean generateZoneGroupNames(DigitalstromConnectionManager connectionManager);
	
	/**
	 * 
	 * @param zoneID
	 * @return
	 */
	public String getZoneName(int zoneID);
	
	/**
	 * 
	 * @param zoneID
	 * @param groupID
	 * @return
	 */
	public String getGroupZoneName(int zoneID, short groupID);
	
	/**
	 * 
	 * @return
	 */
	public Map<DSID, Device> getDeviceMap();
	
	/**
	 * 
	 * @return
	 */
	public Map<DSID, Device> getDeviceHashMapReference();
	
	/**
	 * 
	 * @return
	 */
	public Map<Integer, HashMap<Short, List<Device>>> getStructureReference();
	
	/**
	 * 
	 * @param zoneID
	 * @return
	 */
	public HashMap<Short, List<Device>> getGroupsFromZoneX(int zoneID);
	
	/**
	 * 
	 * @param zoneID
	 * @param groupID
	 * @return
	 */
	public List<Device> getReferenceDeviceListFromZoneXGroupX(int zoneID, short groupID);
	
	/**
	 * 
	 * @param dSID
	 * @return
	 */
	public Device getDeviceByDSID(String dSID);
	
	/**
	 * 
	 * @param dSID
	 * @return
	 */
	public Device getDeviceByDSID(DSID dSID);
	
	/**
	 * 
	 * @param dSUID
	 * @return
	 */
	public Device getDeviceByDSUID(String dSUID);
	
	/**
	 * 
	 * @param oldZone
	 * @param oldGroups
	 * @param device
	 */
	public void updateDevice(int oldZone, List<Short> oldGroups, Device device);
	
	/**
	 * 
	 * @param device
	 */
	public void updateDevice(Device device);
	
	/**
	 * 
	 * @param device
	 */
	public void deleteDevice(Device device);
	
	/**
	 * 
	 * @param device
	 */
	public void addDeviceToStructure(Device device);
	
	/**
	 * 
	 * @return
	 */
	public Set<Integer> getZoneIDs();
	
}
