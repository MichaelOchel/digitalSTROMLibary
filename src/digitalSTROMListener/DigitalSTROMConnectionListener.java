package digitalSTROMListener;

/**
 * The {@link DigitalSTROMConnectionListener} is notified when the connection state of digitalSTROM-Server has changed.
 * 
 * @author Michael Ochel - Initial contribution
 * @author Mathias Siegele - Initial contribution
 *
 */
public interface DigitalSTROMConnectionListener {

	/* Connection-States */
	
	/**
	 * State if we'r not authenticated on the digitalSTROM-Server.
	 */
	public final String NOT_AUTHENTICATED = "notAuth";
	/**
	 * State if the connection to the digitalSTROM-Server is lost.
	 */
	public final String CONNECTION_LOST = "connLost";
	/**
	 * State if the connection to the digitalSTROM-Server is resumed.
	 */
	public final String CONNECTION_RESUMED = "connResumed";
	
	/* Not authentication reasons */
	
	/**
	 * State if the given application-token can't be used.
	 */
	public final String WRONG_APP_TOKEN = "wrongAppT";
	/**
	 * State if the given username or password can't be used.
	 */
	public final String WRONG_USER_OR_PASSWORD = "wrongUserOrPasswd";
	/**
	 * State if no username or password is set and the given application-token can't be used.
	 */
	public final String NO_USER_PASSWORD = "noUserPasswd";
	
	/**
	 * This method is called when ever the connection state has changed from {@link #CONNECTION_LOST}
	 * to {@link #CONNECTION_RESUMED} and vice versa.
	 * 
	 * @param newConnectionState
	 */
	public void onConnectionStateChange(String newConnectionState);
	
	/**
	 * This method is called when ever the connection state has changed to {@link #NOT_AUTHENTICATED} 
	 * and also passes the reason why. Reason can be:
	 * 	<ul>
	 * 	<li>{@link #WRONG_APP_TOKEN} if the given application-token can't be used.</li> 
	 * 	<li>{@link #WRONG_USER_OR_PASSWORD} if the given username or password can't be used.</li>
	 * 	<li>{@link #NO_USER_PASSWORD} if no username or password is set and the given application-token can't be used.</li>
	 * </ul>
	 * 
	 * @param newConnectionState
	 * @param reason
	 */
	public void onConnectionStateChange(String newConnectionState, String reason);
}
