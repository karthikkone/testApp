package com.infy.report.model;

import java.util.Calendar;

public class ReportType {

	private String className;
	private Integer Category;
	private String issueDesc;
	private Integer lineNumber;
	private String lastModifiedBy;
	private Calendar lastModifiedDate;
	private String issueType;
	private String componentType;
	private String sheetName;
	private String componentId;
	private String methodName;
	private String variableName;
	private String severity;
    private Integer cycloComplexity;
    
    private String ruleCategory;
	


	public String getRuleCategory() {
		return ruleCategory;
	}

	public void setRuleCategory(String ruleCategory) {
		this.ruleCategory = ruleCategory;
	}

	public Integer getCycloComplexity() {
		return cycloComplexity;
	}

	public void setCycloComplexity(Integer cycloComplexity) {
		this.cycloComplexity = cycloComplexity;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the sheetName
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            the sheetName to set
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * @return the issueDesc
	 */
	public String getIssueDesc() {
		return issueDesc;
	}

	/**
	 * @param issueDesc
	 *            the issueDesc to set
	 */
	public void setIssueDesc(String issueDesc) {
		this.issueDesc = issueDesc;
	}

	/**
	 * @return the issueType
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * @param issueType
	 *            the issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	/**
	 * @return the componentType
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * @param componentType
	 *            the componentType to set
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId
	 *            the componentId to set
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public void setCategory(Integer category) {
		Category = category;
	}

	public Integer getCategory() {
		return Category;
	}

	public ReportType(String lastModifiedBy, Calendar lastModifiedDate) {
		super();
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	

	

	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	/**
	 * @param lastModifiedBy
	 *            the lastModifiedBy to set
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Calendar getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Calendar lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public ReportType() {
		super();
	}

	public ReportType(String className, int category, String issueDesc, int lineNumber) {
	
		super();
		this.className = className;
		this.issueDesc = issueDesc;
		this.lineNumber=lineNumber;
		this.Category = category;
	}
	//componentName, 20,"Annotation is not present above method decalartion at line ",lineNo

	public ReportType(String className, Integer category, String issueDesc, Integer lineNumber) {
		super();
		this.className = className;
		// this.line = line;
		this.issueDesc = issueDesc;
		this.lineNumber = lineNumber;
		this.Category = category;

	}
	public ReportType(String className,String issueType,String issueDesc) {
		//created for Classnaming convention
		super();
		this.className = className;
		this.issueDesc = issueDesc;
		this.issueType=issueType;
	}

	public ReportType(String className, String methodName, Integer category, String issueDesc, String issueType) {
		//created for Method Naming convention
		super();
		this.className = className;
		this.issueDesc = issueDesc;
		this.issueType=issueType;
		this.Category = category;

	}
	

	

	
}