package digitalSTROMSensorJobExecuter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import digitalSTROMConfiguration.DigitalSTROMConfig;
import digitalSTROMManager.DigitalstromConnectionManager;
import digitalSTROMSensorJobExecuter.sensorJob.SensorJob;
import digitalSTROMServerConnection.DigitalSTROMAPI;
import digitalSTROMStructure.digitalSTROMDevices.Device;
import digitalSTROMStructure.digitalSTROMDevices.deviceParameters.DSID;

public abstract class AbstractSensorJobExecutor {

	private boolean shutdown = false;

	private long lowestNextExecutionTime = 0;
	private long sleepTime = 1000;
	
	
	private final DigitalSTROMAPI digitalSTROM;
	
	private DigitalstromConnectionManager connectionManager = null;
	
	//private Logger logger = LoggerFactory.getLogger(SensorJobExecutor.class);

	private List<CircuitScheduler> circuitSchedulerList = Collections
			.synchronizedList(new LinkedList<CircuitScheduler>());
	
	Thread executer = new Thread(){
		public void run() {
			while (!shutdown) {
				lowestNextExecutionTime = System.currentTimeMillis() + DigitalSTROMConfig.SENSOR_READING_WAIT_TIME;
				boolean noMoreJobs = true;
			
				synchronized(circuitSchedulerList){
					for(CircuitScheduler circuit: circuitSchedulerList){
						SensorJob sensorJob = circuit.getNextSensorJob();
						if(sensorJob != null && connectionManager.checkConnection()){
							sensorJob.execute(digitalSTROM, connectionManager.getSessionToken());
						} else{
							if(lowestNextExecutionTime > circuit.getNextExecutionTime() && 
									circuit.getNextExecutionTime() > System.currentTimeMillis()){
								lowestNextExecutionTime = circuit.getNextExecutionTime();
							}
						}
						if(!circuit.noMoreJobs()){
							noMoreJobs = false;
						}
					}
				}
				try {
					if(noMoreJobs){
						synchronized(this){
							this.wait();
						}
					}else{
						sleepTime = lowestNextExecutionTime - System.currentTimeMillis();
						if(sleepTime > 0){
							sleep(sleepTime);
						}
					}
				} catch (InterruptedException e) {
					
				}
			}	
		}
	};

	/**
	 * Creates a new SensorJobExecuter.
	 * 
	 * @param digitalSTROMAPI
	 * @param dssBrideHandler
	 */
	public AbstractSensorJobExecutor(DigitalstromConnectionManager connectionManager){
		this.connectionManager = connectionManager;
		this.digitalSTROM = connectionManager.getDigitalSTROMAPI();
		//this.dssBrideHandler = dssBrideHandler;
	}
	
	/**
	 * Stops the SensorJobExecuter Thread.
	 * 
	 */
	public synchronized void shutdown() {
		this.shutdown = true;
	}
	
	
	/**
	 * Restarts the SensorJobExecuter Thread.
	 * 
	 */
	public synchronized void wakeUp() {
		this.shutdown = false;
		executer.run();
	}
	
	/**
	 * Starts the SensorJobExecuter Thread.
	 */
	public void startExecuter(){
		executer.start();
	}
	
	/**
	 * Adds a high priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	protected void addHighPriorityJob(SensorJob sensorJob) {
		//TODO: can be Overridden to implement a priority
		addSensorJobToCircuitScheduler(sensorJob);
	}

	/**
	 * Adds a medium priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	protected void addMediumPriorityJob(SensorJob sensorJob) {
		//TODO: can be Overridden to implement a priority
		addSensorJobToCircuitScheduler(sensorJob);
	}

	/**
	 * Adds a low priority SensorJob to the SensorJobExecuter.
	 * 
	 * @param sensorJob
	 */
	protected void addLowPriorityJob(SensorJob sensorJob) {
		//TODO: can be Overridden to implement a priority
		addSensorJobToCircuitScheduler(sensorJob);
	}

	/**
	 * 
	 * @param sensorJob
	 */
	protected void addSensorJobToCircuitScheduler(SensorJob sensorJob){
		synchronized (this.circuitSchedulerList) {
			CircuitScheduler circuit = getCircuitScheduler(sensorJob.getMeterDSID());
			if(circuit != null){
				circuit.addSensorJob(sensorJob);
				synchronized(executer){
					executer.notifyAll();
				}
			} else{
				circuit = new CircuitScheduler(sensorJob);
				this.circuitSchedulerList.add(circuit);
			}
			if(circuit.getNextExecutionTime() <= System.currentTimeMillis()){
				executer.interrupt();
			}
		}
	}
	
	private CircuitScheduler getCircuitScheduler(DSID dsid){
		for(CircuitScheduler circuit : this.circuitSchedulerList){
			if (circuit.getMeterDSID().equals(dsid)){
				return circuit;
			}
		}
		return null;
	}
	
	/**
	 * Removes all SensorJobs of a specific ds-device.
	 * 
	 * @param dsid of the ds-device
	 */
	/*public void removeSensorJobs(DSID dsid) {
		Device device = this.dssBrideHandler.getDeviceByDSID(dsid.getValue());
		if(device != null){
			CircuitScheduler circuit = getCircuitScheduler(device.getMeterDSID());
			if(circuit != null){
				circuit.removeSensorJob(dsid);
			}	
		}		
	}
	*/
	public void removeSensorJobs(Device device) {
		//Device device = this.dssBrideHandler.getDeviceByDSID(dsid.getValue());
		if(device != null){
			CircuitScheduler circuit = getCircuitScheduler(device.getMeterDSID());
			if(circuit != null){
				circuit.removeSensorJob(device.getDSID());
			}	
		}		
	}
}
