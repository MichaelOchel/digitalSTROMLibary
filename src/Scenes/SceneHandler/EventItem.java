package Scenes.SceneHandler;

/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


import java.util.Map;


/**
 * The {@link EventItem} represent a event item of an DigitalSTROM-Event.
 * 
 * @author 	Alexander Betker
 * @since 1.3.0
 */
public interface EventItem {
	
	/**
	 * Returns the name of this {@link EventItem}.
	 * 
	 * @return name of this {@link EventItem}
	 */
	public String getName();
	
	/**
	 * Returns {@link HashMap} with the properties of this {@link EventItem}.
	 * The key is a {@link EventPropertyEnum} and represent the property name 
	 * and the value is the property value.  
	 * 
	 * @return the properties of this {@link EventItem}
	 */
	public Map<Scenes.NamedScene.Constants.EventPropertyEnum, String> getProperties();

}
