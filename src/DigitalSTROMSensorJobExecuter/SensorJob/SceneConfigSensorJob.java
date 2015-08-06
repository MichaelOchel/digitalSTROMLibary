/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package DigitalSTROMSensorJobExecuter.SensorJob;

import DSServerConnection.DigitalSTROMAPI;
import DigitalSTROMDevices.Device;
import DigitalSTROMDevices.DeviceParameters.DSID;
import DigitalSTROMDevices.DeviceParameters.DeviceSceneSpec;



/**
 * The {@link SceneConfigSensorJob} is the implementation of a {@link SensorJob} 
 * for reading out a device scene output value of a digitalSTROM-Device.
 * It also store the scene output value of the scene into the {@link Device} and and persistent to ESH. 
 * 
 * @author Alexander Betker
 * @author Alex Maier
 * @author Michael Ochel - updated and added some methods
 * @author Matthias Siegele - updated and added some methods
 */
public class SceneConfigSensorJob implements SensorJob {

	//private static final Logger logger = LoggerFactory
		//	.getLogger(SceneOutputValueSensorJob.class);
	
	private Device device = null;
	private short sceneId = 0;
	//private DssBridgeHandler dssBridgeHandler;
	private DSID meterDSID = null;
	private long initalisationTime = 0;

	/**
	 * Creates a new {@link SceneConfigSensorJob} with the given scene id.
	 * 
	 * @param device
	 * @param sceneId
	 * @param dssBridgeHandler
	 */
	public SceneConfigSensorJob(Device device, short sceneId) { //, DssBridgeHandler dssBridgeHandler
		this.device = device;
		this.sceneId = sceneId;
		//this.dssBridgeHandler = dssBridgeHandler;
		this.meterDSID = device.getMeterDSID();
		this.initalisationTime = System.currentTimeMillis();
	}
	
	@Override
	public void execute(DigitalSTROMAPI digitalSTROM, String token) {
		DeviceSceneSpec sceneConfig = digitalSTROM.getDeviceSceneMode(token, device.getDSID(), null, sceneId);
		
		if (sceneConfig != null) {
			device.addSceneConfig(sceneId, sceneConfig);
			//logger.info
			System.out.println("UPDATED scene configuration for dsid: "+this.device.getDSID()+", sceneID: "+sceneId+", configuration: " + sceneConfig.toString());
		
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SceneConfigSensorJob) {
			SceneConfigSensorJob other = (SceneConfigSensorJob) obj;
			String str = other.device.getDSID().getValue()+"-"+other.sceneId;
			return (this.device.getDSID().getValue()+"-"+this.sceneId).equals(str);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return new String(this.device.getDSID().getValue()+this.sceneId).hashCode();
	}

	@Override
	public DSID getDsid() {
		return device.getDSID();
	}	

	@Override
	public DSID getMeterDSID() {
		return this.meterDSID;
	}
	
	@Override
	public long getInitalisationTime() {
		return this.initalisationTime;
	}
	
	@Override
	public void setInitalisationTime(long time) {
		this.initalisationTime = time;
	}	

	
}
