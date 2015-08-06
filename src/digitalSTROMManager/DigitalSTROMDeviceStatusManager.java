package digitalSTROMManager;

import digitalSTROMListener.DeviceStatusListener;
import digitalSTROMListener.SceneStatusListener;
import digitalSTROMListener.TotalPowerConsumptionListener;
import digitalSTROMSensorJobExecuter.sensorJob.SensorJob;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DeviceStateUpdate;
import digitalSTROMStructure.digitalSTROMScene.InternalScene;

/**
 * 
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DigitalSTROMDeviceStatusManager {

	/**
	 * 
	 */
	public void start();
	
	/**
	 * 
	 */
	public void stop();
	
	/**
	 * 
	 */
	public void restart();
	
	/**
	 * 
	 * @param scene
	 * @param call_undo
	 */
	public void sendSceneComandsToDSS(InternalScene scene, boolean call_undo);
	
	/**
	 * 
	 * @param device
	 */
	public void sendStopComandsToDSS(Device device);
	
	/**
	 * 
	 * @param device
	 */
	public void sendComandsToDSS(Device device);
	
   /**
     * This method adds a {@link SensorJobs} with the appropriate priority to the {@link SensorJobExecuter}.
     * 
     * @param sensorJob
     * @param priority 
     */
    public void updateSensorData(SensorJob sensorJob, String priority);
    
    /**
     * This method adds a {@link SensorJobs} with the appropriate priority to the {@link SceneSensorJobExecuter}.
     * 
     * @param device
     * @param deviceStateUpdate 
     */
    public void updateSceneData(Device device, DeviceStateUpdate deviceStateUpdate);
    
    /**
     * 
     * @param deviceListener
     */
    public void registerDeviceListener(DeviceStatusListener deviceListener);

    /**
     * 
     * @param deviceListener
     */
	public void unregisterDeviceListener(DeviceStatusListener deviceListener);
	
	/**
	 * 
	 * @param totalPowerConsumptionListener
	 */
	public void registerTotalPowerConsumptionListener(TotalPowerConsumptionListener totalPowerConsumptionListener);
	
	/**
	 * 
	 */
	public void unregisterTotalPowerConsumptionListener();
	
	/**
	 * 
	 * @param sceneListener
	 */
	public void registerSceneListener(SceneStatusListener sceneListener);
	
	/**
	 * 
	 * @param sceneListener
	 */
	public void unregisterSceneListener(SceneStatusListener sceneListener);
}
