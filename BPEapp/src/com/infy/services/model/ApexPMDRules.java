package com.infy.services.model;

import com.infy.utility.BPEnforcerConstants;

public enum ApexPMDRules {
	SEE_ALL_DATA_TRUE_PMD("SeeAllData  set to true",true,BPEnforcerConstants.EMPTY_STR,true,100),
	ASSERTS_CHECK("Assert Missing",true,BPEnforcerConstants.EMPTY_STR,true,101),
	AVOID_GLOBAL("Avoid Global modifier",false,BPEnforcerConstants.EMPTY_STR,false,102),
	LOGIC_TRIGGER("Logic in Trigger",false,BPEnforcerConstants.EMPTY_STR,false,103),
	CLASS_NAMING_CONVENTION("Class Naming Convention",false,BPEnforcerConstants.EMPTY_STR,false,104),
	METHOD_NAMING_CONVENTION("Method Naming Convention",false,BPEnforcerConstants.EMPTY_STR,false,105),
	VARIABLE_NAMING_CONVENTION("Variable Naming Convention",false,BPEnforcerConstants.EMPTY_STR,false,106),
	HARDCODED("Hard coded ID found",false,BPEnforcerConstants.EMPTY_STR,false,107),
	DML_IN_LOOP("Avoid Dml Statements In Loops",true,BPEnforcerConstants.EMPTY_STR,false,108),
	SOQL_IN_LOOP("Avoid Soql Statements In Loops",true,BPEnforcerConstants.EMPTY_STR,false,109),
	SOSL_IN_LOOP("Avoid Sosl Statements In Loops",true,BPEnforcerConstants.EMPTY_STR,false,110),
	CYCLO_COMPLEXITY("CyclomaticComplexity",false,BPEnforcerConstants.EMPTY_STR,false,111),
	BAD_CRYPTO("Apex Bad Crypto",false,BPEnforcerConstants.EMPTY_STR,false,112),
	CRUD_VIOLATION("Apex CRUD Violation",false,BPEnforcerConstants.EMPTY_STR,false,113),
	APEX_CSRF("ApexCSRF",false,BPEnforcerConstants.EMPTY_STR,false,114),
	DANGEROUS_METHODS ("Apex Dangerous Methods",false,BPEnforcerConstants.EMPTY_STR,false,115),
	INSECURE_ENDPOINT("Apex Insecure Endpoint",false,BPEnforcerConstants.EMPTY_STR,false,116),
	OPEN_REDIRECT("Apex Open Redirect",false,BPEnforcerConstants.EMPTY_STR,false,117),
	SHARING_VIOLATIONS("Apex Sharing Violations",false,BPEnforcerConstants.EMPTY_STR,false,118),
	SOQL_INJECTION("Apex SOQL Injection",false,BPEnforcerConstants.EMPTY_STR,false,119),
	NAMED_CRED("Apex Suggest Using Named Cred",false,BPEnforcerConstants.EMPTY_STR,false,120),
	APEX_XSS_ESCAPE("Apex XSS From Escape False",false,BPEnforcerConstants.EMPTY_STR,false,121),
	APEX_XSS_URL("ApexXSSFromURLParam",false,BPEnforcerConstants.EMPTY_STR,false,122),
    METHODAME_MATCHES_ENCLOSING_CLSNAME( "Method name matches enclosing class name",false,BPEnforcerConstants.EMPTY_STR,false,123);
	 
	private final String sheetDesc;
	
	private final boolean error;
	
	private final String notes;
	
	private final boolean lineNumberAvailable;
	
	private final int category;

	
	/**
	 * @return the desc
	 */
	public String getSheetDesc() {
		return sheetDesc;
	}
	
	
	
	/**
	 * @return the isError
	 */
	public boolean isError() {
		return error;
	}

	
	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}



	/**
	 * @return the lineNumber
	 */
	public boolean isLineNumberAvailable() {
		return lineNumberAvailable;
	}

	

	/**
	 * @return the category
	 */
	public Integer getCategory() {
		return category;
	}



	/**
	 * 
	 * @param sheetDescription
	 */
	ApexPMDRules(String sheetDescription,boolean isError,String notesDesc,boolean isLineNumberAvailable,Integer categoryId){
		this.sheetDesc=sheetDescription;
		this.error = isError;
		this.notes = notesDesc;
		this.lineNumberAvailable = isLineNumberAvailable;
		this.category = categoryId;
	}
}
