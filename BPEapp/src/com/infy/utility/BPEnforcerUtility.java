package com.infy.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.infy.bpe.core.DataStore;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class BPEnforcerUtility {

	private static PartnerConnection pCon = null;

	public static PartnerConnection getConnection() {
		try {
			if (null == pCon) {
				ConnectorConfig pConfig = getConfig();
				pCon = com.sforce.soap.partner.Connector.newConnection(pConfig);
				getNamespace();

			}
		} catch (ConnectionException e) {
			System.err.println("ConnectionException to Agile Pro Org " + e.getMessage());
			e.printStackTrace();
		}
		return pCon;
	}

	private static void getNamespace() {
		String orgNameSpace = "";
		System.out.println("Querying for the Namespace of Organization..." + DataStore.AGILEPRO_USERNAME);
		try {
			// query for organization namespace
			QueryResult queryResults = pCon.query("SELECT Id,Name,NamespacePrefix FROM Organization ");
			if (queryResults.getSize() > 0) {
				for (SObject s : queryResults.getRecords()) {
					System.out.println("Id: " + s.getId() + " NAME  " + s.getField("Name") + " NAME SPACE "
							+ s.getField("NamespacePrefix"));
					orgNameSpace = (String) s.getField("NamespacePrefix");
					DataStore.NAMESPACE = (null != orgNameSpace && !orgNameSpace.isEmpty()) ? (orgNameSpace + "__")
							: BPEnforcerConstants.EMPTY_STR;
					System.out.println(DataStore.NAMESPACE + "#################DataStore.NAMESPACE");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertScanFailures(String exceptionName, String exceptionCause) {
		try {
			pCon = getConnection();

			String scanId = insertScanDtls(DataStore.ORGANISATIONID, DataStore.ORGANISATIONNAME,DataStore.REMOTEAUTHNAME);
			if (null != scanId) {
				List<SObject> scanList = new ArrayList<SObject>();
				SObject scanFailureObj = new SObject();
				DataStore.NAMESPACE="AGP__";
				//DataStore.NAMESPACE="AGP_Kone__";


				scanFailureObj.setType(DataStore.NAMESPACE + "AGP_Scan_Failure__c");
				scanFailureObj.setField("Name", exceptionName);
				scanFailureObj.setField(DataStore.NAMESPACE + "Description__c", exceptionCause);
				scanFailureObj.setField(DataStore.NAMESPACE + "Scan__c", scanId);
				scanList.add(scanFailureObj);
				SaveResult[] scanSaveResults = pCon.create(scanList.toArray(new SObject[scanList.size()]));
				if (!scanSaveResults[0].isSuccess()) {
					for (com.sforce.soap.partner.Error error : scanSaveResults[0].getErrors()) {
						System.out.println("Scan Failure Object insert error   " + error.getMessage());
					}
				}
			}
		} catch (ConnectionException e) {
			System.err.println("ConnectionException to Agile Pro Org " + e.getMessage());
			e.printStackTrace();

		}
	}

	public static <T> List<List<T>> getBatches(List<T> collection, int batchSize) {
		int i = 0;
		List<List<T>> batches = new ArrayList<List<T>>();
		while (i < collection.size()) {
			int nextInc = Math.min(collection.size() - i, batchSize);
			List<T> batch = collection.subList(i, i + nextInc);
			batches.add(batch);
			i = i + nextInc;
		}

		return batches;
	}

	public static String insertScanDtls(String organisationID, String organisationName,String remoteAuth) throws ConnectionException {
		List<SObject> scanList = new ArrayList<SObject>();
		SObject scanObj = new SObject();
		
			DataStore.NAMESPACE="AGP__";
		
			
			scanObj.setType(DataStore.NAMESPACE+"AGP_Scan__c");
			scanObj.setField(DataStore.NAMESPACE+"Org_Id__c", organisationID);
			scanObj.setField(DataStore.NAMESPACE+"Org_Name__c", organisationName);
			scanObj.setField(DataStore.NAMESPACE+"Remote_Auth_Detail__c", remoteAuth);
			//scanObj.setField(DataStore.REMOTEAUTHNAME, value)
		
	
	

		scanList.add(scanObj);
		SaveResult[] scanSaveResults = pCon.create(scanList.toArray(new SObject[scanList.size()]));
		if (!scanSaveResults[0].isSuccess()) {
			for (com.sforce.soap.partner.Error error : scanSaveResults[0].getErrors()) {
				System.out.println("Scan Object Insert Error   " + error.getMessage());
			}
			return null;
		}
		return scanSaveResults[0].getId();
	}

	private static ConnectorConfig getConfig() {
		System.out.println("Configure connection details for the Agile Pro Org ");
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(DataStore.AGILEPRO_USERNAME);
		config.setPassword(DataStore.AGILEPRO_PASSWORD);
		if (BPEnforcerConstants.YES_LABEL.equalsIgnoreCase(DataStore.PROXYREQUIRED)) {
			config.setProxy(DataStore.HOST, DataStore.PORT);
			config.setProxyPassword(DataStore.PROXY_PASSWORD);
			config.setProxyUsername(DataStore.PROXY_USERNAME);
		}
		config.setManualLogin(false);
		config.setAuthEndpoint(DataStore.AGILEPRO_AUTHENDPOINT + "/services/Soap/u/" + DataStore.API_VERSION);

		return config;
	}

	// To count the number of Lines in VFpage
	public static int getPageLineCount(String body) {
		int lineNo = 0;
		try {
			BufferedReader br = new BufferedReader(new StringReader(body));
			String line;
			String startRegex = "<!--";
			String endRegex = "-->";

			Pattern startReg = Pattern.compile(startRegex);
			Pattern endReg = Pattern.compile(endRegex);
			// System.out.println(startReg+"Regex"+endReg);

			boolean flag = false;
			while ((line = br.readLine()) != null) {
				Matcher mStart = startReg.matcher(line);
				if (mStart.find()) {
					flag = true;
				}
				if (!flag && !("".equals(line.trim()))) {
					lineNo++;
				}
				Matcher mEnd = endReg.matcher(line);
				if (mEnd.find()) {
					flag = false;
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("count of Lines: "+lineNo);
		return lineNo;
	}

	public static int findDMLSOQLCount(String input, String regex) throws IOException {
		int count = 0;
		String line;
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		BufferedReader br = new BufferedReader(new StringReader(input));
		while ((line = br.readLine()) != null) {
			matcher = pattern.matcher(line);
			if (matcher.matches()) {
				count++;
			}

		}
		return count;
	}
}
