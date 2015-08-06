package Scenes.SceneHandler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import DSServerConnection.DigitalSTROMAPI;
import DSServerConnection.DigitalstromConnectionManager;
import DSServerConnection.HttpTransport;
import DSServerConnection.Costants.JSONApiResponseKeysEnum;
import DSServerConnection.Costants.JSONRequestConstants;
import DSServerConnection.impl.JSONResponseHandler;
import GeneralConstants.DigitalSTROMConfig;
import Scenes.Scene.SceneManager;

/**
 * If someone turns a device or a zone etc. on, we will get a notification
 * to update the state of the item
 * 
 * @author Alexander Betker
 * @since 1.3.0
 * 
 */
public class EventListener extends Thread {
//
	//private Logger logger = LoggerFactory.getLogger(DigitalSTROMEventListener.class);

	
	
	private boolean shutdown = false;
	private final String EVENT_NAME = DigitalSTROMConfig.EVENT_NAME;
	private final int ID = 11;
	private final DigitalstromConnectionManager connManager;

	
	//private int timeout = 1000;

	private final String INVALID_SESSION = "Invalid session!";// Invalid
																// session!

	/** Mapping digitalSTROM-Scene to digitalSTROM-State */
	//private SceneToStateMapper stateMapper = new SceneToStateMapper();
	
	private HttpTransport transport = null;
	//private JSONResponseHandler handler = null;
	private DigitalSTROMAPI digitalSTROM;
	SceneManager sceneManager;

	public synchronized void shutdown() {
		this.shutdown = true;
		//this.sensorJobExecutor.shutdown();
		unsubscribe();
	}
	
	public synchronized void wakeUp(){
		this.shutdown = false;
		this.subscribe();
		this.run();
	}

	public EventListener(DigitalstromConnectionManager connectionManager, SceneManager sceneManager) {
		this.transport = connectionManager.getHttpTransport();
		this.digitalSTROM = connectionManager.getDigitalSTROMAPI();
		this.connManager = connectionManager;
		this.sceneManager = sceneManager;
		
		this.subscribe();
	}

	private void subscribe() {
		if (connManager.checkConnection()) {

			boolean transmitted = digitalSTROM.subscribeEvent(
					this.connManager.getSessionToken(), 
					EVENT_NAME, 
					this.ID,
					DigitalSTROMConfig.DEFAULT_CONNECTION_TIMEOUT,
					DigitalSTROMConfig.DEFAULT_READ_TIMEOUT);
			
			if (!transmitted) {
				this.shutdown = true;
				System.err.println("Couldn't subscribe eventListener ... maybe timeout because system is to busy ...");
			}else{
				System.out.println("subscribe successfull");
			}
		} else {
			System.err.println("Couldn't subscribe eventListener because there is no token (no connection)");
		}
	}

	@Override
	public void run() {
		//this.sensorJobExecutor = new SensorJobExecutor(digitalSTROM, connManager);
		//sensorJobExecutor.start();
		System.out.println("DigitalSTROMEventListener startet");
		while (!this.shutdown) {
			
			String request = this.getEventAsRequest(this.ID, 500);

			if (request != null) {
				//System.out.println("läuuuuft");
				String response = this.transport.execute(request);

				JSONObject responseObj = JSONResponseHandler
						.toJSONObject(response);

				if (JSONResponseHandler.checkResponse(responseObj)) {
					JSONObject obj = JSONResponseHandler
							.getResultJSONObject(responseObj);

					if (obj != null
							&& obj.get(JSONApiResponseKeysEnum.EVENT_GET_EVENT
									.getKey()) instanceof JSONArray) {
						JSONArray array = (JSONArray) obj
								.get(JSONApiResponseKeysEnum.EVENT_GET_EVENT
										.getKey());
						try {
							handleEvent(array);
						} catch (Exception e) {
							System.out.println("EXCEPTION in eventListener thread : "
									+ e.getLocalizedMessage());
						}
					}
				} else {
					String errorStr = null;
					if (responseObj != null
							&& responseObj
									.get(JSONApiResponseKeysEnum.EVENT_GET_EVENT_ERROR
											.getKey()) != null) {
						errorStr = responseObj
								.get(JSONApiResponseKeysEnum.EVENT_GET_EVENT_ERROR
										.getKey()).toString();
					}

					if (errorStr != null
							&& errorStr.equals(this.INVALID_SESSION)) {
						this.subscribe();
					} else if (errorStr != null) {
						System.err.println("Unknown error message in event response: "
								+ errorStr);
					}
				}
			}
		}
		//sensorJobExecutor.shutdown();
	}

	private String getEventAsRequest(int subscriptionID, int timeout) {
		if (connManager.checkConnection()) {
			return JSONRequestConstants.JSON_EVENT_GET
					+ JSONRequestConstants.PARAMETER_TOKEN
					+ connManager.getSessionToken()
					+ JSONRequestConstants.INFIX_PARAMETER_SUBSCRIPTION_ID
					+ subscriptionID
					+ JSONRequestConstants.INFIX_PARAMETER_TIMEOUT
					+ timeout;
		}
		return null;
	}

	private boolean unsubscribeEvent(String name, int subscriptionID) {
		if (connManager.checkConnection()) {
			return digitalSTROM.unsubscribeEvent(connManager.getSessionToken(),
					EVENT_NAME, 
					this.ID,
					DigitalSTROMConfig.DEFAULT_CONNECTION_TIMEOUT,
					DigitalSTROMConfig.DEFAULT_READ_TIMEOUT);
		}
		return false;
	}

	private boolean unsubscribe() {
		return this.unsubscribeEvent(this.EVENT_NAME, this.ID);
	}

	private void handleEvent(JSONArray array) {
		if (array.size() > 0) {
			Event event = new JSONEventImpl(array);

			for (EventItem item : event.getEventItems()) {
				if (item.getName() != null
						&& item.getName().contains(this.EVENT_NAME)) {
					System.out.println(item.getName());
					this.sceneManager.handleEvent(item);
				}
			}
		}
	}
	
	
}
