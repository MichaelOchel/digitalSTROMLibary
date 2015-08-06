package DigitalSTROMDevices.DeviceParameters;
/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.HashMap;

/**
 * @author 	Alexander Betker
 * @since 1.3.0
 * @version	digitalSTROM-API 1.14.5
 */
public enum OutputModeEnum {
	
	/*
	 * | Output Mode	| Description												|
	 * ------------------------------------------------------------------------------
	 * | 0	 			| No output or output disabled								|
	 * | 16 			| Switched													|
	 * | 17 			| RMS (root mean square) dimmer								|
	 * | 18 			| RMS dimmer with characteristic curve						|
	 * | 19				| Phase control dimmer										|
	 * | 20 			| Phase control dimmer with characteristic curve			|
	 * | 21				| Reverse phase control dimmer								|
	 * | 22				| Reverse phase control dimmer with characteristic curve	|
	 * | 23				| PWM (pulse width modulation)								|
	 * | 24				| PWM with characteristic curve								|
	 * | 33				| Positioning control										|
	 * | 39				| Relay with switched mode scene table configuration		|
	 * | 40				| Relay with wiped mode scene table configuration			|
	 * | 41				| Relay with saving mode scene table configuration			|
	 * | 42				| Positioning control for uncalibrated shutter				|
	 */
	DISABLED		(0),
	SWITCHED		(16),
	RMS_DIMMER		(17),
	RMS_DIMMER_CC	(18),
	PC_DIMMER		(19),
	PC_DIMMER_CC	(20),
	RPC_DIMMER		(21),
	RPC_DIMMER_CC	(22),
	PWM				(23),
	PWM_CC			(24),
	POSITION_CON	(33),
	SWITCH			(39),
	WIPE 			(40),
	POWERSAVE		(41),
	POSITION_CON_US (42);
	
	private final int	mode;
	
	static final HashMap<Integer, OutputModeEnum> outputModes = new HashMap<Integer, OutputModeEnum>();
	
	static {
		for (OutputModeEnum out:OutputModeEnum.values()) {
			outputModes.put(out.getMode(), out);
		}
	}
	
	public static boolean containsMode(Integer mode) {
		return outputModes.keySet().contains(mode);
	}
	
	public static OutputModeEnum getMode(Integer mode) {
		return outputModes.get(mode);
	}
	
	private OutputModeEnum(int outputMode) {
		this.mode = outputMode;
	}
	
	public int getMode() {
		return mode;
	}

}
