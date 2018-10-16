package com.infy.restservices;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.DataStore;
import com.infy.services.model.ConfigSettings;
import com.infy.services.model.ErrorMessage;
import com.infy.utility.BPEnforcerUtility;
import com.sforce.ws.ConnectionException;

@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {
	
	private static ExecutorService executorService;
	
	private static synchronized ExecutorService getInstance(){
		if(executorService == null ){
		    executorService = Executors.newFixedThreadPool(15);
		}
		return executorService;
	}

	@POST
	public Response setConfigDetails(@Valid ConfigSettings configSettings) {
		Response response = null;
		try {
			DataStore.USERNAME = configSettings.getSfdcUser();
			DataStore.PASSWORD = configSettings.getSfdcPassword();
		//  DataStore.PROXY_PASSWORD = configSettings.getProxyPassword(); 
		//	DataStore.PROXY_USERNAME = configSettings.getProxyUser(); 
			DataStore.AUTHENDPOINT =configSettings.getAuthUrl();
			
//			DataStore.REMOTEAUTHNAME =configSettings.getRemoteAuthName();

			DataStore.REMOTEAUTHNAME= configSettings.getRemoteAuth();
			DataStore.BESTPRACTICES =configSettings.getBestPractices();			
			DataStore.CODESTYLE=configSettings.getCodeStyle();
			DataStore.ERRORPRONE=configSettings.getErrorProne();
			DataStore.PERFORMANCE=configSettings.getPerformance();
			DataStore.DESIGN=configSettings.getDesign();
			DataStore.SECURITY=configSettings.getSecurity();
			
			
		
			
		
			response= validateData(configSettings);
			if(response==null){
				response= Response.status(Response.Status.OK).entity("Credentials validated").build();
				try{
				asyncStartBPScan();
				}
				catch(Exception e){
					response= Response.status(Response.Status.BAD_REQUEST).entity(" Connection failed ").build();

					
				}
				
				/* catch (FileNotFoundException e) {
				ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), 404, "FileNotFoundException");
				response =Response.status(Response.Status.NOT_FOUND).entity(errorMessage).build();
				e.printStackTrace();
			} catch (ConnectionException e) {
				ErrorMessage errorMessage = new ErrorMessage(e.getMessage(), 401, "ConnectionException");
				response =Response.status(Response.Status.UNAUTHORIZED).entity(errorMessage).build();
				e.printStackTrace();
			} catch (InterruptedException e) {
				response= Response.status(Response.Status.NOT_FOUND).build();
				e.printStackTrace();
			} catch (IOException e) {
				response =Response.status(Response.Status.NOT_FOUND).build();
				e.printStackTrace();
			} catch (JAXBException e) {
				response = Response.status(Response.Status.NOT_FOUND).build();
			}*/ 

			
			
		}
			
			
		}
		
		catch (Exception e) {
			response =Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();
		}
		return response;
		
	}

	private Response validateData(ConfigSettings configSettings) {
		Response response = null;
		boolean isValid = true;
		String message = null;
		if(StringUtils.isEmpty(configSettings.getSfdcUser())){
			isValid =  false;
			message = "SDFC user cannot be empty.Please provide the valid data";
		}else if(StringUtils.isEmpty(configSettings.getSfdcPassword())){
			isValid =   false;
			message = "SDFC password cannot be empty.Please provide the valid data";
		}else if(StringUtils.isEmpty(configSettings.getAuthUrl())){
			isValid =   false;
			message = "Auth URL cannot be empty.Please provide the valid URL";
		}
		if (!isValid) {
			ErrorMessage errorMessage = new ErrorMessage(message, 400, "Field Data Invalid");
			response = Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
		}
		return response;
	}

	private void asyncStartBPScan(){	 

		ExecutorService executorService = ConfigResource.getInstance();
	    Future<?> future = executorService.submit(new Runnable()
	    {
	    	
	        public void run() {
	            System.out.println("Asynchronous task started");
	            try {
					CodeAnalyser.startScanSalesOrg();
					

				} catch (ConnectionException e) {
					System.err.println("F1: Asynchronous Job fail due to "+e.getMessage());
					BPEnforcerUtility.insertScanFailures(e.getClass().getSimpleName(), e.getMessage()); 
					e.printStackTrace();
					
					
				} catch (InterruptedException e) {
					System.err.println("F2: Asynchronous Job fail due to "+e.getMessage());
					BPEnforcerUtility.insertScanFailures(e.getClass().getSimpleName(), e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("F3: Asynchronous Job fail due to "+e.getMessage());
					BPEnforcerUtility.insertScanFailures(e.getClass().getSimpleName(), e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("F4: Asynchronous Job fail due to "+e.getMessage());
					BPEnforcerUtility.insertScanFailures(e.getClass().getSimpleName(), e.getMessage());
					e.printStackTrace();
				}
				
	        }

	    }); 	    
	    if(future.isDone()){
	    	executorService.shutdown();
	    }
	    
	
	}
	
	  	@GET
	  	@Path("/greeting")
	    public Response handleGreeting() {
	    	ConfigSettings configSettings = new ConfigSettings();
	    	configSettings.setSfdcUser("Mramach@rei.com");	    
	    	configSettings.setAuthUrl("TEST.com");
	    	return  Response.status(Response.Status.OK).entity(configSettings).build();
	    }
  
    
}
