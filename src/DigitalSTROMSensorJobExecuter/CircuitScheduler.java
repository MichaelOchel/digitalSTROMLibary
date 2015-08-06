package DigitalSTROMSensorJobExecuter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import DigitalSTROMDevices.DeviceParameters.DSID;
import DigitalSTROMSensorJobExecuter.SensorJob.SensorJob;
import GeneralConstants.DigitalSTROMConfig;



/**
 * This {@link CircuitScheduler} represents a circuit in DigitalSTROM and manages the priorities and execution times for the 
 * {@link SensorJob}s to be executed on this circuit.
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public class CircuitScheduler {
	
	private class SensorJobComparator implements Comparator<SensorJob>{

		@Override
		public int compare(SensorJob job1, SensorJob job2) {
			return ((Long) job1.getInitalisationTime()).compareTo(job2.getInitalisationTime());
		}
		
	}
	
	private final DSID meterDSID;
	private long nextExecutionTime = System.currentTimeMillis();
	private PriorityQueue<SensorJob> sensorJobQueue = new PriorityQueue<SensorJob>(10, new SensorJobComparator());
	
	//private Logger logger = LoggerFactory.getLogger(CircuitScheduler.class);
	
	/**
	 * Creates a new {@link CircuitScheduler}.
	 * 
	 * @param meterDSID
	 */
	public CircuitScheduler(DSID meterDSID){
		if(meterDSID == null) throw new IllegalArgumentException("The meterDSID must not be null!");
		this.meterDSID = meterDSID;
	}
	
	/**
	 * Creates a new {@link CircuitScheduler} and add the first {@link SensorJob} to this {@link CircuitScheduler}.
	 * 
	 * @param sensorJob
	 */
	public CircuitScheduler(SensorJob sensorJob){
		this.meterDSID = sensorJob.getMeterDSID();
		this.sensorJobQueue.add(sensorJob);
		System.out.println("create circuitScheduler: "+ this.getMeterDSID() +" and add sensorJob: " + sensorJob.getDsid().toString());
	}
	
	/**
	 * Returns the ds-Meter-ID of the ds-Meter in which the jobs are to be executed.  
	 * 
	 * @return ds-Meter-ID
	 */
	public DSID getMeterDSID(){
		return this.meterDSID;
	}
	
	/**
	 * Adds a new SensorJob to this {@link CircuitScheduler}.
	 * 
	 * @param sensorJob
	 */
	public void addSensorJob(SensorJob sensorJob){
		synchronized(sensorJobQueue){
			if(!this.sensorJobQueue.contains(sensorJob)){
				sensorJobQueue.add(sensorJob);
				System.out.println("add sensorJob: " + sensorJob.toString() + "to circuitScheduler: " + this.getMeterDSID());
			} else if(checkSensorJobPrio(sensorJob)){
				System.out.println("add sensorJob: " + sensorJob.toString() + "with higher priority to circuitScheduler: " + this.getMeterDSID());
			} else {
				System.out.println("sensorJob: " + sensorJob.getDsid().toString() + " allready exist with a higher priority");
			}
		}
	}
	
	private boolean checkSensorJobPrio(SensorJob sensorJob){
		synchronized(sensorJobQueue){
			for (Iterator<SensorJob> iter = sensorJobQueue.iterator(); iter
					.hasNext();) {
				SensorJob existSensorJob = iter.next();
				if (existSensorJob.equals(sensorJob))
					if(existSensorJob != null && sensorJob.getInitalisationTime() < existSensorJob.getInitalisationTime()){
						iter.remove();
						sensorJobQueue.add(sensorJob);
						return true;
					}
			}
		
			//System.out.println("Remove SensorJobs from device with DSID {}."+ dsid);
		}
		return false;
		/*
		SensorJob[] sensorJobs = (SensorJob[]) this.sensorJobQueue.toArray();
		for(int i = 0 ; i < sensorJobs.length ; i++ ){
			if(sensorJobs[i].equals(sensorJobs)){
				return sensorJobs[i];
			}
		}
		return null;*/
	}
	/**
	 * Returns the next {@link SensorJob} which can be executed or null if there are no more SensorJob to execute 
	 * or the wait time between {@link SensorJob}s executions has not yet expired. 
	 * 
	 * @return next SensorJob or null
	 */
	public SensorJob getNextSensorJob(){
		synchronized(sensorJobQueue){
			if(sensorJobQueue.peek() != null && this.nextExecutionTime <= System.currentTimeMillis()){
				nextExecutionTime = System.currentTimeMillis() + DigitalSTROMConfig.SENSOR_READING_WAIT_TIME;
				return sensorJobQueue.poll();
			} else return null;
		}
	}
	
	/**
	 * Returns the time when the next {@link SensorJob} can be executed.
	 * 
	 * @return next SesnorJob execution time 
	 */
	public Long getNextExecutionTime(){
		return this.nextExecutionTime;
	}
	
	/**
	 * Remove all {@link SensorJob} of a specific ds-device.
	 * 
	 * @param dsid of the ds-device
	 */
	public void removeSensorJob(DSID dsid){
		synchronized(sensorJobQueue){
			for (Iterator<SensorJob> iter = sensorJobQueue.iterator(); iter
					.hasNext();) {
				SensorJob job = iter.next();
				if (job.getDsid().equals(dsid))
					iter.remove();
			}
		
			System.out.println("Remove SensorJobs from device with DSID {}."+ dsid);
		}
	}
	
	/**
	 * Returns true if there are no more {@link SensorJob}s to execute, otherwise false.
	 * 
	 * @return no more SensorJobs (true | false)
	 */
	public boolean noMoreJobs(){
		synchronized(sensorJobQueue){
			return this.sensorJobQueue.isEmpty();
		}
	}
	
	
}
