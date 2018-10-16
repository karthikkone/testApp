package com.infy.services.model;

public enum HealthCheckParams {
	

	SECURITYSCORE("Security Health Risk Score"),
	VFCUSTOMLINES("# of lines of custom code in VisualForce Page"),
	APEXCUSTOMLINES("# of lines of custom code in Apex"),
	CODECOVERAGE("# of classes with coverage less than 75%"),
	SOQLSTATEMENT("# of SOQL statements"),
	DMLSTATEMENT("# of DML statements"),
	TOTALAPEXCLASS("# of Apex Classes"),
	TOTALAPEXPAGES("# of Visualforce Pages"),
	TOTALAPEXCOMPONENTS("# of Visualforce Components");
	
	private final String desc;

	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}


	HealthCheckParams(String description){
		this.desc=description;
	}
	
}
