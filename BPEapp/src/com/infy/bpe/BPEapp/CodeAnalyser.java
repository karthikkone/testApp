package com.infy.bpe.BPEapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


	


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.infy.bpe.core.DataStore;
import com.infy.bpe.core.EnterpriseOperations;
import com.infy.bpe.core.ToolingOperations;
import com.infy.pmd.bestpractices.ApexUnitTestClassShouldHaveAssertsCustomRule;
import com.infy.pmd.bestpractices.ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule;
import com.infy.pmd.bestpractices.AvoidGlobalModifierCustomRule;
import com.infy.pmd.bestpractices.AvoidLogicInTriggerCustomRule;
import com.infy.pmd.designRules.StdCustomCyclomaticComplexityRule;
import com.infy.pmd.errorprone.AvoidHardcodingIdCustomRule;
import com.infy.pmd.errorprone.MethodWithSameNameAsEnclosingClassCustomRule;
import com.infy.pmd.namingConvention.ClassNamingConventionsCutomRule;
import com.infy.pmd.namingConvention.MethodNamingConventionsCustomRule;
import com.infy.pmd.namingConvention.VariableNamingConventionsCustomRule;
import com.infy.pmd.performance.AvoidDmlStatementsInLoopsCustomRule;
import com.infy.pmd.performance.AvoidSoqlInLoopsCustomRule;
import com.infy.pmd.performance.AvoidSoslInLoopsCustomRule;
import com.infy.pmd.security.ApexBadCryptoCustomRule;
import com.infy.pmd.security.ApexCRUDViolationCustomRule;
import com.infy.pmd.security.ApexCSRFCustomRule;
import com.infy.pmd.security.ApexDangerousMethodsCustomRule;
import com.infy.pmd.security.ApexInsecureEndpointCustomRule;
import com.infy.pmd.security.ApexOpenRedirectCustomRule;
import com.infy.pmd.security.ApexSOQLInjectionCustomRule;
import com.infy.pmd.security.ApexSharingViolationsCustomRule;
import com.infy.pmd.security.ApexSuggestUsingNamedCredCustomRule;
import com.infy.pmd.security.ApexXSSFromEscapeFalseCustomRule;
import com.infy.pmd.security.ApexXSSFromURLParamCustomRule;
import com.infy.report.model.ReportType;
import com.infy.services.BPEnforcerContext;
import com.infy.services.impl.ExcelOutputGenerator;
import com.infy.services.impl.SFObjectWriter;
import com.infy.services.model.CyclomaticRuleBean;
import com.infy.services.model.HealthCheckParameter;
import com.infy.services.model.MethodDetailsBean;
import com.infy.utility.BPEnforcerConstants;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.tooling.ToolingConnection;
import com.sforce.soap.tooling.sobject.ApexClass;
import com.sforce.soap.tooling.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;


@SpringBootApplication
public class CodeAnalyser {

	static HashMap<String, ArrayList<String>> custPermProMap = new HashMap<String, ArrayList<String>>();

	static final int DEFAULT_PORT = 80;

	static final String PROXY_REQUIRED = "NO";

	static String proxyReqSetting;

	private static BPEnforcerContext bpEnorcerContext = new BPEnforcerContext();

	private static List<ReportType> reportList;
	
	private static Map<String,ReportType> apexClassModifiedDetails;
	
	private static List<CyclomaticRuleBean> cycloMaticRuleList;
	
	private static Map<String, List<MethodDetailsBean>> mapMethodDetails;

	private static boolean isHerokuInvoke = true;

	public static void main(String[] args) throws InterruptedException, IOException {
		
		SpringApplication.run(CodeAnalyser.class, args);

		System.out.println("IS Heroku " + System.getProperty("heroku"));
		Properties props = readSettings();
		try {
			DataStore.USERNAME = props.getProperty("sfdcUser");
			DataStore.PASSWORD = props.getProperty("sfdcPassword");
			DataStore.HOST = props.getProperty("proxyHost");
			proxyReqSetting = !isEmpty(props.getProperty("proxyRequired")) ? props.getProperty("proxyRequired")
					: PROXY_REQUIRED;
			DataStore.PORT = isNumeric(props.getProperty("proxyPort"))
					? Integer.parseInt(props.getProperty("proxyPort")) : DEFAULT_PORT;
			DataStore.PROXY_PASSWORD = props.getProperty("proxyPassword");
			DataStore.PROXY_USERNAME = props.getProperty("proxyUser");
			System.out.println("HTTP Proxy Required? " + proxyReqSetting);
			if (PROXY_REQUIRED.equalsIgnoreCase(proxyReqSetting) && (isEmpty(DataStore.HOST)
					|| isEmpty(DataStore.PROXY_USERNAME) || isEmpty(DataStore.PROXY_PASSWORD))) {
				System.err.println("Please provide all the required proxy details in settings.properties file");
				return;
			}
			DataStore.AUTHENDPOINT = props.getProperty("authUrl");
			if (PROXY_REQUIRED.equalsIgnoreCase(proxyReqSetting)) {
				System.out.println("Connecting to Salesforce org at URL: " + DataStore.AUTHENDPOINT
						+ " through HTTP Proxy " + DataStore.HOST);
			} else {
				System.out.println("Connecting to Salesforce org at URL: " + DataStore.AUTHENDPOINT);
			}
			DataStore.OUTPUT_FILENAME = props.getProperty("pathToOutputFile");
			DataStore.GENERIC_USERID = props.getProperty("genericUserId");
			DataStore.BATCH_SIZE = Integer.parseInt(props.getProperty("batchSize"));
			DataStore.API_VERSION = Double.parseDouble(props.getProperty("apiVersion"));
			DataStore.PROXYREQUIRED = proxyReqSetting;
			DataStore.AGILEPRO_USERNAME = props.getProperty("agileProUser");
			DataStore.AGILEPRO_PASSWORD = props.getProperty("agileProPwd");
			DataStore.AGILEPRO_AUTHENDPOINT = props.getProperty("agileProURL");
			System.out.println("Force.com API Version " + DataStore.API_VERSION);
			//System.out.println("isRestBasedInvoke ==>" + isHerokuInvoke);
			DataStore.WAIT_TIME_MILLIS = Long.parseLong(props.getProperty("waitTimeMillis"));
			DataStore.MAX_NUM_POLL_REQUESTS = Integer.parseInt(props.getProperty("maxPolls"));
			DataStore.DASHBOARDCOMPONENTS = Integer.parseInt(props.getProperty("dashboardComponents"));
			DataStore.MAXADMINCOUNT = Integer.parseInt(props.getProperty("maxadmincount"));
			DataStore.MINUSERCOUNT = Integer.parseInt(props.getProperty("minusercount"));
			if (!false) {
				startScanSalesOrg();
			}

		} catch (ConnectionException e1) {
			System.err.println("ConnectionException " + e1.getMessage());
			e1.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static ArrayList<ReportType> startScanSalesOrg()
			throws ConnectionException, InterruptedException, IOException, Exception {
		ArrayList<ReportType> reportlist = new ArrayList<ReportType>();

		ConnectorConfig eConfig = getConfig();
		eConfig.setManualLogin(false);
		eConfig.setAuthEndpoint(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
		System.out.println(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
		DataStore.ent = com.sforce.soap.enterprise.Connector.newConnection(eConfig);
		//not used
	//	EnterpriseOperations.profile();

		// Partner and Tooling Connection
		ConnectorConfig pConfig = getConfig();
		pConfig.setManualLogin(true);
		pConfig.setAuthEndpoint(DataStore.AUTHENDPOINT + "/services/Soap/u/" + DataStore.API_VERSION);
		pConfig.setServiceEndpoint(eConfig.getServiceEndpoint().replaceAll("/c/", "/u/"));
		PartnerConnection pCon = com.sforce.soap.partner.Connector.newConnection(pConfig);

		com.sforce.soap.partner.LoginResult lrp = pCon.login(DataStore.USERNAME, DataStore.PASSWORD);
		pConfig.setSessionId(lrp.getSessionId());
		pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/T/"));
		pConfig.setAuthEndpoint(pConfig.getAuthEndpoint().replace("/u/", "/T/"));

		ToolingConnection con = com.sforce.soap.tooling.Connector.newConnection(pConfig);

		DataStore.ORGANISATIONID = con.getUserInfo().getOrganizationId();
		DataStore.ORGANISATIONNAME = con.getUserInfo().getOrganizationName();
		System.out.println(" ORG DETAILS: " + DataStore.ORGANISATIONID + " NAME: " + DataStore.ORGANISATIONNAME);

		// Retrieving Apex Classes
		ArrayList<SObject> classes = ToolingOperations.retrieveToolingComponents(con, "ApexClass");
		ArrayList<SObject> triggers = ToolingOperations.retrieveToolingComponents(con, "ApexTrigger");
		ArrayList<SObject> pages = ToolingOperations.retrieveToolingComponents(con, "ApexPage");
		//ArrayList<SObject> apexComponents = ToolingOperations.retrieveToolingComponents(con, "ApexComponent");
		ArrayList<SObject> validationRules = ToolingOperations.retrieveToolingComponents(con, "ValidationRule");
		//ArrayList<SObject> workflowFieldUpdate = ToolingOperations.retrieveToolingComponents(con, "WorkflowFieldUpdate");
		//ArrayList<SObject> flowDtls = ToolingOperations.retrieveToolingComponents(con, "Flow");
		//ToolingOperations.multipleFieldUpdates(workflowFieldUpdate, triggers, flowDtls); // commented by Anvesh
		ArrayList<SObject> totallist = new ArrayList<SObject>();		
		totallist.addAll(classes);
		totallist.addAll(triggers);
		totallist.addAll(pages);
		// inactive workflow rules		
		ArrayList<SObject> workFlowRules = ToolingOperations.retrieveToolingComponents(con, "Workflowrule");
		reportlist.addAll(ToolingOperations.inactiveWorkFlowRule(workFlowRules));
	
	

		//############################## NEED TO CHECK -- NEVER DELETE ##########################
		 reportlist.addAll(ToolingOperations.checkDmlInTry(triggers));
		// reportlist.addAll(EnterpriseOperations.dashboard());
		// reportlist.addAll(ToolingOperations.checkFutureTag(classes));
		// reportlist.addAll(ToolingOperations.methodDecl(classes));
		//############################## NEED TO CHECK##########################


		// ORG health parameters
		HealthCheckParameter healthCheckParameter = new HealthCheckParameter();
//		if (!isHerokuInvoke) {
		if (false) {
/*
			Integer codeLines = BPHealthCheckerRules.findLinesOfCodeApexPage(pages);
			Map<String, Integer> codeCoverageDtls = BPHealthCheckerRules.codeCoverage(con, classes);
			// No. classes which does not have test coverage
			healthCheckParameter.setLessCodeCoverageCnt(codeCoverageDtls.get(BPEnforcerConstants.TESTCLS_APEXCOVERAGE));

			// custom lines of code (Apex+VF)
			healthCheckParameter.setApexClassLines(codeCoverageDtls.get(BPEnforcerConstants.LOC_APEX));
			healthCheckParameter.setApexPageLines(codeLines);

			// Security Health Risk Score
			healthCheckParameter.setSecurityScore(BPHealthCheckerRules.securityHealthCheckRisks(con));
			// No. of SOQL statements (Class + Trigger)

			healthCheckParameter.setApexClsSoql(BPHealthCheckerRules.checkForSOQLInApexClass(classes));
			healthCheckParameter.setApexTriggerSoql(BPHealthCheckerRules.checkForSOQLInApexTrigger(triggers));
			// No. of DML statements (Class + Trigger)
			healthCheckParameter.setApexClsDML(BPHealthCheckerRules.checkForDMLInApexClass(classes));
			healthCheckParameter.setApexTriggerDML(BPHealthCheckerRules.checkForDMLInApexTrigger(triggers));
			//No of Apex Class
			healthCheckParameter.setTotalApexCls(codeCoverageDtls.get(BPEnforcerConstants.APEX_CLASS));
			//No of Apex Pages
			healthCheckParameter.setTotalApexPage(pages.size());
			//No of Apex Components
			healthCheckParameter.setTotalApexComponents(apexComponents.size());*/ //commented by Anvesh
			
			Integer codeLines = 2;
			//Map<String, Integer> codeCoverageDtls = BPHealthCheckerRules.codeCoverage(con, classes);
			// No. classes which does not have test coverage
			healthCheckParameter.setLessCodeCoverageCnt(2);

			// custom lines of code (Apex+VF)
			healthCheckParameter.setApexClassLines(2);
			healthCheckParameter.setApexPageLines(2);

			// Security Health Risk Score
			healthCheckParameter.setSecurityScore(2);
			// No. of SOQL statements (Class + Trigger)

			healthCheckParameter.setApexClsSoql(2);
			healthCheckParameter.setApexTriggerSoql(2);
			// No. of DML statements (Class + Trigger)
			healthCheckParameter.setApexClsDML(2);
			healthCheckParameter.setApexTriggerDML(2);
			//No of Apex Class
			healthCheckParameter.setTotalApexCls(2);
			//No of Apex Pages
			healthCheckParameter.setTotalApexPage(2);
			//No of Apex Components
			healthCheckParameter.setTotalApexComponents(2);
		}

		pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/m/"));
		MetadataConnection mcon = com.sforce.soap.metadata.Connector.newConnection(pConfig);
		DataStore.mCon = mcon;
		reportlist.addAll(EnterpriseOperations.scheduledJobs());

		 startCyclomaticScan(classes); // contains cyclo-pmd-DESIGN

		// PMD rules start

			//reportlist.addAll(EnterpriseOperations.inactiveuser());
			reportlist.addAll(ToolingOperations.checkInActiveTrigger(triggers));
			reportlist.addAll(ToolingOperations.triggerNameCheck(triggers));
			reportlist.addAll(ToolingOperations.findSystemDebugCount(classes));
			reportlist.addAll(ToolingOperations.inactiveValidationRule(validationRules));
		//see all data true
			//reportlist.addAll(ToolingOperations.findquery(classes));
			reportlist.addAll(ToolingOperations.findTest(classes));
			reportlist.addAll(ToolingOperations.errorhandling(classes));
			reportlist.addAll(ToolingOperations.annotationbetweencomments(classes));
			reportlist.addAll(ToolingOperations.Presence_Of_Doctype_VF(pages));

			reportlist.addAll(ToolingOperations.checkinlinejs(pages));
			reportlist.addAll(ToolingOperations.checkinlinecss(pages));
			startBestPracticesScan(classes, triggers); // all d 4 rules PMD BP

		
			reportlist.addAll(ToolingOperations.findSOQL(classes));
			reportlist.addAll(ToolingOperations.checkRecordTypeInfo(classes));
			reportlist.addAll(ToolingOperations.checkSelectStatement(classes));


			startPerformanceRuleScan(classes); // all 3 rules 4m PMD PERFORMANCE
		
		//	reportlist.addAll(ToolingOperations.sharingclasses(classes));
			startSecurityRulesScan(classes); // all 10 rules PMD SECURITY
		

			reportlist.addAll(ToolingOperations.findSystemRunas(classes));
			reportlist.addAll(ToolingOperations.multipletriggers(triggers));
			//reportlist.addA ll(ToolingOperations.findSystemAssert(classes));
			//reportlist.addAll(ToolingOperations.findID(classes));
			

			startErrorProneCheckScan(classes); // 2 rules from PMD ERRprone 6 PENDING

			startNamingConventionRuleScan(classes); // 3 rules from
														// PMDCODESTYLE -4p
			reportlist.addAll(ToolingOperations.checkNoOfLines(classes));
		

			reportlist.addAll(reportList);
			System.out.println("############## " + reportlist.size());
		

//			bpEnorcerContext.setBpOutputGenerator(isHerokuInvoke?new SFObjectWriter(DataStore.ORGANISATIONID,DataStore.ORGANISATIONNAME,DataStore.REMOTEAUTHNAME):new ExcelOutputGenerator());
			bpEnorcerContext.setBpOutputGenerator(new SFObjectWriter(DataStore.ORGANISATIONID,DataStore.ORGANISATIONNAME,DataStore.REMOTEAUTHNAME));
			bpEnorcerContext.setHealthCheckParameter(healthCheckParameter);
			bpEnorcerContext.createOutput(reportlist);	

		return reportlist;
	}

	private static ConnectorConfig getConfig() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(DataStore.USERNAME);
		config.setPassword(DataStore.PASSWORD);

		if (PROXY_REQUIRED.equalsIgnoreCase(proxyReqSetting)) {
			config.setProxy(DataStore.HOST, DataStore.PORT);
			config.setProxyPassword(DataStore.PROXY_PASSWORD);
			config.setProxyUsername(DataStore.PROXY_USERNAME);
		}
		return config;
	}

	
	private static Properties readSettings() throws IOException {
		Properties props = new Properties();
		InputStream is = null;
		// First try loading from the current directory
		/*isHerokuInvoke = (null != System.getProperty("heroku") && Boolean.valueOf(System.getProperty("heroku"))) ? true
				: false;*/
		
		isHerokuInvoke=true;
		
		try {
			File f = new File("settings.properties");
			is = new FileInputStream(f);

		} catch (Exception e) {
			is = null;
			System.out.println("Error while retrieving Settings: " + e.getMessage());
			e.printStackTrace();
		}

		try {
			if (is == null || isHerokuInvoke) {
				startGrizzlyServer();
				// Try loading from classpath
				/*
				 * is = new CodeAnalyser().getClass().getResourceAsStream(
				 * "/settings.properties");
				 */
			}
			// Try loading properties from the file (if found)
			props.load(is);
		} catch (Exception e) {
			System.out.println("Error while retrieving Settings: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Retrieving configuration from settings.properties...");
		is.close();
		return props;
	}

	/**
	 * check if the string is numeric
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		return str.matches("\\d+");
	}

	/**
	 * check if string is null or empty
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

	private static void startGrizzlyServer() throws IllegalArgumentException, IOException {

		final String baseUri = "http://localhost:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "1234")
				+ "/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", "com.infy.restservices");
		initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

		System.out.println("Starting grizzly...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
		System.out
				.println(String.format("Jersey started with WADL available at %sapplication.wadl.", baseUri, baseUri));
	}

	/**
	 * @return the mapMethodDetails
	 */
	public static Map<String, List<MethodDetailsBean>> getMapMethodDetails() {
		if (null == mapMethodDetails) {
			mapMethodDetails = new HashMap<String, List<MethodDetailsBean>>();
		}
		return mapMethodDetails;
	}

	/**
	 * @param mapMethodDetails
	 *            the mapMethodDetails to set
	 */
	public static void setMapMethodDetails(String className, List<MethodDetailsBean> methodDetailsBeans) {
		if (null == mapMethodDetails) {
			mapMethodDetails = new HashMap<String, List<MethodDetailsBean>>();
		}
		mapMethodDetails.put(className, methodDetailsBeans);
	}

	public static void addBestPracticesRule(ReportType reportType) {
		if (null == reportList) {
			reportList = new ArrayList<>();
		}
		reportList.add(reportType);

	}
	
	/**
	 * @param cycloMaticRuleList the cycloMaticRuleList to set
	 */
	public static void setCycloMaticRuleList(List<CyclomaticRuleBean> cycloMaticRuleList) {
		CodeAnalyser.cycloMaticRuleList = cycloMaticRuleList;
	}
	
	
	/**
	 * @return the apexClassModifiedDetails
	 */
	public static Map<String, ReportType> getApexClassModifiedDetails() {
		if (null == apexClassModifiedDetails) {
			apexClassModifiedDetails = new HashMap<String,ReportType>();
		}
		return apexClassModifiedDetails;
	}


	/**
	 * @param cycloMaticRuleList the cycloMaticRuleList to set
	 */
	public static void addCycloMaticRule(CyclomaticRuleBean cycloMaticRule) {
		if(null==cycloMaticRuleList){
			cycloMaticRuleList = new ArrayList<>();
		}
		cycloMaticRuleList.add(cycloMaticRule);  
	}
	

	private static void startCyclomaticScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********DESIGN - PMD**********");
		try {

			StdCustomCyclomaticComplexityRule rule = new StdCustomCyclomaticComplexityRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())
							&& apexComponent.getBody() != null) {
						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule.apply(Arrays.asList(node), ruleCtx);
						
					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startNamingConventionRuleScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********NAMING CONVENTIONS FROM CODESTYLE - PMD**********");
		try {
			ClassNamingConventionsCutomRule rule1 = new ClassNamingConventionsCutomRule();
			VariableNamingConventionsCustomRule rule2 = new VariableNamingConventionsCustomRule();
			MethodNamingConventionsCustomRule rule3 = new MethodNamingConventionsCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
					}
				}
			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startPerformanceRuleScan(List<SObject> classes) {
		Node node;
		ApexClass apexComponent;
		System.out.println("**********PERFORMANCE SCAN - PMD**********");
		try {
			AvoidDmlStatementsInLoopsCustomRule rule1 = new AvoidDmlStatementsInLoopsCustomRule();
			AvoidSoqlInLoopsCustomRule rule2 = new AvoidSoqlInLoopsCustomRule();
			AvoidSoslInLoopsCustomRule rule3 = new AvoidSoslInLoopsCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startErrorProneCheckScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********ERROR PRONE SCAN - PMD**********");
		try {
			AvoidHardcodingIdCustomRule rule = new AvoidHardcodingIdCustomRule();
			MethodWithSameNameAsEnclosingClassCustomRule rule2 = new MethodWithSameNameAsEnclosingClassCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {
						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startSecurityRulesScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********SECURITY SCAN - PMD**********");
		try {
			ApexBadCryptoCustomRule rule = new ApexBadCryptoCustomRule();
			ApexCRUDViolationCustomRule rule1 = new ApexCRUDViolationCustomRule();
			ApexCSRFCustomRule rule2 = new ApexCSRFCustomRule();
			ApexDangerousMethodsCustomRule rule3 = new ApexDangerousMethodsCustomRule();
			ApexInsecureEndpointCustomRule rule4 = new ApexInsecureEndpointCustomRule();
			ApexOpenRedirectCustomRule rule5 = new ApexOpenRedirectCustomRule();
			ApexSharingViolationsCustomRule rule6 = new ApexSharingViolationsCustomRule();
			ApexSOQLInjectionCustomRule rule7 = new ApexSOQLInjectionCustomRule();
			ApexSuggestUsingNamedCredCustomRule rule8 = new ApexSuggestUsingNamedCredCustomRule();
			ApexXSSFromEscapeFalseCustomRule rule9 = new ApexXSSFromEscapeFalseCustomRule();
			ApexXSSFromURLParamCustomRule rule10 = new ApexXSSFromURLParamCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));

						rule.apply(Arrays.asList(node), ruleCtx);
						rule1.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
						rule4.apply(Arrays.asList(node), ruleCtx);
						rule5.apply(Arrays.asList(node), ruleCtx);
						rule6.apply(Arrays.asList(node), ruleCtx);
						rule7.apply(Arrays.asList(node), ruleCtx);
						rule8.apply(Arrays.asList(node), ruleCtx);
						rule9.apply(Arrays.asList(node), ruleCtx);
						rule10.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private static void startBestPracticesScan(List<SObject> classes, List<SObject> triggers) {

		Node node;
		ApexClass apexComponent;
		System.out.println("BEST PRACTICES SCAN - PMD");
		try {

			AvoidLogicInTriggerCustomRule rule1 = new AvoidLogicInTriggerCustomRule();
			AvoidGlobalModifierCustomRule rule2 = new AvoidGlobalModifierCustomRule();
			ApexUnitTestClassShouldHaveAssertsCustomRule rule3 = new ApexUnitTestClassShouldHaveAssertsCustomRule();
			ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule rule4 = new ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
				
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));

						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
						rule4.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

			for (SObject c : triggers) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

}