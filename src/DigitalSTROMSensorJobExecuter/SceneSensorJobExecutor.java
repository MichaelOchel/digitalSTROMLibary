package DigitalSTROMSensorJobExecuter;

import DSServerConnection.DigitalstromConnectionManager;
import DigitalSTROMSensorJobExecuter.SensorJob.SensorJob;



/**
 * This class performs the sensor Jobs by DigitalSTROM Rule 9 "Application processes that do automatic 
 * cyclic reads of measured values are subject to a request limit: at maximum one request per minute
 * and circuit is allowed.". 
 * In addition priorities can be assigned to jobs .
 * 
 * @author Michael Ochel
 * @author Matthias Siegele
 * 
 */
public class SceneSensorJobExecutor extends AbstractSensorJobExecutor {
	
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
