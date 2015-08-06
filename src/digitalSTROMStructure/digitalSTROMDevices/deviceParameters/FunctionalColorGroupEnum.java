/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package digitalSTROMStructure.digitalSTROMDevices.deviceParameters;


import java.util.HashMap;

/**
 * The {@link FunctionalColorGroupEnum} contains all DigitalSTROM functional color groups.
 * 
 * @see http://developer.digitalstrom.org/Architecture/ds-basics.pdf, "Table 1: digitalSTROM functional groups and their colors", page 9
 * @author Michael Ochel - Initial contribution
 * @author Matthias Siegele - Initial contribution
 */
public enum FunctionalColorGroupEnum {
	/*
	 * | Number	| Name		| Color		| Function								|
	 * --------------------------------------------------------------------------
	 * | 1		| Lights	| Yellow	| Room lights							|
	 * | 2 		| Blinds 	| Gray		| Blinds or shades outside				|
	 * | 3		| Climate	| Blue		| Heating or cooling					|
	 * | 4		| Audio		| Cyan		| Playing music or radio				|
	 * | 5 		| Video		| Magenta	| TV, Video								|
	 * | 8		| Joker		| Black		| Configurable behaviour				|
	 * | n/a	| Security	| Red		| Security related functions, Alarms	|
	 * | n/a	| Access	| Green		| Access related functions, door bell	|
	 */
	YELLOW		(1),
	GREY		(2),
	BLUE		(3),
	CYAN		(4),
	MAGENTA		(5),
	BLACK		(8),
	RED			(-1),
	GREEN		(-1);	
	
	private final int	colorGroup;
	
	static final HashMap<Integer, FunctionalColorGroupEnum> colorGroups = new HashMap<Integer, FunctionalColorGroupEnum>();
	
	static {
		for (FunctionalColorGroupEnum colorGroup:FunctionalColorGroupEnum.values()) {
			colorGroups.put(colorGroup.getFunctionalColorGroup(), colorGroup);
		}
	}
	
	/**
	 * Returns true if contains the given output mode id in DigitalSTROM, otherwise false.
	 * 
	 * @param colorGroup
	 * @return true if contains
	 */
	public static boolean containsColorGroup(Integer functionalColorGroupID) {
		return colorGroups.keySet().contains(functionalColorGroupID);
	}
	
	/**
	 * Returns the {@link FunctionalColorGroupEnum} of the given mode id.
	 * 
	 * @param modeID
	 * @return mode
	 */
	public static FunctionalColorGroupEnum getMode(Integer functionalColorGroupID) {
		return colorGroups.get(functionalColorGroupID);
	}
	
	private FunctionalColorGroupEnum(int functionalColorGroupID) {
		this.colorGroup = functionalColorGroupID;
	}
	
	/**
	 * Returns the functional color group form this Object.
	 * 
	 * @return functional color group id
	 */
	public int getFunctionalColorGroup() {
		return colorGroup;
	}

}
