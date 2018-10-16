package com.infy.bpe.core;

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
import com.sforce.soap.enterprise.EnterpriseConnection;
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

public class LoginUtility {

	
	static final String PROXY_REQUIRED = "YES";

		public static void main(String[] args) throws InterruptedException, IOException {
	
		
		try{
				connectSalesforce();
			

		} catch (ConnectionException e1) {
			System.err.println("ConnectionException " + e1.getMessage());
			e1.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void connectSalesforce()throws ConnectionException, InterruptedException, IOException, Exception {
		
		String username="anvesh.vemula@agileprov1.com";
		String password="sfdc_hyd18zN3dCv2zSeH9T7WMTSySpPA";
		
		
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		
		
		ConnectorConfig eConfig = config;
		eConfig.setManualLogin(false);
		eConfig.setAuthEndpoint("https://login.salesforce.com" + "/services/Soap/c/" + "40.0");
		System.out.println("https://login.salesforce.com"+ "/services/Soap/c/" + "40.0");
		EnterpriseConnection ent = com.sforce.soap.enterprise.Connector.newConnection(eConfig);
		System.out.println(ent.getConfig().getAuthEndpoint());
		
		ConnectorConfig pConfig = config;
		pConfig.setManualLogin(true);
		pConfig.setAuthEndpoint("https://login.salesforce.com" + "/services/Soap/u/" + "40.0");
		pConfig.setServiceEndpoint(eConfig.getServiceEndpoint().replaceAll("/c/", "/u/"));
		PartnerConnection pCon = com.sforce.soap.partner.Connector.newConnection(pConfig);

		com.sforce.soap.partner.LoginResult lrp = pCon.login(username, password);
		pConfig.setSessionId(lrp.getSessionId());
		pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/T/"));
		pConfig.setAuthEndpoint(pConfig.getAuthEndpoint().replace("/u/", "/T/"));

		ToolingConnection con = com.sforce.soap.tooling.Connector.newConnection(pConfig);

		System.out.println(" ORG DETAILS: " + con.getUserInfo().getOrganizationId()+ " NAME: " + con.getUserInfo().getOrganizationName());

		ArrayList<SObject> classes = ToolingOperations.retrieveToolingComponents(con, "ApexClass");
		
		pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/m/"));
		MetadataConnection mcon = com.sforce.soap.metadata.Connector.newConnection(pConfig);


	}


}

	
	

	



	