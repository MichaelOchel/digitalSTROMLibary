package digitalSTROMSensorJobExecuter;

import digitalSTROMManager.DigitalstromConnectionManager;
import digitalSTROMSensorJobExecuter.sensorJob.SensorJob;



/**
 * This class performs the sensor Jobs by DigitalSTROM Rule 9 "Application processes that do automatic 
 * cyclic reads of measured values are subject to a request limit: at maximum one request per minute
 * and circuit is allowed.". 
 * 
 * In addition priorities can be assigned to jobs therefor an {@link SceneSensorJobExecutor} offers the methods
 * {@link #addHighPriorityJob()}, {@link #addLowPriorityJob()} and {@link #addLowPriorityJob()}.
 * <p>
 * Note: 
 * In contrast to the {@link SensorJobExecutor} the {@link SceneSensorJobExecutor} will execute {@link SensorJob}s with high priority 
 * always before medium priority {@link SensorJob}s and so on.  
 * 
 * 
 * @author Michael Ochel
 * @author Matthias Siegele
 * 
 */
public class SceneSensorJobExecutor extends AbstractSensorJobExecutor {
	
	/**
	 * 
	 * @param connectionManager
	 */
	public SceneSensorJobExecutor(DigitalstromConnectionManager connectionManager) {
		super(connectionManager);
	}

	/**
	 * Adds a high priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	public void addHighPriorityJob(SensorJob sensorJob) {
		if(sensorJob == null) return;
		sensorJob.setInitalisationTime(0);
		addSensorJobToCircuitScheduler(sensorJob);
	}

	/**
	 * Adds a medium priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	public void addMediumPriorityJob(SensorJob sensorJob) {
		if(sensorJob == null) return;
		sensorJob.setInitalisationTime(1);
		addSensorJobToCircuitScheduler(sensorJob);
	}

	/**
	 * Adds a low priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	public void addLowPriorityJob(SensorJob sensorJob) {
		if(sensorJob == null) return;
		sensorJob.setInitalisationTime(2);
		addSensorJobToCircuitScheduler(sensorJob);
	}


}
