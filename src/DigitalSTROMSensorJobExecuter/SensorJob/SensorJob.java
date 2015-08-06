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
import DigitalSTROMDevices.DeviceParameters.DSID;





/**
 * Interface for a DigitalSTROM-Sensor-Jobs.
 * 
 * @author Alexander Betker - initial 
 * @author Alex Maier
 * @since 1.3.0
 * 
 * @author Michael Ochel
 * @author Matthias Siegele
 */
public interface SensorJob {
	
	/**
	 * Returns the dSID of the ds-Device in which this job is to be executed.
	 *  
	 * @return dSID from the device
	 */
	public DSID getDsid();
	
	/**
	 * Returns the dSID of the ds-Meter in which this job is to be executed.
	 * 
	 * @return
	 */
	public DSID getMeterDSID();
	
	/**
	 * Executes the SensorJob.
	 * 
	 * @param digitalSTROM client
	 * @param sessionToken
	 */
	public void execute(DigitalSTROMAPI digitalSTROM, String sessionToken);
	
	/**
	 * Returns the time when the Sensor-Job was initialized.
	 * 
	 * @return
	 */
	public long getInitalisationTime();
	
	/**
	 * Sets the time when the Sensor-Job was initialized e.g. to decrease the priority of this job.
	 * 
	 * @param time
	 */
	public void setInitalisationTime(long time);
}
