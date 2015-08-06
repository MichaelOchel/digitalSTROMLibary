package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import DSServerConnection.DigitalSTROMAPI;
import DSServerConnection.impl.DigitalSTROMJSONImpl;
import DigitalSTROMDevices.Apartment;
import DigitalSTROMDevices.DetailedGroupInfo;
import DigitalSTROMDevices.Device;
import DigitalSTROMDevices.DigitalSTROMStructureManager;
import DigitalSTROMDevices.Zone;

public class DigitalSTROMStructureManagerTest {

	private DigitalSTROMAPI dssAPI;
	private final String uri = "https://testrack2.aizo.com:58080";
	private final String user_pw = "dssadmin";
	private String sessionToken;
	
	private List<Device> deviceList;
	private Apartment apartment;
	private DigitalSTROMStructureManager strucMan; 
	
	@Before
	public void setUp() throws Exception {
		dssAPI = new DigitalSTROMJSONImpl(uri, 1000,1000);
		sessionToken = dssAPI.login(user_pw, user_pw);
		deviceList = dssAPI.getApartmentDevices(sessionToken, false);
		//System.out.println(deviceList);
		apartment = dssAPI.getApartmentStructure(sessionToken);
		strucMan = new DigitalSTROMStructureManager(deviceList);
	}
	
	@Test
	public void StructureCorrectGenerated() {
		
		Map<Integer, Zone> zoneMap = apartment.getZoneMap();
		
		for(Device device: zoneMap.get(0).getDevices()){
			assertTrue(strucMan.getDeviceByDSID(device.getDSID()).equals(device));
			assertTrue(strucMan.getDeviceByDSUID(device.getDSUID()).equals(device));
		}
		for(Zone zone: zoneMap.values()){
			if(zone != null && !zone.getDevices().isEmpty()){
				if(strucMan.getGroupsFromZoneX(zone.getZoneId()) == null){
					fail("Zone " + zone.getZoneId() + " is not in the StructureManager");
				}
				
				if(!zone.getDevices().containsAll(strucMan.getReferenceDeviceListFromZoneXGroupX(zone.getZoneId(), (short) 0))){
					//System.out.println("check equals \n" + zone.getDevices().toString() + "\nequals\n" + strucMan.getDeviceListFromZoneXGroupX(zone.getZoneId(), (short) 0).toString());
					fail("Device in Zone " + zone.getZoneId() + " are not the equals as the Devices of the StructureManager");
				}
				
				for(DetailedGroupInfo groupInf : zone.getGroups()){
					System.out.println("check existing group " + groupInf.getGroupID() + " in Zone " + zone.getZoneId() + " = " + strucMan.getReferenceDeviceListFromZoneXGroupX(zone.getZoneId(), groupInf.getGroupID()));
					if( strucMan.getReferenceDeviceListFromZoneXGroupX(zone.getZoneId(), groupInf.getGroupID()) != null){
						Iterator<Device> interator = strucMan.getReferenceDeviceListFromZoneXGroupX(zone.getZoneId(), groupInf.getGroupID()).iterator();
						List<String> tmpDSUIDList = groupInf.getDeviceList(); 
						while(interator.hasNext()){
							String dsuid = interator.next().getDSUID();
							//System.out.println("check Device with dSUID " + dsuid + "is in Group " + groupInf.getGroupID() + " from Zone " + zone.getZoneId());
							assertTrue(tmpDSUIDList.remove(dsuid));
						}
					} else{
						if(!groupInf.getDeviceList().isEmpty()){
							fail("Group " + groupInf.getGroupID() + " not exist in Zone " + zone.getZoneId());
						}
						
					}
				}
			}
		}
	}
	
	@Test
	public void getDeviceByDSID(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(3);
		assertTrue(strucMan.getDeviceByDSID(device.getDSID()).equals(device));
	}
	
	@Test
	public void getDeviceByDSUID(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(2);
		assertTrue(strucMan.getDeviceByDSUID(device.getDSUID()).equals(device));		
	}
	
	@Test
	public void UpdateDevice_AddGroup(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(3);
		//int oldZoneId = -1;
		//List<Short> oldGroups = device.getGroups();
		device.addGroup((short) 20);
		strucMan.updateDevice(device);
		for(Short groupID:device.getGroups()){
			assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			if(groupID <= 16){
				assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}		
	}
	
	@Test
	public void UpdateDevice_changeZone(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(3);
		int oldZoneId = device.getZoneId();
		List<Short> oldGroups = device.getGroups();
		device.setZoneId(2);
		strucMan.updateDevice(device);
		for(Short groupID:device.getGroups()){
			assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			if(groupID <= 16){
				assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}	
		for(Short groupID:oldGroups){
			assertFalse(strucMan.getReferenceDeviceListFromZoneXGroupX(oldZoneId, groupID).contains(device));
		}	
	}

	@Test
	public void UpdateDevice_RemoveGroup(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(3);
		int oldZoneId = device.getZoneId();
		List<Short> oldGroups = device.getGroups();
		List<Short> newGroups = device.getGroups();
		newGroups.remove(0);
		System.out.println("Check remove Group: " +newGroups.toString() + " " + oldGroups.toString());
		device.setGroups(newGroups);
		strucMan.updateDevice(device);
		for(Short groupID:device.getGroups()){
			System.out.println("Check remove Group is in group " + groupID + " and zoneID " + device.getZoneId() +
					" = " + strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			if(groupID <= 16){
				System.out.println("Check remove Group is in group " + groupID + " and zoneID " + 0 +
						" = " + strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
				assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}	
		for(Short groupID:oldGroups){
			System.out.println("Check remove Group is not longer in group " + groupID + " and zoneID " + device.getZoneId() +
					" = " + !strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			assertFalse(strucMan.getReferenceDeviceListFromZoneXGroupX(oldZoneId, groupID).contains(device));
			if(groupID <= 16){
				System.out.println("Check remove Group is not longer in group " + groupID + " and zoneID " + 0 +
						" = " + !strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
				assertFalse(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}		
	}
	
	@Test
	public void DeleteAndAddDevice(){
		Device device = apartment.getZoneMap().get(1).getDevices().get(3);
		
		strucMan.deleteDevice(device);
		assertTrue(strucMan.getDeviceByDSID(device.getDSID()) == null);
		assertTrue(strucMan.getDeviceByDSUID(device.getDSUID()) == null);
		for(Short groupID:device.getGroups()){
			assertFalse(strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			if(groupID <= 16){
				assertFalse(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}
		
		strucMan.addDeviceToStructure(device);
		assertTrue(strucMan.getDeviceByDSID(device.getDSID()) != null);
		assertTrue(strucMan.getDeviceByDSUID(device.getDSUID()) != null);
		for(Short groupID:device.getGroups()){
			assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(device.getZoneId(), groupID).contains(device));
			if(groupID <= 16){
				assertTrue(strucMan.getReferenceDeviceListFromZoneXGroupX(0, groupID).contains(device));
			}
		}
	}
	
	
}
