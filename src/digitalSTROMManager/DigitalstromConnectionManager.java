package digitalSTROMManager;

import digitalSTROMListener.DigitalSTROMConnectionListener;
import digitalSTROMServerConnection.DigitalSTROMAPI;
import digitalSTROMServerConnection.HttpTransport;

//Therefor he create the --- and provide ???

/**
 * The {@link DigitalstromConnectionManager} manage the connection to a digitalSTROM-Server.    
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DigitalstromConnectionManager {

	/**
	 * Return the {@link HttpTransport} to execute queries or special commands on the digitalSTROM-Server.
	 * 
	 * @return
	 */
	public HttpTransport getHttpTransport();
	
	/**
	 * Return the {@link DigitalSTROMAPI} to execute commands on the digitalSTROM-Server. 
	 * 
	 * @return
	 */
	public DigitalSTROMAPI getDigitalSTROMAPI();
	
	/**
	 * This method has to be called before each command to check the connection to the digitalSTROM-Server.
	 * It examines the connection to the server, sets a new session token if it is expired and sets a new ApplicationToken, 
	 * if none it set at the DigitalSTROM-Server. It also outputs the specific connection failure. 
	 * 
	 * @return true if the connection is established and false if not 
	 */
	public boolean checkConnection();
	
	/**
	 * Returns the current session-token.
	 * 
	 * @return session-token
	 */
	public String getSessionToken();
	
	/**
	 * Return the auto-generated or user defined application-token.
	 * 
	 * @return application-token
	 */
	public String getApplicationToken();
	
	/**
	 * Checks the connection with {@link DigitalstromConnectionManager#checkConnection()} and returns the current session-token. 
	 * 
	 * @return session-token
	 */
	public String checkConnectionAndGetSessionToken();
	
	/**
	 * Register a {@link DigitalSTROMConnectionListener} to this {@link DigitalstromConnectionManager}.
	 * 
	 * @param connectionListener
	 */
	public void registerConnectionListener(DigitalSTROMConnectionListener connectionListener);
	
	/**
	 * Unregister the {@link DigitalSTROMConnectionListener} from this {@link DigitalstromConnectionManager}.
	 */
	public void unregisterConnectionListener();
	
}
