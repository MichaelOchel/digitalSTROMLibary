/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package digitalSTROMSensorJobExecuter.sensorJob.impl;

import digitalSTROMSensorJobExecuter.sensorJob.SensorJob;
import digitalSTROMServerConnection.DigitalSTROMAPI;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceConstants;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceStateUpdate;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceStateUpdateImpl;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.OutputModeEnum;




/**
 * The {@link DeviceOutputValueSensorJob} is the implementation of a {@link SensorJob} 
 * for reading out a device output value of a digitalSTROM-Device.
 * 
 * @author Alexander Betker
 * @author Alex Maier
 * @author Michael Ochel - updated and added some methods
 * @author Matthias Siegele - updated and added some methods
 */
public class DeviceOutputValueSensorJob implements SensorJob {

	//private static final Logger logger = LoggerFactory
		//	.getLogger(DeviceOutputValueSensorJob.class);
	private Device device = null;
	private short index = 0;
	private DSID meterDSID = null;
	private long initalisationTime = 0;
	
	/**
	 * Creates a new {@link DeviceOutputValueSensorJob} with the given sensor index.
	 * 
	 * @param device
	 * @param index	sensor index
	 */
	public DeviceOutputValueSensorJob(Device device) {
		this.device = device;
		if(device.getOutputMode() == OutputModeEnum.POSITION_CON_US){
			this.index = DeviceConstants.DEVICE_SENSOR_SLAT_OUTPUT;
		} else {
			this.index = DeviceConstants.DEVICE_SENSOR_OUTPUT;
		}
		this.meterDSID = device.getMeterDSID();
		this.initalisationTime = System.currentTimeMillis();
	}
	
	@Override
	public void execute(DigitalSTROMAPI digitalSTROM, String token) {
		int value = digitalSTROM.getDeviceOutputValue(token, this.device.getDSID(), null, this.index);
		//logger.info
		System.out.println("DeviceOutputValue on Demand : "+value+", DSID: "+this.device.getDSID().getValue());
	
		if (value != 1) {
			switch (this.index) {
			case 0:
				this.device.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS, value));
				break;
			case 4:
				this.device.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, value));
				break;
		
			default: 
				break;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceOutputValueSensorJob) {
			DeviceOutputValueSensorJob other = (DeviceOutputValueSensorJob) obj;
			String key = this.device.getDSID().getValue()+this.index;
			return key.equals((other.device.getDSID().getValue()+other.index));
		}
		return false;
	}

	@Override
	public int hashCode(){
		return new String(this.device.getDSID().getValue()+this.index).hashCode();
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
