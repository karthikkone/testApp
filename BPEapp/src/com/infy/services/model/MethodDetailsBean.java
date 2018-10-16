package com.infy.services.model;

public class MethodDetailsBean {
	
	private String methodName;
	
	private int methodComplexity;

	
	public MethodDetailsBean(String methodName, int methodComplexity) {
		super();
		this.methodName = methodName;
		this.methodComplexity = methodComplexity;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the methodComplexity
	 */
	public int getMethodComplexity() {
		return methodComplexity;
	}

	/**
	 * @param methodComplexity the methodComplexity to set
	 */
	public void setMethodComplexity(int methodComplexity) {
		this.methodComplexity = methodComplexity;
	}
	
	
	
}