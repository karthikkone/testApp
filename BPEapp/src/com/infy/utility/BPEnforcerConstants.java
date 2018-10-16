package com.infy.utility;

public interface BPEnforcerConstants {
	
	String COMMENTS_BTW_ANNOTATIONS = "Inappropriate commenting location";
	
	String SYSTEM_ASSERT_CHECK = "System.assert Missing";
	
	String LIMIT_OR_WHERE_IN_SELECT = "SOQL without WHERE or LIMIT";
	
	String TEST_START_AND_STOP = "DML_SOQL in Test classes";
	
	String RECOD_TYPE_INFO_CLASS = "RecordTypeInfo not used";
	
	String DML_SOQL_STATEMENT = "DML_SOQL Ex. Handling - Triggers";
	
	String SOQL_IN_EXECUTE = "SOQL in execute method in Batch classes";
	
	String HARDCODED_IDS = "Hardcoded Salesforce IDs";
	
	String LINES_OF_CODE = "Method size too large";
	
	String BODY_CODE="(hidden)";
	
	String ERROR_HANDLING = "DML_SOQL Ex. Handling - Apex Classes";
	
	String WITHOUT_SHARING_CLASSES = "Without sharing classes";
	
	String SYSTEM_DEBUG_COUNT = "System.debug count";
	
	String TRIGGER_NAMING_CONVENTION = "Trigger Naming Convention";
	
	String SYSTEM_RUN_AS_CHECK = "System.runAs Missing";
	
	String SCHEDULED_JOBS = "Jobs without generic user id";
	
	String INACTIVE_VR = "Inactive Validation Rules";
	
	String INACTIVE_WF = "Inactive WorkFlow Rules";
	
	String MULTIPLE_TRIGGERS = "Multiple Triggers";
	
	String DASHBOARD_COMPONENTS = "Dashboard Components";
	
	String SEE_ALL_DATA = "SeeAllData set to true";
	
	String EMPTY_STR = "";

//	 String ERROR ="ERROR";	
//	 String WARN ="WARNING";
	 
	 String LOW= "Low";
	 String MEDIUM="Medium";
	 String HIGH="High";
	 
	 
	 
	
	 String APEX_CLASS ="Apex Class";
	 
	 String APEX_TRIGGER ="Apex Trigger";
	 
	 String VALIDATION_RULE ="Validation Rule";
	 
	 String WORKFLOW_RULE ="WorkFlow Rule";
	 
	 String DASHBOARD ="Dashboard";
	 
	 String JOBS ="Scheduled Jobs";
	 
	 String YES_LABEL ="YES";
	 
	 Integer BATCH_SIZE =200;
	 
	 String NAMESPACEPREFIX ="''";
	 
	 String INACTIVE_USER = "Inactive User";
	 
	 String INACTIVE_TRIGGER = "Inactive Trigger";
	 
     String INLINE_JAVASCRIPT ="Inline Javascript in VF";
    
     String INLINE_CSS ="Inline CSS in VF";
     
     String DOCTYPE="Doctype";
     
     String APEX_PAGE ="Apex Page";
     
     String TEST_ANNOTATION =   "isTest";
     
     Integer TEST_COVERAGE_PERCENT =  75;
     
     String LOC_APEX =  "APEXLINESOFCODE";

	String TESTCLS_APEXCOVERAGE = "APEXTESTCOVERAGELESSCNT";
	
	String ACTIVE = "Active";
	
	String INACTIVE = "Inactive";
	
	String CYLCOMATIC_COMPLEXITY="CYLCOMATIC";
	
	String METHOD_NAMING_CONV ="Method Naming Convention";

	String CLASS_NAMING_CONV="Class Naming Convention";
	
	String VAR_NAMING_CONV="Variable Naming Convention";
	
	String DML_IN_LOOP="Avoid Dml Statements In Loops";
	String SOQL_IN_LOOP="Avoid Soql Statements In Loops";
	String SOSL_IN_LOOP="Avoid Sosl Statements In Loops";
	
	String HARDCODED="Hard coded ID found";
	String METHOD_SAMEAS_CONSTRUCTOR_NAME = "Method name matches enclosing class name";
	String ASSERTS_CHECK="Apex Unit Test classes should have Asserts";
	String SEE_ALL_DATA_TRUE_PMD = "Apex Unit test should not use SeeAllData True";
	String AVOID_GLOBAL = "Avoid Global modifier";
	String LOGIC_TRIGGER= "Logic in Trigger";
	String BAD_CRYPTO= "Apex Bad Crypto";
	String CRUD_VIOLATION= "Apex CRUD Violation";
	String APEX_CSRF="ApexCSRF";
	String DANGEROUS_METHODS ="Apex Dangerous Methods";
	String INSECURE_ENDPOINT= "Apex Insecure Endpoint";
	String OPEN_REDIRECT="Apex Open Redirect";
	String SHARING_VIOLATIONS="Apex Sharing Violations";
	String SOQL_INJECTION="Apex SOQL Injection";
	String NAMED_CRED="Apex Suggest Using Named Cred";
	String APEX_XSS_ESCAPE= "Apex XSS From Escape False";
	String APEX_XSS_URL="ApexXSSFromURLParam";
	
	String NESTED_IF="Avoid nested If stmts";
	String CYCLO_COMPLEXITY="CyclomaticComplexity";
	String TOO_MANY_FIELDS= "Too Many Fields";
	
	String BEST_PRACTICES= "Best Practices";
	String PERFORMANCE="Performance";	
	String SECURITY="Security";
	String DESIGN="Design";
	String CODESTYLE="CodeStyle";
	String ERROR_PRONE="Error prone";
	
	String MULTIPLE_FIELD_UPDATE="Multiple Field Update";
	String ERROR ="ERROR";
	String APEX_ASSERTS="Apex Asserts";
	
	
}
