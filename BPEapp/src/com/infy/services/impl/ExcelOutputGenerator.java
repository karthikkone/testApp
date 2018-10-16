package com.infy.services.impl;

import java.io.IOException;
import java.util.List;

import com.infy.bpe.core.DataStore;
import com.infy.report.model.ReportType;
import com.infy.reports.generator.ReportGeneration;
import com.infy.services.BPOutputGenerator;
import com.infy.services.model.HealthCheckParameter;

public class ExcelOutputGenerator implements BPOutputGenerator {
	
	public ExcelOutputGenerator(){
		
	}
	
	@Override
	public boolean generateOutput(List<ReportType> reportList,HealthCheckParameter healthCheckParameter) {
		System.out.println("IN ExcelOutputGenerator");
		
		try {
			ReportGeneration.generateExcelOutput(reportList,healthCheckParameter);
		} catch (IOException e) {
			System.out.println("Error IOException : "
					+ e.getMessage());
			e.printStackTrace();
			return false;
			
		}
		System.out.println("Execution completed successfully! Results are written to: " + DataStore.OUTPUT_FILENAME);
		return true;
	}



}
