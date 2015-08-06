package digitalSTROMStructure.digitalSTROMDevices;

/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


import java.util.List;

import digitalSTROMListener.DeviceStatusListener;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceSceneSpec;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceStateUpdate;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.FunctionalColorGroupEnum;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.OutputModeEnum;
import digitalSTROMStructure.digitalSTROMScene.InternalScene;




/**
 * The {@link Device} represents a DigitalSTROM internal stored device.
 * 
 * @author 	Alexander Betker - Initial contribution
 * @since 1.3.0
 * @author Michael Ochel - add methods for ESH, new functionalities and JavaDoc
 * @author Mathias Siegele - add methods for ESH, new functionalities and JavaDoc
 */
public interface Device {
	
	/**
	 * Returns the dSID of this device.
	 * @return {@link DSID} dSID 
	 */
	public DSID getDSID();
	
	/**
	 * Returns the dSUID of this device.
	 * @return dSID
	 */
	public String getDSUID();
	
	/**
	 * Returns the id of the DS-Meter in which the device is registered.
	 * 
	 * @return meterDSID
	 */
	public DSID getMeterDSID();
	
	/**
	 * Sets the id of the DS-Meter in which the device is registered.
	 * 
	 * @param meterDSID
	 */
	
	public void setMeterDSID(String meterDSID);
	/**
	 * Returns the hardware info of this device. 
	 * You can see all available hardware info here {@link http://www.digitalstrom.com/Partner/Support/Techn-Dokumentation/}
	 *   
	 * @return hardware info
	 */
	public String getHWinfo();
	
	/**
	 * Returns the user defined name of this device.
	 * 
	 * @return name of this device
	 */
	public String getName();
	
	/**
	 * Sets the name of this device;
	 * 
	 * @param new name for this device
	 */
	public void setName(String name);
	
	/**
	 * Returns the zone id in which this device is.
	 *  
	 * @return zone id
	 */
	public int getZoneId();

	/**
	 * Sets the zone id to the given zone id of this device is.
	 *  
	 * @parm zone id
	 */
	public void setZoneId(int zoneID);
	
	/**
	 * This device is available in his zone or not.
	 * Every 24h the dSM (meter) checks, if the devices are
	 * plugged in
	 * 
	 * @return	true, if device is available otherwise false
	 */
	public boolean isPresent();
	
	/**
	 * Set this device is available in his zone or not.
	 * 
	 * @param isPresent (true = available | false = not available)
	 */
	public void setIsPresent(boolean isPresent);
	
	/**
	 * Returns true if this device is on otherwise false.
	 * 
	 * @return is on (true = on | false = off)
	 */
	public boolean isOn();
	
	/**
	 * Set this device on if the flag is true or off if it is false.
	 * 
	 * @param flag (true = on | false = off)
	 */
	public void setIsOn(boolean flag);
	
	/**
	 * Returns true if this shade device is open otherwise false.
	 * 
	 * @return is on (true = open | false = closed)
	 */
	public boolean isOpen();
	
	/**
	 * Set this shade device open if the flag is true or closed if it is false.
	 * 
	 * @param flag (true = open | false = closed)
	 */
	public void setIsOpen(boolean flag);
	
	/**
	 * Return true if this device is dimmable, otherwise false. 
	 * 
	 * @return is dimmable (true = yes | false = no)
	 */
	public boolean isDimmable();
	
	/**
	 * Returns true if this device is a shade device (grey), otherwise false.
	 *  
	 * @return is shade (true = yes | false = no)
	 */
	public boolean isRollershutter();
	
	/**
	 * Returns true if the device output mode isn't disabled.
	 * 
	 * @return have output mode (true = yes | false = no)
	 */
	public boolean isDeviceWithOutput();
	
	/**
	 * Returns the current functional color group of this device. 
	 * For more informations please have a look at {@link FunctionalColorGroup}. 
	 * 
	 * @return current functional color group
	 */
	public FunctionalColorGroupEnum getFunctionalColorGroup();
	
	/**
	 * Sets the functional color group of this device.
	 * 
	 * @param fuctionalColorGroup
	 */
	public void setFunctionalColorGroup(FunctionalColorGroupEnum fuctionalColorGroup);
	
	/**
	 * Returns the current output mode of this device. 
	 * Some devices are able to have different output modes e.g. the device GE-KM200 is able to 
	 * be in dimm mode, switch mode or disabled.
	 * For more informations please have a look at {@link OutputModeEnum}.  
	 * 
	 * @return	the current output mode of this device
	 */
	public OutputModeEnum getOutputMode();
	
	/**
	 * Increase the output value of this device.
	 */
	public void increase();
	
	/**
	 * Decrease the output value of this device.
	 */
	public void decrease();
	
	/**
	 * Returns the current slat position of this device.
	 * 
	 * @return current slat position
	 */
	public int getSlatPosition();
	
	/**
	 * Sets the slat position of this device to the given slat position.
	 * 
	 * @return slat position
	 */
	public void setSlatPosition(int slatPosition);
	
	/**
	 * Returns the maximal slat position value of this device.
	 * 
	 * @return maximal slat position value
	 */
	public int getMaxSlatPosition();
	
	/**
	 * Returns the minimal slat position value of this device.
	 * 
	 * @return minimal slat position value
	 */
	public int getMinSlatPosition();
	
	/**
	 * Returns the current output value of this device. 
	 * This can be the slat position or the brightness of this device.
	 * 
	 * @return current output value
	 */	
	public int getOutputValue();
	
	/**
	 * Set the output value of this device to a given value.
	 * 
	 * @param outputValue
	 */
	public void setOutputValue(int outputValue);
	
	/**
	 * Returns the maximal output value of this device.
	 * 
	 * @return maximal output value
	 */
	public int getMaxOutputValue();
	
	/**
	 * Returns the last recorded power consumption in watt of this device.
	 * 
	 * @return current power consumption in watt
	 */
	public int getPowerConsumption();
	
	/**
	 * Set the current power consumption in watt to the given power consumption.
	 * 
	 * @param powerConsumption in watt
	 */
	public void setPowerConsumption(int powerConsumption);
	
	/**
	 * Returns the energy meter value in watt per hour of this device.
	 * 
	 * @return	energy meter value in watt per hour
	 */
	public int getEnergyMeterValue();
	
	/**
	 * Set the last recorded energy meter value in watt per hour of this device.
	 * 
	 * @param energy meter value in watt per hour
	 */
	public void setEnergyMeterValue(int value);
	
	/**
	 * Returns the last recorded electric meter value in ampere of this device.
	 * 
	 * @return	electric meter value in amoere 
	 */
	public int getElectricMeterValue();
	
	/**
	 * Sets the last recorded electric meter value in ampere of this device.
	 * 
	 * @param electric meter value in mA
	 */
	public void setElectricMeterValue(int electricMeterValue);
	
	/**
	 * Returns a list with group id's in which the device is part of.
	 * 
	 * @return List of group id's
	 */
	public List<Short> getGroups();
	
	
	public void addGroup(Short groupID);
	
	public void setGroups(List<Short> newGroupList);
	/**
	 * Returns the scene output value of this device of the given scene id 
	 * or -1 if this scene id isn't read yet.
	 * 
	 * @return	scene output value or -1 
	 */
	public int getSceneOutputValue(short sceneId);
	
	/**
	 * Sets the scene output value of this device for the given scene id and scene output value.
	 * 
	 * @param sceneId
	 * @param sceneOutputValue
	 */
	public void setSceneOutputValue(short sceneId, int sceneOutputValue);
	
	/**
	 * This configuration is very important. The devices can
	 * be configured to not react to some commands (scene calls).
	 * So you can't imply that a device automatically turns on (by default yes,
	 * but if someone configured his own scenes, then maybe not) after a
	 * scene call. This method returns true or false, if the configuration 
	 * for this sceneID already has been read
	 * 
	 * @param sceneId	the sceneID
	 * @return			true if this device has the configuration for this specific scene
	 */
	public boolean containsSceneConfig(short sceneId);
	
	/**
	 * Add the config for this scene. The config has the configuration
	 * for the specific sceneID.
	 * 
	 * @param sceneId	scene call id
	 * @param sceneSpec	config for this sceneID
	 */
	public void addSceneConfig(short sceneId, DeviceSceneSpec sceneSpec);
	
	/**
	 * Get the config for this scene. The config has the configuration
	 * for the specific sceneID.
	 * 
	 * @param sceneId 		scene call id
	 * @return sceneSpec	config for this sceneID
	 */
	public DeviceSceneSpec getSceneConfig(short sceneId);
	
	/**
	 * Should the device react on this scene call or not .
	 * 
	 * @param sceneId	scene call id
	 * @return			true, if this device should react on this sceneID
	 */
	public boolean doIgnoreScene(short sceneId);
		
	//for ESH
	
	/**
	 * Returns true if the power consumption is up to date or false if it has to be updated.
	 * 
	 * @return is up to date (true = yes | false = no)
	 */
	public boolean isPowerConsumptionUpToDate();
	
	/**
	 * Returns true if the electric meter is up to date or false if it has to be updated.
	 * 
	 * @return is up to date (true = yes | false = no)
	 */
	public boolean isElectricMeterUpToDate();
	
	/**
	 * Returns true if the energy meter is up to date or false if it has to be updated.
	 * 
	 * @return is up to date (true = yes | false = no)
	 */
	public boolean isEnergyMeterUpToDate();
	
	/**
	 * Returns true if all sensor data are up to date or false if some have to be updated.
	 * 
	 * @return is up to date (true = yes | false = no)
	 */	
	public boolean isSensorDataUpToDate();
	
	/**
	 * Sets the priority to refresh the data of the sensors to the given priorities.
	 * They can be never, low, medium or high.
	 * 
	 * @param powerConsumptionRefreshPriority
	 * @param electricMeterRefreshPriority
	 * @param energyMeterRefreshPriority
	 */
		public void setSensorDataRefreshPriority(String powerConsumptionRefreshPriority, 
			String electricMeterRefreshPriority, 
			String energyMeterRefreshPriority);
		
	/**
	 * Returns the priority of the power consumption refresh.
	 * 
	 * @return power consumption refresh priority 
	 */
	
	public String getPowerConsumptionRefreshPriority();
	
	/**
	 * Returns the priority of the electric meter refresh.
	 * 
	 * @return electric meter refresh priority 
	 */
	public String getElectricMeterRefreshPriority();
	
	/**
	 * Returns the priority of the energy meter refresh.
	 * 
	 * @return energy meter refresh priority 
	 */
	public String getEnergyMeterRefreshPriority();
	
	/**
	 * Returns true if the device is already added to the ESH and false if not.
	 * 
	 * @return is added already (true = yes | false = no)
	 */
	
	//public boolean isAddToESH();
	
	/**
	 * With this method you can set the boolean to true if a device is added or false if it is deleted.
	 * 
	 * @param isAdd (true = yes | false = no)
	 */
	//public void setIsAddToESH(boolean isAdd);
	
	/**
	 * Returns the next {@linkDeviceStateUpdate} to update the ESH-Thing to the current state.
	 * 
	 * @return DeviceStateUpdate for ESH-Thing
	 */
	//public DeviceStateUpdate getNextESHThingUpdateStates();
	
	/**
	 * Returns true if the ESH-Thing is up to date.
	 *  
	 * @return ESH-Thing is up to date (true = yes | false = no)
	 */
	//public boolean isESHThingUpToDate();
	
	/**
	 * Returns true if the device is up to date.
	 * 
	 * @return DigitalSTROM-Device is up to date (true = yes | false = no)
	 */
	public boolean isDeviceUpToDate();
	
	/**
	 * Returns the next {@linkDeviceStateUpdate} to update the DigitalSTROM-Device on the DigitalSTROM-Server.
	 * 
	 * @return DeviceStateUpdate for DigitalSTROM-Device
	 */
	public DeviceStateUpdate getNextDeviceUpdateState();
	
	/**
	 * Update the in ESH internal stored device object.
	 * 
	 * @param deviceStateUpdate
	 */
	public void updateInternalDeviceState(DeviceStateUpdate deviceStateUpdate);

	
	
	void callNamedScene(InternalScene scene);

	void undoNamedScene();

	void callScene(Short sceneNumber);

	InternalScene getAcitiveScene();

	void undoScene();

	void checkSceneConfig(Short sceneNumber, int prio);

	void registerDeviceStateListener(DeviceStatusListener listener);

	void unregisterDeviceStateListener();

	boolean isListenerRegisterd();

	public void setOutputMode(OutputModeEnum newOutputMode);
	
}
