package digitalSTROMManager.impl;

import java.net.HttpURLConnection;
import java.util.HashMap;

import digitalSTROMListener.DigitalSTROMConnectionListener;
import digitalSTROMManager.DigitalstromConnectionManager;
import digitalSTROMServerConnection.DigitalSTROMAPI;
import digitalSTROMServerConnection.HttpTransport;
import digitalSTROMServerConnection.impl.DigitalSTROMJSONImpl;
import digitalSTROMServerConnection.impl.HttpTransportImpl;

public class DigitalSTROMConnectionManagerImpl implements
		DigitalstromConnectionManager {

	public final String APPLICATION_TOKEN = "appT";
	public final String USER_NAME = "user";
	public final String PASSWORD = "pw";
	public final String HOST = "host";
	public final String APPLICATION_NAME = "ESH";
	
	private DigitalSTROMConnectionListener connListener= null;  
	
	private HttpTransport transport;
	private String sessionToken;
	private String applicationToken;
	private HashMap<String, String> configuration;
	
	private Boolean lastConnectionState = false;
	
	private boolean genAppToken = true;
	
	private DigitalSTROMAPI digitalSTROMClient;
	
	public DigitalSTROMConnectionManagerImpl(String host, int connectTimeout, int readTimeout, String username, String password, String applicationToken){
		init(host, connectTimeout, readTimeout, username, password, applicationToken);
	}
	
	public DigitalSTROMConnectionManagerImpl(String host, String username, String password, String applicationToken){
		init(host, -1, -1, username, password, applicationToken);
	}
	
	public DigitalSTROMConnectionManagerImpl(String host, String applicationToken){
		init(host, -1, -1, null, null, applicationToken);
	}
	
	public DigitalSTROMConnectionManagerImpl(String host, String username, String password){
		init(host, -1, -1, username, password, null);
	}
	
	public DigitalSTROMConnectionManagerImpl(String host, String username, String password, boolean genAppToken){
		this.genAppToken = genAppToken; 
		init(host, -1, -1, username, password, null);
	}
	
	private void init(String host, int connectTimeout, int readTimeout, String username, String password, String applicationToken){
		configuration = new HashMap<String, String>(4);
		configuration.put(this.HOST, host);
		configuration.put(this.APPLICATION_TOKEN, applicationToken);
		configuration.put(this.USER_NAME, username);
		configuration.put(this.PASSWORD, password);
		
		if(connectTimeout >= 0 || readTimeout >= 0){
			this.transport = new HttpTransportImpl(host, connectTimeout, readTimeout);
		} else{
			this.transport = new HttpTransportImpl(host);
		}
		
		this.digitalSTROMClient = new DigitalSTROMJSONImpl(transport);
		if(this.genAppToken){
			this.onNotAuthentificated();
		}
	}
	
	@Override
	public HttpTransport getHttpTransport() {
		return transport;
	}

	@Override
	public DigitalSTROMAPI getDigitalSTROMAPI() {
		return this.digitalSTROMClient;
	}


	@Override
	public String getSessionToken() {
		return this.sessionToken;
	}

	@Override
	public String checkConnectionAndGetSessionToken() {
		if(checkConnection()){
			return this.sessionToken;
		} 
		return null;
	}

	
	@Override
	public synchronized boolean checkConnection(){
		switch(this.digitalSTROMClient.checkConnection(sessionToken)) {
			case HttpURLConnection.HTTP_OK:
				if(!lastConnectionState){ 
					System.err.println("Connection to DigitalSTROM-Server established.");
					lastConnectionState = true;
					onConnectionResumed();
				}
				break;
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				System.out.println("DigitalSTROM server  {} send HTTPStatus {}"+ this.getConfig().get(HOST) + HttpURLConnection.HTTP_UNAUTHORIZED);
				lastConnectionState = false;
				break;
			case HttpURLConnection.HTTP_FORBIDDEN:
				if(this.genAppToken){
					sessionToken = this.digitalSTROMClient.loginApplication(applicationToken);
				} else {
					sessionToken = this.digitalSTROMClient.login(this.configuration.get(USER_NAME), this.configuration.get(PASSWORD));
				}
				if(this.digitalSTROMClient.checkConnection(sessionToken) == HttpURLConnection.HTTP_OK){
					if(!lastConnectionState){
						System.err.println("Connection to DigitalSTROM-Server established.");
						onConnectionResumed();
						lastConnectionState = true;
					}
				} else{
					if(this.genAppToken){
						onNotAuthentificated();
					}
					
					lastConnectionState = false;
				}
				break;
			case -1:
			case HttpURLConnection.HTTP_NOT_FOUND:
				System.err.println("Server not found! Please check this points:\n"
						+ " - DigitalSTROM-Server turned on?\n"
						+ " - hostadress correct?\n"
						+ " - ethernet cable connection established?");
				onConnectionLost();
				
				lastConnectionState = false;
				break;
			case -2:
				System.err.println("Invalide URL!");
				lastConnectionState = false;
				break;
		}
		return lastConnectionState;
	}
	
	public HashMap<String, String> getConfig(){
		return this.configuration;
	}
	 /**
     * This method is called whenever the connection to the digitalSTROM-Server is available,
     * but requests are not allowed due to a missing or invalid authentication.
     */
	private void onNotAuthentificated(){
				
		String applicationToken;
		//String sessionToken;
		//Configuration configuration = getConfig();
		
		boolean isAutentificated = false;
		
		System.out.println("DigitalSTROM server {} is not authentificated - please set a applicationToken or username and password." + configuration.get(HOST));

		if(configuration.get(APPLICATION_TOKEN) != null && 
				!(applicationToken = configuration.get(APPLICATION_TOKEN).toString()).isEmpty()){
			sessionToken = digitalSTROMClient.loginApplication(applicationToken);
			if(digitalSTROMClient.checkConnection(sessionToken) == HttpURLConnection.HTTP_OK) {
				System.out.println("User defined Applicationtoken can be used.");
				isAutentificated = true;
			} else{
				//updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "User defined Applicationtoken is wrong.");
				System.out.println("User defined Applicationtoken can't be used.");
				if(connListener != null){
					connListener.onConnectionStateChange(DigitalSTROMConnectionListener.NOT_AUTHENTICATED, DigitalSTROMConnectionListener.WRONG_APP_TOKEN);
				}
			}
		} else{
			System.out.println("Can't find Appicationtoken.");
		}
		if(checkUserPassword(configuration)){
			if(!isAutentificated){
				System.out.println("Generating Applicationtoken with user and password.");
				
				//generate applicationToken and test host is reachable
				applicationToken = this.digitalSTROMClient.requestAppplicationToken(APPLICATION_NAME);
							
				if(applicationToken != null && !applicationToken.trim().isEmpty()){
					//enable applicationToken
					sessionToken = this.digitalSTROMClient.login(
							configuration.get(USER_NAME).toString(), 
							configuration.get(PASSWORD).toString());
					//System.err.println("SessionToken: {}, applicationToken: {}", sessionToken, applicationToken);
					if(this.digitalSTROMClient.enableApplicationToken(applicationToken, sessionToken)){
						configuration.put(APPLICATION_TOKEN, applicationToken);
						this.applicationToken = applicationToken;
						isAutentificated = true;
						
						System.err.println("Applicationtoken generated and added successfull to DigitalSTROM Server.");
					} else {
						//updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "The set username or password is wrong.");
						System.err.println("Incorrect Username or password. Can't enable Applicationtoken.");
						if(connListener != null){
							connListener.onConnectionStateChange(DigitalSTROMConnectionListener.NOT_AUTHENTICATED, DigitalSTROMConnectionListener.WRONG_USER_OR_PASSWORD);
						}
					}
				}
			}
			
			//remove password and username, to don't store them persistently   
			if(isAutentificated){
				configuration.remove(PASSWORD);
				configuration.remove(USER_NAME);
			}
		} else 
			if(!isAutentificated){
				//updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "no username or password is set to genarate Appicationtoken.");
				System.out.println("Can't find Username or password to genarate Appicationtoken.");
				
				if(connListener != null){
					connListener.onConnectionStateChange(DigitalSTROMConnectionListener.NOT_AUTHENTICATED, DigitalSTROMConnectionListener.NO_USER_PASSWORD);
				}
			}
	}
	
	
	private boolean checkUserPassword(HashMap<String, String> configuration){
		if((configuration.get(USER_NAME) != null && configuration.get(PASSWORD) != null) &&
			(!configuration.get(USER_NAME).toString().isEmpty() && !configuration.get(PASSWORD).toString().isEmpty()))//notwendig? 
			return true;
		return false;
	}
	
    /**
     * This method is called whenever the connection to the DigitalSTROM-Server is lost.
     */
	private void onConnectionLost() {
        System.err.println("DigitalSTROM-Server connection lost. Updating thing status to OFFLINE.");
        if(connListener != null){
        	connListener.onConnectionStateChange(DigitalSTROMConnectionListener.CONNECTION_LOST);
        }
        //in Connlistener 
        /*
         * updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "The connection to the digitalSTROM-Server can't established");
         * DigitalSTROMDeviceStatusManager devStatMan.stop();
         * */
    }

  
    /**
     * This method is called whenever the connection to the DigitalSTROM-Server is resumed.
     */
	private void onConnectionResumed() {
       System.err.println("DigitalSTROM-Server connection resumed. Updating thing status to ONLINE.");
       if(connListener != null){
    	   connListener.onConnectionStateChange(DigitalSTROMConnectionListener.CONNECTION_RESUMED);
       }
    }

	@Override
	public void registerConnectionListener(DigitalSTROMConnectionListener listener) {
		this.connListener = listener;
		
	}

	@Override
	public void unregisterConnectionListener() {
		this.connListener = null;		
	}

	@Override
	public String getApplicationToken() {
		return this.applicationToken;
	}
}
