package com.infy.services;

import java.util.List;

import com.infy.report.model.ReportType;
import com.infy.services.model.HealthCheckParameter;

//testing heroku
public class BPEnforcerContext {

	private HealthCheckParameter healthCheckParameter;


	private BPOutputGenerator bpOutputGenerator;

	/**
	 * @return the bpOutputGenerator
	 */
	public BPOutputGenerator getBpOutputGenerator() {
		return bpOutputGenerator;
	}

	/**
	 * @return the healthCheckParameter
	 */
	public HealthCheckParameter getHealthCheckParameter() {
		return healthCheckParameter;
	}

	/**
	 * @param healthCheckParameter
	 *            the healthCheckParameter to set
	 */
	public void setHealthCheckParameter(HealthCheckParameter healthCheckParameter) {
		this.healthCheckParameter = healthCheckParameter;
	}

	/**
	 * @param bpOutputGenerator
	 *            the bpOutputGenerator to set
	 */
	public void setBpOutputGenerator(BPOutputGenerator bpOutputGenerator) {
		this.bpOutputGenerator = bpOutputGenerator;
	}

	// use the strategy
	public void createOutput(List<ReportType> reportTypes) {
		bpOutputGenerator.generateOutput(reportTypes, healthCheckParameter);
	}
	
}
