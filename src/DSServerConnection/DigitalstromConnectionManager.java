package DSServerConnection;

public interface DigitalstromConnectionManager {

	public HttpTransport getHttpTransport();
	
	public DigitalSTROMAPI getDigitalSTROMAPI();
	
	/**
	 * This method has to be called before each command to check the connection to the digitalSTROM-Server.
	 * It examines the connection to the server, sets a new session token if it is expired and sets a new ApplicationToken, 
	 * if none it set at the DigitalSTROM-Server. It also outputs the specific connection failure. 
	 * 
	 * @return true if the connection is established and false if not 
	 */
	public boolean checkConnection();
	
	public String getSessionToken();
	
	public String getApplicationToken();
	
	public String checkConnectionAndGetSessionToken();
	
	public void registerConnectionListener(DigitalSTROMConnectionListener listener);
	
	public void unregisterConnectionListener();
}
