package com.infy.bpe.core;

import java.util.List;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;

public class DataStore {
	public static String USERNAME;
	public static String PASSWORD;
	public static String HOST;
	public static String NAMESPACE;
	public static int PORT;
	public static String PROXY_PASSWORD;
	public static String PROXY_USERNAME;
	public static String OUTPUT_FILENAME;
	public static String GENERIC_USERID;
	public static EnterpriseConnection ent;
	public static PartnerConnection pCon;
	public static MetadataConnection mCon;
	public static String AUTHENDPOINT;
	public static int BATCH_SIZE;
	public static double API_VERSION;
	public static long WAIT_TIME_MILLIS;
	public static int MAX_NUM_POLL_REQUESTS;
	public static int DASHBOARDCOMPONENTS;
	public static int MAXADMINCOUNT;
	public static int MINUSERCOUNT;
	public static String ORGANISATIONID;
	public static String ORGANISATIONNAME;
	
	public static String PROXYREQUIRED;
	public static String AGILEPRO_USERNAME;
	public static String AGILEPRO_PASSWORD;
	public static String AGILEPRO_AUTHENDPOINT;
	public static List<String> wfData;
	
	
	public static String BESTPRACTICES;
	public static String CODESTYLE;
	public static String ERRORPRONE;
	public static String PERFORMANCE;
	public static String DESIGN;
	public static String SECURITY;
	public static String SEEALLDATA;
	public static String REMOTEAUTHNAME;

	
	
	
	
}
