package com.infy.services.model;

import java.util.Calendar;
import java.util.List;

public class CyclomaticRuleBean {
	
	private String className;
	
	private int cycloMaticComplexity;
	
	private List<MethodDetailsBean> methodNames;
	
	private Integer Category;
	private String issueDesc;
	private Integer lineNumber;
	private String lastModifiedBy;
	private Calendar lastModifiedDate;
	private String issueType;
	private String componentType;
	private String sheetName;
	private String componentId;
	private String severity;
	
	
	
	public CyclomaticRuleBean(String className, int cycloMaticComplexity) {
		super();
		this.className = className; 
		this.cycloMaticComplexity = cycloMaticComplexity;
		
	}

	

	public CyclomaticRuleBean(String className, int cycloMaticComplexity, List<MethodDetailsBean> methodNames) {
		super();
		this.className = className;
		this.cycloMaticComplexity = cycloMaticComplexity;
		this.methodNames = methodNames;
	}



	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the cycloMaticComplexity
	 */
	public int getCycloMaticComplexity() {
		return cycloMaticComplexity;
	}

	/**
	 * @param cycloMaticComplexity the cycloMaticComplexity to set
	 */
	public void setCycloMaticComplexity(int cycloMaticComplexity) {
		this.cycloMaticComplexity = cycloMaticComplexity;
	}

	/**
	 * @return the methodNames
	 */
	public List<MethodDetailsBean> getMethodNames() {
		return methodNames;
	}

	/**
	 * @param methodNames the methodNames to set
	 */
	public void setMethodNames(List<MethodDetailsBean> methodNames) {
		this.methodNames = methodNames;
	}
	
}
