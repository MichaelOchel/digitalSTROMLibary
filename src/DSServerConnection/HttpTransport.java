package DSServerConnection;
/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */







/**
 * The {@link HttpTransport} executes an request to the DigitalSTROM-Server. 
 * 
 * @author Michael Ochel -  add SSL-Certification check, add fixURI(String uri) and  checkConnection(String testRequest) method
 * @author Matthias Siegele -  add SSL-Certification check, add fixURI(String uri) and  checkConnection(String testRequest) method
 */
public interface HttpTransport {
	
	/**
	 * Executes a DigitalSTROM-request.
	 * 
	 * @param request
	 * @return response
	 */
	public String execute(String request);
	
	/**
	 * Executes a DigitalSTROM-request.
	 * 
	 * @param request
	 * @param connectTimeout
	 * @param readTimeout
	 * @return response
	 */
	public String execute(String request, int connectTimeout, int readTimeout);
	
	/**
	 * Executes a DigitalSTROM test request and returns the HTTP-Code.
	 * 
	 * @param testRequest
	 * @return HTTP-Code
	 */
	public int checkConnection(String testRequest) ;
	
}