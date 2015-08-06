package DigitalSTROMDevices;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import DigitalSTROMDevices.DeviceParameters.ChangeableDeviceConfigEnum;
import DigitalSTROMDevices.DeviceParameters.DSID;
import DigitalSTROMDevices.DeviceParameters.DeviceConstants;
import DigitalSTROMDevices.DeviceParameters.DeviceSceneSpec;
import DigitalSTROMDevices.DeviceParameters.DeviceStateUpdate;
import DigitalSTROMDevices.DeviceParameters.DeviceStateUpdateImpl;
import DigitalSTROMDevices.DeviceParameters.FunctionalColorGroupEnum;
import DigitalSTROMDevices.DeviceParameters.OutputModeEnum;
import GeneralConstants.DigitalSTROMBindingConstants;
import GeneralConstants.DigitalSTROMConfig;
import Scenes.Scene.InternalScene;

public class TestDevice implements Device {

	private DSID dsid = null;
	
	private DSID meterDSID = null;
	
	private String dSUID = null;
	
	private String hwInfo;
	
	private String name = null;
	
	
	private int zoneId = 0;
	
	private boolean isPresent = false;
	
	private boolean isOn = false;
	
	private boolean isOpen = true;
	
	private OutputModeEnum outputMode = null;
	
	
	private int outputValue = 0;
	
	private int maxOutputValue = DeviceConstants.DEFAULT_MAX_OUTPUTVALUE;
	
	private int minOutputValue = 0;
	
	FunctionalColorGroupEnum functionalGroup = null;
	
	
	private int slatPosition = 0;
	
	private int maxSlatPosition = DeviceConstants.MAX_SLAT_POSITION;
	
	private int minSlatPosition = DeviceConstants.MIN_SLAT_POSITION;
	
	
	private int powerConsumption = 0;
	
	private int energyMeterValue = 0;
	
	private int electricMeterValue = 0;
	
	
	private	List<Short> groupList = new LinkedList<Short>();
	
	//private List<DeviceListener> deviceListenerList = Collections.synchronizedList(new LinkedList<DeviceListener>());
	
	/* Cache the last MeterValues to get MeterData directly
	 * the key is the output value and the value is an Integer array for the meter data (0 = powerConsumption, 1 = electricMeter, 2 =EnergyMeter)
	 */
	private Map<Integer, Integer[]> cachedSensorMeterData = Collections.synchronizedMap(new HashMap<Integer, Integer[]>());
	
	private Map<Short, DeviceSceneSpec> sceneConfigMap = Collections.synchronizedMap(new HashMap<Short, DeviceSceneSpec>());
	
	private Map<Short, Integer> sceneOutputMap = Collections.synchronizedMap(new HashMap<Short, Integer>());
	
	/**
	 * Creates a new {@link JSONDeviceImpl} from the given DigitalSTROM-Device {@link JSONObject}.
	 * 
	 * @param group json object
	 */
	public TestDevice(String name, String dSID, String meterDSID, String dSUID, String hwInfo, boolean isOn, boolean isPresent,
			int zoneId, List<Short> groups, OutputModeEnum outputMode) {
		
		this.name = name;
		this.dsid = new DSID(dSID);
		this.meterDSID = new DSID(meterDSID);
		this.dSUID = dSUID;
		this.hwInfo = hwInfo;
		this.isOn = isOn;
		this.isPresent = isPresent;
		this.zoneId = zoneId;
		
		if (groups != null) {
			for (Short groupID :groups) {
				if (groupID != -1) {
					this.groupList.add(groupID);
					if(FunctionalColorGroupEnum.containsColorGroup((int) groupID)){
						this.functionalGroup = FunctionalColorGroupEnum.getMode((int) groupID);
					}
				}
			}
		}
		this.outputMode = outputMode;
		
		init();
		
	}
	
	private void init() {
		if (groupList.contains((short)1)) {
			maxOutputValue = DeviceConstants.MAX_OUTPUT_VALUE_LIGHT;
			if (this.isDimmable()) {
				minOutputValue = DeviceConstants.MIN_DIMM_VALUE;
			}
		}
		else {
			maxOutputValue = DeviceConstants.DEFAULT_MAX_OUTPUTVALUE;
			minOutputValue = 0;
		}
		
		if(isOn)
			outputValue = DeviceConstants.DEFAULT_MAX_OUTPUTVALUE;
	}
	
	@Override
	public DSID getDSID() {
		return dsid;
	}

	@Override
	public String getDSUID(){
		return this.dSUID;
	}
	
	@Override
	public synchronized DSID getMeterDSID(){
		return this.meterDSID;
	}
	
	@Override
	public synchronized void setMeterDSID(String meterDSID){
		this.meterDSID = new DSID(meterDSID);
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.METER_DSID);
		}
	}
	
	@Override
	public String getHWinfo() {
		return hwInfo;
	}
	
	@Override
	public synchronized String getName() {
		return this.name;
	}

	@Override
	public synchronized void setName(String name) {
		this.name = name;
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.DEVICE_NAME);
		}
	}

	@Override
	public List<Short> getGroups() {
		return new LinkedList<Short>(groupList);
	}
	
	@Override
	public void addGroup(Short groupID){
		if(!this.groupList.contains(groupID)){
			this.groupList.add(groupID);
		}
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.GROUPS);
		}
	}
	
	@Override
	public void setGroups(List<Short> newGroupList){
		if(newGroupList != null){
			this.groupList = newGroupList;
		}
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.GROUPS);
		}
	}

	@Override
	public synchronized int getZoneId() {
		return zoneId;
	}
	
	@Override
	public synchronized void setZoneId(int zoneID) {
		this.zoneId = zoneID;
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.ZONE_ID);
		}
	}

	@Override
	public synchronized boolean isPresent() {
		return isPresent;
	}
	
	@Override
	public synchronized void setIsPresent(boolean isPresent){
		this.isPresent = isPresent;
		if(listener != null){
			if(!isPresent){
				listener.onDeviceRemoved(this);
			} else{
				listener.onDeviceAdded(this);
			}
			
		}
	}

	@Override
	public synchronized boolean isOn() {
		return isOn;
	}
	
	@Override
	public synchronized void setIsOn(boolean flag) {
		if(flag){
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
		} else {
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
		}
	}
	
	@Override
	public synchronized boolean isOpen(){
		return this.isOpen;
	}
	
	@Override
	public synchronized void setIsOpen(boolean flag){
		if(flag){
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OPEN_CLOSE, 1));
		} else {
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OPEN_CLOSE, -1));
		}
	}
	
	@Override
	public synchronized void setOutputValue(int value) {
		if(!isRollershutter()){
			if (value <= 0) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
			
			} else if (value > maxOutputValue) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS, value));
			}
		} else{
			if (value <= 0) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
			} else if (value > maxOutputValue) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, value));
			}
		}
		
		this.outputValueBeforeSceneCall = outputValue;
		informLastSceneAboutSceneCall((short) -1);
		this.activeScene = null;
	}

	@Override
	public synchronized boolean isDimmable() {
		if (outputMode == null) {
			return false;
		}
		switch(this.outputMode){
		case RMS_DIMMER:
		case RMS_DIMMER_CC:
		case PC_DIMMER:
		case PC_DIMMER_CC:
		case RPC_DIMMER:
		case RPC_DIMMER_CC: return true;
		default:
			return false;
		}
	}
	
	@Override
	public synchronized boolean isDeviceWithOutput(){
		return this.outputMode != null && !this.outputMode.equals(OutputModeEnum.DISABLED);
	}
	
	@Override
	public synchronized FunctionalColorGroupEnum getFunctionalColorGroup(){
		return this.functionalGroup;
	}
	
	@Override
	public synchronized void setFunctionalColorGroup(FunctionalColorGroupEnum fuctionalColorGroup){
		this.functionalGroup = fuctionalColorGroup;
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.FUNCTIONAL_GROUP);
		}
	}
	
	@Override
	public OutputModeEnum getOutputMode() {
		return outputMode;
	}
	
	@Override
	public synchronized void setOutputMode(OutputModeEnum newOutputMode){
		this.outputMode = newOutputMode;
		if(listener != null){
			listener.onDeviceConfigChanged(ChangeableDeviceConfigEnum.OUTPUT_MODE);
		}
	}

	@Override
	public synchronized void increase() {
		if (isDimmable()) {
			
			if (outputValue == maxOutputValue) {
				return;
			}
			if ((outputValue + getDimmStep()) > maxOutputValue) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS_INCREASE, outputValue + getDimmStep()));
				//outputValue += getDimmStep();
			}
			//setIsOn(true);
			//notifyDeviceListener(this.dsid.getValue());
		}
		
		if(isRollershutter()){
			if (slatPosition == maxSlatPosition) {
				return;
			}
			if ((slatPosition + getDimmStep()) > slatPosition) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, maxSlatPosition));
				//outputValue = maxOutputValue;
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLAT_INCREASE, slatPosition + getDimmStep()));
				//outputValue += getDimmStep();
			}
		}
	}

	@Override
	public synchronized void decrease() {
		if (isDimmable()) {
			if (outputValue == minOutputValue) {
			/*	if (outputValue == 0) {
					setIsOn(false);
				}*/
				return;
			}
			
			if ((outputValue - getDimmStep()) <= minOutputValue) {
							
				if (isOn) {
					this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
				}
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS_DECREASE, outputValue - getDimmStep()));
			}
		}
		
		if(isRollershutter()){
			if (slatPosition == minSlatPosition) {
				return;
			}
			if ((slatPosition + getDimmStep()) < slatPosition) {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, minSlatPosition));
			}
			else {
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLAT_DECREASE, slatPosition - getDimmStep()));
			}
		}

	}
	
	@Override
	public synchronized int getOutputValue() {
		return outputValue;
	}
	
	@Override
	public int getMaxOutputValue() {
		return maxOutputValue;
	}

	@Override
	public boolean isRollershutter() {
		if (outputMode == null) {
			return false;
		}
		return outputMode.equals(OutputModeEnum.POSITION_CON) || outputMode.equals(OutputModeEnum.POSITION_CON_US);
	}

	@Override
	public synchronized int getSlatPosition() {
		return slatPosition;
	}

	@Override
	public synchronized void setSlatPosition(int position) {
		if(position == this.slatPosition){
			return;
		}
		
		if (position < minSlatPosition) {
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, minSlatPosition));
			//slatPosition = minSlatPosition;
		}
		else if (position > this.maxSlatPosition) {
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, maxSlatPosition));
			//slatPosition = this.maxSlatPosition;
		}
		else {
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, position));
			//this.slatPosition = position;
		}
		//notifyDeviceListener(this.dsid.getValue());
	}

	@Override
	public synchronized int getPowerConsumption() {
		return powerConsumption;
	}
	
	@Override
	public synchronized void setPowerConsumption(int powerConsumption) {
		lastPowerConsumptionUpdate = System.currentTimeMillis();
		
		if(powerConsumption == this.powerConsumption) {
			return;
		}
		
		if (powerConsumption < 0) {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_POWER_CONSUMPTION, 0));
			this.powerConsumption = 0;
		}
		else {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_POWER_CONSUMPTION, powerConsumption));
			this.powerConsumption = powerConsumption;
			this.addPowerConsumptionToMeterCache(this.getOutputValue(), powerConsumption);
		}
	}

	@Override
	public synchronized int getEnergyMeterValue() {
		return energyMeterValue;
	}
	
	@Override
	public synchronized void setEnergyMeterValue(int energyMeterValue) {
		lastEnergyMeterUpdate = System.currentTimeMillis();
		
		if(energyMeterValue == this.electricMeterValue) {
			return;
		}
		
		if (energyMeterValue < 0) {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ELECTRIC_METER_VALUE, 0));
			this.energyMeterValue = 0;
		}
		else {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ELECTRIC_METER_VALUE, energyMeterValue));
			this.addEnergyMeterValueToMeterCache(this.getOutputValue(), energyMeterValue);
			this.energyMeterValue = energyMeterValue;
		}
	}

	@Override
	public synchronized int getElectricMeterValue() {
		return electricMeterValue;
	}

	
	
	@Override
	public synchronized void setElectricMeterValue(int electricMeterValue) {
		lastElectricMeterUpdate = System.currentTimeMillis();
		
		if(electricMeterValue == this.electricMeterValue){
			return;
		}
		
		if (electricMeterValue < 0) {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ENERGY_METER_VALUE, 0));
			this.electricMeterValue = 0; 
		}
		else {
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ENERGY_METER_VALUE, electricMeterValue));
			this.addElectricMeterValueToMeterCache(this.getOutputValue(), electricMeterValue);
			this.electricMeterValue = electricMeterValue;
		}
	}
	
	private void addPowerConsumptionToMeterCache(int outputValue, int powerConsumption){
		Integer[] cachedMeterData = cachedSensorMeterData.get(outputValue);
		if(cachedMeterData == null){
			cachedMeterData = new Integer[3];
		} 
		
		cachedMeterData[0] = powerConsumption;
		
		this.cachedSensorMeterData.put(outputValue, cachedMeterData);
	}

	private void addElectricMeterValueToMeterCache(int outputValue, int electricMeterValue){
		Integer[] cachedMeterData = cachedSensorMeterData.get(outputValue);
		if(cachedMeterData == null){
			cachedMeterData = new Integer[3];
		} 
		
		cachedMeterData[1] = electricMeterValue;
		
		this.cachedSensorMeterData.put(outputValue, cachedMeterData);
	}
	
	private void addEnergyMeterValueToMeterCache(int outputValue, int energyMeterValue){
		Integer[] cachedMeterData = cachedSensorMeterData.get(outputValue);
		if(cachedMeterData == null){
			cachedMeterData = new Integer[3];
		}  
		
		cachedMeterData[2] = energyMeterValue;
		
		this.cachedSensorMeterData.put(outputValue, cachedMeterData);
	}
	
	private short getDimmStep() {
		if (isDimmable()) {
			return DeviceConstants.DIMM_STEP_LIGHT;
		}
		else if (isRollershutter()) {
			return DeviceConstants.MOVE_STEP_ROLLERSHUTTER;
		}
		else {
			return DeviceConstants.DEFAULT_MOVE_STEP;
		}
	}

	@Override
	public int getMaxSlatPosition() {
		return maxSlatPosition;
	}

	@Override
	public int getMinSlatPosition() {
		return minSlatPosition;
	}
	
	/**** Begin-Scenes ****/
	
	private InternalScene activeScene = null;
	private int outputValueBeforeSceneCall = 0;
	
	@Override
	public synchronized void callNamedScene(InternalScene scene){
		//boolean haveStoredOutput = 
				internalCallScene(scene.getSceneID()) ;
		this.activeScene = scene;	
		//return haveStoredOutput;
	}
	
	//weis net
	@Override
	public void checkSceneConfig(Short sceneNumber, int prio){
		if(!containsSceneConfig(sceneNumber)){
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SCENE_CONFIG, prio+sceneNumber));
		} 
		if(sceneOutputMap.get(sceneNumber) == null){
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SCENE_OUTPUT, prio+sceneNumber));
		}
	}
	
	@Override
	public synchronized void undoNamedScene(){
		internalUndoScene();
		this.activeScene = null;	
	}
	
	@Override
	public synchronized void callScene(Short sceneNumber){
		this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_CALL_SCENE, sceneNumber));
	}
	
	short activeSceneNumber = -1;
	
	private synchronized void internalCallScene(Short sceneNumber){
		if(containsSceneConfig(sceneNumber)){
			if(doIgnoreScene(sceneNumber)){
				return;
			}
		} else{
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SCENE_CONFIG, sceneNumber));
		}
		
		if(checkSceneNumber(sceneNumber)){
			return;
		}
		
		//boolean flag = false;
		if(sceneOutputMap.get(sceneNumber) != null){
			if(!isRollershutter()){
				this.outputValueBeforeSceneCall = this.outputValue;
				this.outputValue = sceneOutputMap.get(sceneNumber);
				addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS, this.outputValue));
			} else{
				this.outputValueBeforeSceneCall = this.slatPosition;
				this.slatPosition = sceneOutputMap.get(sceneNumber);
				addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, this.slatPosition));
			}
			//flag = true;
		} else{
			this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SCENE_OUTPUT, sceneNumber));
		}
		activeSceneNumber = sceneNumber;
		informLastSceneAboutSceneCall(sceneNumber);
		
		//return flag;
	}
	
	
	private boolean checkSceneNumber(Short sceneNumber){	
		switch(sceneNumber){
				//on scenes
			case 51:
			case 14:
				if (isDimmable()) {
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
				}
				
				if(isRollershutter()){
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OPEN_CLOSE, 1));
				}
				return true;
			//off scenes
			case 13: 
			case 50: 
				if (isDimmable()) {
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
				}
				if(isRollershutter()){
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OPEN_CLOSE, -1));
				}
				return true;
			//increase scenes
			case 11:
			case 43:
			case 45:
			case 47:
			case 49: 
				if (isDimmable()) {
					if (outputValue == maxOutputValue) {
						return true;
					}
					if ((outputValue + getDimmStep()) > maxOutputValue) {
						this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, 1));
					}
					else {
						this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS_INCREASE, outputValue + getDimmStep()));
					}
				}
				
				if(isRollershutter()){
					if (slatPosition == maxSlatPosition) {
						return true;
					}
					if ((slatPosition + getDimmStep()) > slatPosition) {
						this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, maxSlatPosition));
					}
					else {
						this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLAT_INCREASE, slatPosition + getDimmStep()));
					}
				}
				return true;
			//decrease scenes
			case 12:
			case 42:
			case 44:
			case 46:
			case 48:
					if (isDimmable()) {
						if (outputValue == minOutputValue) {
							return true;
						}
						if ((outputValue - getDimmStep()) <= minOutputValue) {
							if (isOn) {
								this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
							}
						}
						else {
							this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS_DECREASE, outputValue - getDimmStep()));
						}
					}
					
					if(isRollershutter()){
						if (slatPosition == minSlatPosition) {
							return true;
						}
						if ((slatPosition + getDimmStep()) < slatPosition) {
							this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, minSlatPosition));
						}
						else {
							this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLAT_DECREASE, slatPosition - getDimmStep()));
						}
					}
					
					return true;
			//Stop scenes
			case 52:
			case 53:
			case 54:
			case 55:
			case 15:
				this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OUTPUT_VALUE, 0));
				return true;	
			//Area Stepping continue scenes
			case 10:
				//TODO: gute Frage was passiert hier?
				return true;
					
			//Auto-Off:
			case 40:
				//TOTO: checken ob das stimmt.
				if (isDimmable()) {
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ON_OFF, -1));
				}
				if(isRollershutter()){
					this.updateInternalDeviceState(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_OPEN_CLOSE, -1));
				}
				return true;
				
			default: return false;
		}
	}
	
	private void informLastSceneAboutSceneCall(short sceneNumber){
		if(this.activeScene != null && this.activeScene.getSceneID() != sceneNumber){
			this.activeScene.deviceSceneChanged(sceneNumber);
			this.activeScene = null;
		}
	}
	
	@Override
	public synchronized void undoScene(){
		this.deviceStateUpdates.add(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_UNDO_SCENE, this.activeSceneNumber));
	}
	
	
	private synchronized void internalUndoScene(){
		if(!isRollershutter()){
			this.outputValue = this.outputValueBeforeSceneCall;
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_BRIGHTNESS, this.outputValue));
		} else{
			this.slatPosition = this.outputValueBeforeSceneCall;
			addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_SLATPOSITION, this.slatPosition));
		}
		
		if(this.activeScene != null){
			informLastSceneAboutSceneCall((short) -1);
		}
		
		if(activeSceneNumber != -1){
			activeSceneNumber = -1;
		}
	}

	@Override
	public InternalScene getAcitiveScene(){
		return this.activeScene;
	}
	
	@Override
	public int getSceneOutputValue(short sceneId) {
		synchronized(sceneOutputMap) {
			if (sceneOutputMap.containsKey(sceneId)) {
				return sceneOutputMap.get(sceneId);
			}
		}
		return -1;
	}

	@Override
	public void setSceneOutputValue(short sceneId, int value) {
		synchronized(sceneOutputMap) {
			sceneOutputMap.put(sceneId, value);
			if(listener != null){
				listener.onSceneConfigAdded(sceneId);
			}
		}
	}

	@Override
	public void addSceneConfig(short sceneId, DeviceSceneSpec sceneSpec) {
		if (sceneSpec != null) {
			synchronized(sceneConfigMap) {
				sceneConfigMap.put(sceneId, sceneSpec);
				if(listener != null){
					listener.onSceneConfigAdded(sceneId);
				}
			}
		}
	}
	
	@Override
	public DeviceSceneSpec getSceneConfig(short sceneId) {
		synchronized(sceneConfigMap) {
			return sceneConfigMap.get(sceneId);
		}
	}

	@Override
	public boolean doIgnoreScene(short sceneId) {
		synchronized(sceneConfigMap) {
			if (this.sceneConfigMap.containsKey(sceneId)) {
				return this.sceneConfigMap.get(sceneId).isDontCare();
			}
		}
		return false;
	}

	@Override
	public boolean containsSceneConfig(short sceneId) {
		synchronized(sceneConfigMap) {
			return sceneConfigMap.containsKey(sceneId);
		}
	}
	
	/**** End-Scenes ****/
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device) {
			Device device = (Device)obj;
			return device.getDSID().equals(this.getDSID());
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.getDSID().hashCode();
	}
	
	//for ESH
	
	//private List<DeviceStateUpdate> eshThingStateUpdates = Collections.synchronizedList(new LinkedList<DeviceStateUpdate>());//new LinkedList<DeviceStateUpdate>();
	private List<DeviceStateUpdate> deviceStateUpdates = Collections.synchronizedList(new LinkedList<DeviceStateUpdate>());//new LinkedList<DeviceStateUpdate>();
	
	//private boolean isAddToESH = false;
	
	//save the last update time of the sensor data
	private long lastElectricMeterUpdate = System.currentTimeMillis();
	private long lastEnergyMeterUpdate = System.currentTimeMillis();
	private long lastPowerConsumptionUpdate = System.currentTimeMillis();
	
	//sensor data refresh priorities
	private String powerConsumptionRefreshPriority = DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER; 
	private String electricMeterRefreshPriority = DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER; 
	private String energyMeterRefreshPriority = DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER;
	/*
	@Override
	public boolean isAddToESH(){
		return this.isAddToESH;
	}
	
	@Override
	public void setIsAddToESH(boolean isAdd){
		this.isAddToESH = isAdd;
	}*/
	
	@Override
	public boolean isPowerConsumptionUpToDate(){
		return isOn && !isRollershutter() && !this.powerConsumptionRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER) ?
				(this.lastPowerConsumptionUpdate + DigitalSTROMConfig.SENSORDATA_REFRESH_INTERVAL) > System.currentTimeMillis() 
				:true;
	}
	 
	@Override
	public boolean isElectricMeterUpToDate(){
		return isOn && !isRollershutter() && !this.electricMeterRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER) ?
				(this.lastElectricMeterUpdate + DigitalSTROMConfig.SENSORDATA_REFRESH_INTERVAL) > System.currentTimeMillis()
				:true;
	}
	
	@Override
	public boolean isEnergyMeterUpToDate(){
		return isOn && !isRollershutter() && !this.energyMeterRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER) ?
				(this.lastEnergyMeterUpdate + DigitalSTROMConfig.SENSORDATA_REFRESH_INTERVAL) > System.currentTimeMillis()
				:true;
	}
	
	//1. Unterscheidung zwischen nötigen und nich nötigen Sensordatenabfragen? 
	//	 z.B. Lampe macht sinn, Rollershutter nicht, weil nur beim runter bzw. hochfahren Strom verbraucht wird
	//2. Wenn der Refreshinterval vom Nutzer bestimmt wird Zeit übergeben (eigene var, wegen Übergabe des Devices), 
	//   da diese der Bridge-config entnommen werden muss
	@Override
	public boolean isSensorDataUpToDate(){
		return isOn && !isRollershutter() ? //Überprüfen ob es noch weitere gibt, bei denen es keinen Sinn macht Sensordaten zu erfassen
				isPowerConsumptionUpToDate() &&
				isElectricMeterUpToDate() &&
				isEnergyMeterUpToDate()
				:true;
	}
	
	
	@Override
	public void setSensorDataRefreshPriority(String powerConsumptionRefreshPriority, 
			String electricMeterRefreshPriority, 
			String energyMeterRefreshPriority){
		if(checkPriority(powerConsumptionRefreshPriority) != null){
			this.powerConsumptionRefreshPriority = powerConsumptionRefreshPriority;
		}
		if(checkPriority(electricMeterRefreshPriority) != null){
			this.electricMeterRefreshPriority = electricMeterRefreshPriority;
		}
		if(checkPriority(energyMeterRefreshPriority) != null){
			this.energyMeterRefreshPriority = energyMeterRefreshPriority;
		}
		
	}
	
	 
	private String checkPriority(String priority){
		switch(priority){
		case DigitalSTROMBindingConstants.REFRESH_PRIORITY_HIGH:
			break;
		case DigitalSTROMBindingConstants.REFRESH_PRIORITY_MEDIUM:
			break;
		case DigitalSTROMBindingConstants.REFRESH_PRIORITY_LOW:
			break;
		case DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER:
			return null;
		default:
			System.err.println("Sensor data update priority do not exist! Please check the input!");
			return null;
		}
		return priority;
	}

	@Override
	public String getPowerConsumptionRefreshPriority(){
		return this.powerConsumptionRefreshPriority;
	}
	
	@Override
	public String getElectricMeterRefreshPriority(){
		return this.electricMeterRefreshPriority;
	}
	
	@Override
	public String getEnergyMeterRefreshPriority(){
		return this.energyMeterRefreshPriority;
	}
	
	/*
	@Override
	public DeviceStateUpdate getNextESHThingUpdateStates() {
		return !this.eshThingStateUpdates.isEmpty()?this.eshThingStateUpdates.remove(0):null;
	}
	@Override
	public boolean isESHThingUpToDate(){
		return this.eshThingStateUpdates.isEmpty();
	}*/
	
	@Override
	public boolean isDeviceUpToDate() {
		return this.deviceStateUpdates.isEmpty();
	}

	@Override
	public DeviceStateUpdate getNextDeviceUpdateState() {
		return !this.deviceStateUpdates.isEmpty()?this.deviceStateUpdates.remove(0):null;
	}

	@Override
	public synchronized void updateInternalDeviceState(DeviceStateUpdate deviceStateUpdate) {
		if(deviceStateUpdate != null){			
			switch(deviceStateUpdate.getType()){
				case DeviceStateUpdate.UPDATE_BRIGHTNESS_DECREASE:
				case DeviceStateUpdate.UPDATE_BRIGHTNESS_INCREASE:
				case DeviceStateUpdate.UPDATE_BRIGHTNESS: 
					this.outputValue = deviceStateUpdate.getValue();
					if(this.outputValue <= 0){
						this.isOn = false;
						setPowerConsumption(0);
						setEnergyMeterValue(0);
						setElectricMeterValue(0);
					} else{
						this.isOn = true;
						setCachedMeterData();
					}
					break;
				case DeviceStateUpdate.UPDATE_ON_OFF: 
					if(deviceStateUpdate.getValue() < 0){
						this.outputValue = 0;
						this.isOn = false;
						setPowerConsumption(0);
						setEnergyMeterValue(0);
						setElectricMeterValue(0);
					} else{
						this.outputValue = this.maxOutputValue;
						this.isOn = true;
						setCachedMeterData();
					}
					break;
				case DeviceStateUpdate.UPDATE_OPEN_CLOSE: 
					if(deviceStateUpdate.getValue() < 0){
						this.slatPosition = 0;
						this.isOpen = false;
					} else{
						this.outputValue = this.maxOutputValue;
						this.isOpen = true;
					}
					break;
				case DeviceStateUpdate.UPDATE_SLAT_DECREASE:
				case DeviceStateUpdate.UPDATE_SLAT_INCREASE:
				case DeviceStateUpdate.UPDATE_SLATPOSITION: 
					this.slatPosition = deviceStateUpdate.getValue();
					break;
				case DeviceStateUpdate.UPDATE_ELECTRIC_METER_VALUE:
					setElectricMeterValue(deviceStateUpdate.getValue());
					return;
				case DeviceStateUpdate.UPDATE_ENERGY_METER_VALUE:
					setEnergyMeterValue(deviceStateUpdate.getValue());
					return;
				case DeviceStateUpdate.UPDATE_POWER_CONSUMPTION:
					setPowerConsumption(deviceStateUpdate.getValue());
					return;
				case DeviceStateUpdate.UPDATE_CALL_SCENE:
					this.internalCallScene((short) deviceStateUpdate.getValue()); 
					return;
				case DeviceStateUpdate.UPDATE_UNDO_SCENE:
					this.internalUndoScene(); 
					return;
				/*case DeviceStateUpdate.UPDATE_SCENE_CONFIG:
					break;
				case DeviceStateUpdate.UPDATE_SCENE_OUTPUT:
					break;*/
				default: return;
			}
			
			addEshThingStateUpdate(deviceStateUpdate);
			
		}
	}
	
	private DeviceStatusListener listener = null;
	
	@Override
	public void registerDeviceStateListener(DeviceStatusListener listener){
		if(listener != null){
			this.listener = listener;
			listener.onDeviceAdded(this);
		}
	}
	
	@Override
	public void unregisterDeviceStateListener(){
		this.listener = null;
		//listener.onDeviceRemoved(this);
	}
	
	@Override
	public boolean isListenerRegisterd(){
		return (listener != null);
	}
	
	private void setCachedMeterData(){
		System.out.println("load cached sensor data");
		Integer[] cachedSensorData = this.cachedSensorMeterData.get(this.getOutputValue());
		if(cachedSensorData != null){
			if(cachedSensorData[0] != null && !this.powerConsumptionRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER)){
				this.powerConsumption = cachedSensorData[0];
				addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_POWER_CONSUMPTION, cachedSensorData[0]));
				
			}
			if(cachedSensorData[1] != null && !this.electricMeterRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER)){
				this.electricMeterValue = cachedSensorData[1];
				addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ELECTRIC_METER_VALUE, cachedSensorData[1]));
				
			}
			if(cachedSensorData[2] != null && !this.energyMeterRefreshPriority.contains(DigitalSTROMBindingConstants.REFRESH_PRIORITY_NEVER)){
				this.energyMeterValue = cachedSensorData[2];
				addEshThingStateUpdate(new DeviceStateUpdateImpl(DeviceStateUpdate.UPDATE_ENERGY_METER_VALUE, cachedSensorData[2]));
				
			}
		}
	}
	
	/**
	 *  if the device is added to ESH we save ever device state update to change it in ESH
	 * 	if the device isn't added ESH we only save the current device state
	 */
	private void addEshThingStateUpdate(DeviceStateUpdate deviceStateUpdate){
		if(listener != null){
			listener.onDeviceStateChanged(deviceStateUpdate);
		}
		/*
		if(isAddToESH){
			System.out.println("Add deviceStatusUpdate command {} to eshThingUpdates" + deviceStateUpdate.getType());
			this.eshThingStateUpdates.add(deviceStateUpdate);
		}else{
			this.eshThingStateUpdates.clear();
		}
		*/
	}
	
	
	//List<SceneStatusListener> sceneStatusListeners; 
	
	/*private final String id;
	
	private HashMap<Short, Integer> sceneOutput = new HashMap<Short,Integer>(128);
	private int outputValue = 0;
	
	private InternalScene activeScene = null;
	private int outputValueBeforeSceneCall = 0;
	
	public TestDevice(String id){
		this.id = id;
	}
	
	public String getID(){
		return this.id;
	}
	
	public synchronized boolean callNamedScene(InternalScene scene){
		boolean haveStoredOutput = callScene(scene.getSceneID()) ;
		this.activeScene = scene;	
		return haveStoredOutput;
	}
	
	public synchronized void undoNamedScene(){
		undoScene();
		this.activeScene = null;	
	}
	
	public synchronized boolean callScene(Short sceneNumber){
		boolean flag = false;
		if(sceneOutput.get(sceneNumber) != null){
			this.outputValueBeforeSceneCall = this.outputValue;
			this.outputValue = sceneOutput.get(sceneNumber);
			//this.activeScene = sceneNumber;
			flag = true;
		}
		
		informLastSceneAboutSceneCall(sceneNumber);
		
		return flag;
	}
	
	private void informLastSceneAboutSceneCall(short sceneNumber){
		if(this.activeScene != null && this.activeScene.getSceneID() != sceneNumber){
			this.activeScene.deviceSceneChanged(sceneNumber);
			this.activeScene = null;
		}
	}
	
	public synchronized void undoScene(){
		//if(this.activeScene != -1){
			this.outputValue = this.outputValueBeforeSceneCall;
			if(this.activeScene != null){
				informLastSceneAboutSceneCall((short) -1);
			}
		//}
	}

	public synchronized int getOutputValue() {
		return outputValue;
	}

	public InternalScene getAcitiveScene(){
		return this.activeScene;
	}
	
	public synchronized void setOutputValue(int outputValue) {
		//implied that this is not a Scene-Call and so it can't be undo.
		this.outputValueBeforeSceneCall = outputValue;
		this.outputValue = outputValue;
		informLastSceneAboutSceneCall((short) -1);
		this.activeScene = null;
		this.notifyAll();
		
	}
	
	public void setSceneOutput(Short sceneNumber, Integer sceneValue){
		this.sceneOutput.put(sceneNumber, sceneValue);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return "Device [id=" + id + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TestDevice))
			return false;
		TestDevice other = (TestDevice) obj;
		if (id != other.getID())
			return false;
		return true;
	}
	
	/*public synchronized void registerSceneStatuListener(SceneStatusListener listener){
		this.sceneStatusListeners.add(listener);
	}
	
	public synchronized void unregisterSceneStatuListener(SceneStatusListener listener){
		this.sceneStatusListeners.remove(listener);
	}*/
}
