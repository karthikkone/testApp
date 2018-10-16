package com.infy.services;

import java.util.List;

import com.infy.report.model.ReportType;
import com.infy.services.model.HealthCheckParameter;

public interface BPOutputGenerator {

	public boolean generateOutput(List<ReportType> reportList, HealthCheckParameter healthCheckParameter);
	
	

}
