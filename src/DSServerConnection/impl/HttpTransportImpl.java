package DSServerConnection.impl;
/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import DSServerConnection.HttpTransport;
import GeneralConstants.DigitalSTROMConfig;




/**
 * The {@link HttpTransportImpl} executes an request to the DigitalSTROM-Server. 
 * It also add the SLL-Certification if it is set to the DigialSTROM-Server-Thing. 
 * 
 * @author Alexander Betker - Initial contribution
 * @author Alex Maier - Initial contribution
 * @author Michael Ochel -  add SSL-Certification check, add fixURI(String uri) and  checkConnection(String testRequest) method
 * @author Matthias Siegele -  add SSL-Certification check, add fixURI(String uri) and  checkConnection(String testRequest) method
 */
public class HttpTransportImpl implements HttpTransport {
	
	//Exceptions:
	//Messages
	private final String NO_CERT_AT_PATH = "Missing input stream";
	private final String CERT_EXCEPTION = "java.security.cert.CertificateException";
	//sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	private final String PKIX_PATH_FAILD = "PKIX path building failed";
	private final String HOSTNAME_VERIFIER = "No name matching";
	private final String HOSTNAME_WRONG = "HTTPS hostname wrong";
	private final String UNKOWN_HOST = "unknownHost";
	
	//private static final Logger logger = LoggerFactory.getLogger(HttpTransport.class);
	
	private String uri;
	private String trustedCertPath;
	private X509Certificate	trustedCert;
	private InputStream certInputStream;
	
	private int connectTimeout;
	private int readTimeout;

	private final int DEFAULT_CONNECTION_TIMEOUT = 1000;
	private final int DEFAULT_READ_TIMEOUT = 1000;
	
	public HttpTransportImpl(String uri) {
		init(uri, this.DEFAULT_CONNECTION_TIMEOUT, this.DEFAULT_READ_TIMEOUT);
	}
	
	/**
	 * Creates a new {@link HttpTransportImpl}.
	 * 
	 * @param uri of the DigitalSTROM-Server
	 * @param connectTimeout
	 * @param readTimeout
	 */
	public HttpTransportImpl(String uri, int connectTimeout, int readTimeout) {
		init(uri, connectTimeout, readTimeout);
	}

	private void init(String uri, int connectTimeout, int readTimeout) {
		this.uri = fixURI(uri);
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		
		//Check SSL Certificate
		if(DigitalSTROMConfig.TRUST_CERT_PATH != null){			
			System.out.println("Certification path is set, generate the certification and accept it.");
			trustedCertPath = DigitalSTROMConfig.TRUST_CERT_PATH;
			
			File dssCert = new File(trustedCertPath);
			if(dssCert.isAbsolute()){
				try {
					certInputStream = new FileInputStream(dssCert);
				} catch (FileNotFoundException e) {
					System.err.println("Can't find a certificationfile at the certificationpath: " + trustedCertPath + 
							"\nPlease check the path!");
				}
			} else{
				certInputStream = HttpTransportImpl.class.getClassLoader().getResourceAsStream(trustedCertPath);
			}
			setupWithCertPath();
			String conCheck = this.checkConnection();
			if(conCheck != null && conCheck.contains(CERT_EXCEPTION)){
				System.err.println("Invalid certification at path " + this.trustedCertPath);
				
				
			}
			
		}
		
		checkSSLCert();
	}
	
	private String fixURI(String uri){
		if(!uri.startsWith("https://")) uri = "https://" + uri;
		if(uri.split(":").length != 3) uri = uri + ":8080";
		return uri;
	}
	
	private String fixRequest(String request){
		return request.replace(" ", ""); 
	}
	
	@Override
	public String execute(String request) {
		return execute(request, this.connectTimeout, this.readTimeout );
	}
	
	@Override
	public String execute(String request, int connectTimeout, int readTimeout) {
		request = fixRequest(request);
		if (request != null && !request.trim().equals("")) {
			
			HttpsURLConnection connection = null;
			
			StringBuilder response = new StringBuilder();
			BufferedReader in = null;
			try {
				URL url = new URL(this.uri+request);
				
				connection = (HttpsURLConnection) url.openConnection();
				int responseCode =-1;
				if (connection != null) {
					connection.setConnectTimeout(connectTimeout);
					connection.setReadTimeout(readTimeout);
					
				
					try {
						connection.connect();
						responseCode = connection.getResponseCode();
					} catch (SocketTimeoutException e) {
						System.out.println(e.getMessage()+" : "+request);
						return null;
					}
					
					if (responseCode == HttpURLConnection.HTTP_OK) {
						String inputLine = null;
						in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						
						in.close();
					}
					else {
						response = null;
					}
				
				}
				if (response != null) {
					return response.toString();
				}

			} catch (MalformedURLException e) {
				System.err.println("MalformedURLException by executing jsonRequest: "
						+ request +" ; "+e.getLocalizedMessage());

			} catch (IOException e) {
				System.err.println("IOException by executing jsonRequest: "
						+ request +" ; "+e.getLocalizedMessage());							
			}
			finally{
				if (connection != null)
					connection.disconnect();
			}
		}
		return null;
	}
	
	@Override
	public int checkConnection(String testRequest) {
		if (testRequest != null && !testRequest.trim().equals("")) {
			
			HttpURLConnection connection = null;
			
			try {
				URL url = new URL(this.uri+testRequest);
				
				connection = (HttpsURLConnection) url.openConnection();

				if (connection != null) {
					connection.setConnectTimeout(connectTimeout);
					connection.setReadTimeout(readTimeout);
				
					try {
						connection.connect();
						return connection.getResponseCode();
					} catch (SocketTimeoutException e) {
						System.out.println(e.getMessage()+" : "+testRequest);
						return -1;
					}			
				}

			} catch (MalformedURLException e) {
				System.err.println("MalformedURLException by executing jsonRequest: "
						+ testRequest +" ; "+e.getLocalizedMessage());
				return -2;

			} catch (IOException e) {
				System.err.println("IOException by executing jsonRequest: "
						+ testRequest +" ; "+e.getLocalizedMessage());							
			}
			finally{
				if (connection != null)
					connection.disconnect();
			}
		}
		return -1;
	}
	
	
	/****SSL Check****/
	
	private void checkSSLCert(){
		System.out.println("Check SSL Certificate");
		String conCheck = checkConnection();
		if(conCheck != null){
			switch(conCheck){
			case CERT_EXCEPTION:
				System.out.println("No certification installated");
				trustedCertPath = DigitalSTROMConfig.TRUST_CERT_PATH; 
				if(trustedCertPath != null && !trustedCertPath.isEmpty()){
					System.out.println("Certification path is set, generate the certification and accept it.");
					setupWithCertPath();
					conCheck = checkConnection();
					if(conCheck != null && conCheck.contains(CERT_EXCEPTION)){
						System.err.println("Invalid certification at path " + this.trustedCertPath);
						return;
					}
					//checkSSLCert();
				} else{
					//check SSL certificate is installated
					System.out.println("No certification path is set, accept all certifications.");
					setupAcceptAllSSLCertificats();
					conCheck = checkConnection();
					if(conCheck != null && conCheck.contains(CERT_EXCEPTION)){
						System.err.println("Invalid certification at parth " + this.trustedCertPath);
						return;
					}
					checkSSLCert();
				}
				break;
			case HOSTNAME_VERIFIER:
				
				System.out.println("Can't verifi hostname, accept hostname: dss.local.");
				this.setupHostnameVerifierForDssLocal();
				conCheck = checkConnection();
				if(conCheck != null && conCheck.contains(HOSTNAME_VERIFIER)){
					System.out.println("Can't verifi hostname, accept all hostnames");
					this.setupHostnameVerifier();
				}
				checkSSLCert();
				break;
			case UNKOWN_HOST:
				return;
			default: return;
			}
		}else{
			System.out.println("All right, SSL Certificate is installeted");
		}
		
	}
	
	private String checkConnection(){
		try {
			URL url = new URL(uri);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.connect();
			connection.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			if(e instanceof javax.net.ssl.SSLHandshakeException){
				System.out.println(e.getMessage() );
				if(msg.contains(CERT_EXCEPTION) || msg.contains(PKIX_PATH_FAILD)){
					if(msg.contains(HOSTNAME_VERIFIER)){
						//System.out.println("Hostname");
						return HOSTNAME_VERIFIER;
					}
					return CERT_EXCEPTION;
				}
				
			}
			if(msg.contains(HOSTNAME_WRONG)){
				return HOSTNAME_VERIFIER;
			}
			if(e instanceof java.net.UnknownHostException){
				System.err.println("Can't find host: " + msg);
				return UNKOWN_HOST;
			}
			e.printStackTrace();			
		}
		return null;
	}
	
	private void setupAcceptAllSSLCertificats(){
		Security.addProvider(Security.getProvider("SunJCE"));
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					
					
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					
					
				}
            }
        };

       		
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");			
      
			sslContext.init(null, trustAllCerts, new SecureRandom());
			
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setupWithCertPath() {
		 
		CertificateFactory certificateFactory;
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		
			trustedCert = (X509Certificate) certificateFactory.generateCertificate(this.certInputStream);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			if(e.getMessage().contains(NO_CERT_AT_PATH)){
				System.err.println("Can't find a certificationfile at the certificationpath: " + trustedCertPath + 
						"\nPlease check the path!");
				return;
			} else {
				e.printStackTrace();
			}
		}
				
 
 
		final TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
 
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
 
				return null;
 
			}
 
 
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws CertificateException { 
				if (!certs[0].equals(trustedCert))
					throw new CertificateException();
			}
 
 
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws CertificateException {
 
				if (!certs[0].equals(trustedCert))
					throw new CertificateException();
 
			}
 
		} };
 
		SSLContext sslContext;
		
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void setupHostnameVerifier(){
		 HostnameVerifier allHostValid = new HostnameVerifier(){

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return true;
				}
				
		};
			
		HttpsURLConnection.setDefaultHostnameVerifier(allHostValid);
	}
	
	private void setupHostnameVerifierForDssLocal(){
		 HostnameVerifier allHostValid = new HostnameVerifier(){

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return arg0.contains("dss.local.");
				}
				
		};
			
		HttpsURLConnection.setDefaultHostnameVerifier(allHostValid);
	}
	
}