package Scenes.Scene;

import java.util.List;

import DSServerConnection.DigitalSTROMAPI;
import DSServerConnection.DigitalstromConnectionManager;
import DSServerConnection.impl.DigitalSTROMConnectionManagerImpl;
import DigitalSTROMDevices.Device;
import DigitalSTROMDevices.DigitalSTROMStructureManager;

public class SceneTest {

	public static String hostName = "https://testrack2.aizo.com:58080";
	public static String user = "dssadmin";
	public static String password = "dssadmin";
	
	public static class DeviceList{
		
		long last = System.currentTimeMillis();
		
		public void tuwas(){
			for(int i =0 ; i< 5 ;i++){
				System.out.println("ich habe "+(System.currentTimeMillis()-last)/1000 + "s gewartet tue das " + (i+1) + ". mal etwas");
				last = System.currentTimeMillis();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void test(){
			System.out.println("ich kann auch was wärend ich warte");
		}
	}
	
	public static void main(String[] args) {
		
		DigitalstromConnectionManager connMan = new DigitalSTROMConnectionManagerImpl(hostName, user, password, false);
		
		DigitalSTROMAPI api = connMan.getDigitalSTROMAPI();
		if(connMan.checkConnection()){
			List<Device> devices = api.getApartmentDevices(connMan.getSessionToken(), false);
			if(devices != null){
				DigitalSTROMStructureManager structMan = new DigitalSTROMStructureManager();
				structMan.generateZoneGroupNames(connMan);
				//System.out.println(structMan.getZoneName(0));
				//System.out.println(structMan.getGroupZoneName(0, (short) 1));
				//DigitalSTROMDeviceStatusManager devMan = new DigitalSTROMDeviceStatusManager(connMan);
				/*try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(structMan.getReferenceDeviceListFromZoneXGroupX((Integer) 1281, (short) 0));
				System.out.println(structMan.getZoneIDs().toString());*/
				SceneDiscovery discovery = new SceneDiscovery(true);
				//discovery.generateAppartmentScence();
				//discovery.generateNamedScenes();
				//discovery.generateZoneScenes(connMan);
				discovery.generateReachableScenes(connMan, structMan);
				System.out.println(discovery.getNamedSceneList().size());
				System.out.println(discovery.toString());
				/*for(InternalScene scene: discovery.getNamedSceneList()){
					scene.toString();
				}*/
			}
		}
		
		
		/*DeviceList test = new DeviceList();
		test.tuwas();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.test();
		*/
		
		
		/*final List<Device> devices = new LinkedList<Device>();
		
		final Device device1 = new Device("1");
		final Device device2 = new Device("2");
		devices.add(device1);
		devices.add(device2);
		
		Thread checker = new Thread(){
			boolean shutdown = false;
			
			
			public void run() {
				while(!shutdown) {
					System.out.println("tut was");
					try {
						System.out.println("wait for devices changes");
						//for(Device device: devices){
							synchronized(devices){
								devices.wait();
							}
						//}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		 checker.start();
		 Thread ausloeser = new Thread(){
				boolean shutdown = false;
				
				
				public void run() {
					
						try {
							sleep(10000);
							System.out.println(devices);
							System.out.println("change device output");
							 device2.setOutputValue(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
			};
		 ausloeser.start();
		 
		 */
		/*List<Device> devices1 = new LinkedList<Device>();
		List<Device> devices2 = new LinkedList<Device>();
 		
		for(int i = 0; i < 6; i++){
			devices1.add(new Device(i));
			devices2.add(new Device(i));
		}
		
		System.out.println(devices1.equals(devices2));
		
		NamedScene scene = new NamedScene((Integer) 0, (short) 0, (short) 1,null);
		
		scene.addReferenceDevices(devices1);
		
		//scene.addDevices(devices1);
		
		System.out.println(devices1.equals(scene.getDeviceList()));
		devices1.clear();
		devices1.add(new Device(7));
		
		//System.out.println(devices1.equals(devices2));
		
		System.out.println(devices1.toString() + "\n" + scene.getDeviceList().toString() + "\n = " +  devices1.equals(scene.getDeviceList()));
		*/
		
		
	/*	Device device = new Device(1);
		NamedScene scene = new NamedScene((Integer) 0, (short) 0, (short) 1,null);
		SceneHandler handler = new SceneHandler();
		
		scene.addDevice(device);
		scene.registerSceneListener(handler);
		
		device.setSceneOutput((short) 1, 25);
		device.setSceneOutput((short) 2, 50);
		device.setSceneOutput((short) 3, 75);
		device.setSceneOutput((short) 4, 100);
		
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nAktiviere Scene " + scene.getSceneName());
		scene.activateScene();
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nSetze device output von Device " + device.getID()+ "auf 100");
		device.setOutputValue(100);
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nAktiviere Scene " + scene.getSceneName());
		scene.activateScene();
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nDeaktiviere Scene " + scene.getSceneName());
		scene.deactivateScene();
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nAktiviere Scene " + scene.getSceneName());
		scene.activateScene();
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nRufe auf Device " + device.getID() + " die selbe scene wie aus "+ scene.getSceneName() + " auf");
		device.callScene(scene.getSceneID());
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nRufe auf Device " + device.getID() + " scene 5 auf");
		device.callScene((short) 5);
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nAktiviere Scene " + scene.getSceneName());
		scene.activateScene();
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		System.out.println("\nRufe auf Device " + device.getID() + " scene 4 auf");
		device.callScene((short) 4);
		
		System.out.println("\nneuer Output ist:");
		System.out.println("Scene: "+ scene.getSceneName() +": ist Aktive = " + scene.isActive() + ", " + "handler ist active = " + 
				handler.isActiv() + ", Device "+ device.getID() + ": aktiveScene = " + device.getAcitiveScne() + ", Deviceoutput = " + device.getOutputValue());
		
		*/
		
	/*	for(ApartmentSceneEnum apartmentScene : ApartmentSceneEnum.values()){
			System.out.println(apartmentScene.toString().toLowerCase().replace("_", " "));
		}*/
		
		//System.out.println(new NamedScene(null,null,null,null));
		/*Discovery sceneDiscovery = new Discovery(hostName);
		
		System.out.println(sceneDiscovery.generateNamedScenes());
		
		List<NamedScene> namedScenesList =  sceneDiscovery.getNamedSceneList();
		
		for(NamedScene scene : namedScenesList){
			System.out.println(scene.toString() + "with Devices: " + scene.getDeviceList().toString());
		}
		
		*/
	}

}
