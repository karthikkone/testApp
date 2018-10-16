package com.infy.services.model;

import com.infy.utility.BPEnforcerConstants;

public enum CustomRulesBPE {
	
	DASHBOARD_COMPONENTS("Dashboard Components",false,BPEnforcerConstants.EMPTY_STR,false),
	MULTIPLE_TRIGGERS("Multiple Triggers",true,BPEnforcerConstants.EMPTY_STR,false),
	INACTIVE_VR("Inactive Validation Rules",false,BPEnforcerConstants.EMPTY_STR,false),
	SCHEDULED_JOBS("Jobs without generic user id",false,BPEnforcerConstants.EMPTY_STR,false),
	SYSTEM_RUN_AS_CHECK("System.runAs Missing",false,BPEnforcerConstants.EMPTY_STR,false),
	TRIGGER_NAMING_CONVENTION("Trigger Naming Convention",false,BPEnforcerConstants.EMPTY_STR,false),
	SYSTEM_DEBUG_COUNT("System.debug count",true,"Note:Number of System.debug statements should not exceed 5",true),
	ERROR_HANDLING("DML_SOQL Ex. Handling - Apex Classes",true,BPEnforcerConstants.EMPTY_STR,true),
	LINES_OF_CODE("Method size too large",false,"Note:Number of lines in a method should not exceed 45",true),
	SOQL_IN_EXECUTE("SOQL in execute method in Batch classes",false,BPEnforcerConstants.EMPTY_STR,true),
	DML_SOQL_STATEMENT("DML_SOQL Ex. Handling - Triggers",true,BPEnforcerConstants.EMPTY_STR,true),
	RECOD_TYPE_INFO_CLASS("RecordTypeInfo not used",true,BPEnforcerConstants.EMPTY_STR,true),
	LIMIT_OR_WHERE_IN_SELECT("SOQL without WHERE or LIMIT",false,BPEnforcerConstants.EMPTY_STR,true),
	TEST_START_AND_STOP("DML_SOQL in Test classes",false,BPEnforcerConstants.EMPTY_STR,true),
	COMMENTS_BTW_ANNOTATIONS("Inappropriate commenting location",true,BPEnforcerConstants.EMPTY_STR,true),
	DOCTYPE("Doctype",true,BPEnforcerConstants.EMPTY_STR,true),
	INACTIVE_USER( "Inactive User",false,BPEnforcerConstants.EMPTY_STR,false),
    INLINE_JAVASCRIPT ("Inline Javascript in VF",true,BPEnforcerConstants.EMPTY_STR,true),   
    INLINE_CSS("Inline CSS in VF",true,BPEnforcerConstants.EMPTY_STR,true),
	INACTIVE_TRIGGER ("Inactive Trigger",false,BPEnforcerConstants.EMPTY_STR,false),
	INACTIVE_WF("Inactive WorkFlow Rules",false,BPEnforcerConstants.EMPTY_STR,false),
	MULTIPLE_FIELD_UPDATE("Multiple Field Update",true,BPEnforcerConstants.EMPTY_STR,false);
	
	private final String sheetDesc;
	
	private final boolean error;
	
	private final String notes;
	
	private final boolean lineNumberAvailable;

	
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
	 * 
	 * @param sheetDescription
	 */
	CustomRulesBPE(String sheetDescription,boolean isError,String notesDesc,boolean isLineNumberAvailable){
		this.sheetDesc=sheetDescription;
		this.error = isError;
		this.notes = notesDesc;
		this.lineNumberAvailable = isLineNumberAvailable;
	}
}
