package DSServerConnection;

public interface DigitalSTROMConnectionListener {

	/**
	 * States
	 */
	public final String NOT_AUTHENTIFICATED = "notAuth";
	public final String CONNECTION_LOST = "connLost";
	public final String CONNECTION_RESUMED = "connResumed";
	
	/**
	 * Reasons
	 */
	public final String WRONG_APP_TOKEN = "wrongAppT";
	public final String WRONG_USER_OR_PASSWORD = "wrongUserOrPasswd";
	public final String NO_USER_PASSWORD = "noUserPasswd";
	
	public void connectionStateChange(String newState);
	
	public void connectionStateChange(String newState, String reason);
}
