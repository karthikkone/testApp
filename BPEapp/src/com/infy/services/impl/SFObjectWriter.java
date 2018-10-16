package com.infy.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.infy.bpe.core.DataStore;
import com.infy.report.model.ReportType;
import com.infy.services.BPOutputGenerator;
import com.infy.services.model.HealthCheckParameter;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.BPEnforcerUtility;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class SFObjectWriter implements BPOutputGenerator {

	private String organisationID;

	private String organisationName;
	private String remoteAuth;
	static int count=1;

	public SFObjectWriter(String organisationID, String organisationName,String RemoteAuth) {
		super();
		this.organisationID = organisationID;
		this.organisationName = organisationName;
		this.remoteAuth=RemoteAuth;
	}

	@Override
	public boolean generateOutput(List<ReportType> reportList, HealthCheckParameter healthCheckParameter) {

		System.out.println("Report scan results  " + reportList.size());

		writeScanResults(BPEnforcerUtility.getConnection(), reportList);

		return true;
	}

	

	



	public void writeScanResults(PartnerConnection pCon, List<ReportType> reportList) {
		try {
			System.out.println("bpe: Writing scan results to Agile Pro Org ");

			SObject finding = null;
			List<SObject> findingList;

			String scanId = BPEnforcerUtility.insertScanDtls(organisationID, organisationName,remoteAuth);
			List<List<ReportType>> batchesList = BPEnforcerUtility.getBatches(reportList,
					BPEnforcerConstants.BATCH_SIZE);
			System.out.println("-------->" + batchesList.size());
			if (null != scanId) {
				for (List<ReportType> reportBatches : batchesList) {
					findingList = new ArrayList<SObject>();
					System.out.println("-----reportBatches --->" + reportBatches.size());
					for (ReportType reportType : reportBatches) {
						finding = new SObject();
						
						DataStore.NAMESPACE="AGP__";
						finding.setType(DataStore.NAMESPACE + "AGP_Scan_Finding__c");
						finding.setField(DataStore.NAMESPACE + "Component_Id__c", reportType.getComponentId());
						finding.setField(DataStore.NAMESPACE + "Component_Name__c", reportType.getClassName());
						finding.setField(DataStore.NAMESPACE + "Component_Type__c", reportType.getComponentType());
						finding.setField(DataStore.NAMESPACE + "Description__c", reportType.getIssueDesc());
						finding.setField(DataStore.NAMESPACE + "Issue_Type__c", reportType.getIssueType());
						finding.setField(DataStore.NAMESPACE + "Line_Number__c", reportType.getLineNumber());
						finding.setField(DataStore.NAMESPACE + "Comp_Last_Modified_By__c",reportType.getLastModifiedBy());
						finding.setField(DataStore.NAMESPACE + "Comp_Last_Modified_Date__c",reportType.getLastModifiedDate());
						finding.setField(DataStore.NAMESPACE + "Cyclomatic_Complexity__c", reportType.getCycloComplexity());
						finding.setField(DataStore.NAMESPACE + "Rule_Category__c", reportType.getRuleCategory());
						finding.setField(DataStore.NAMESPACE + "Scan__c", scanId);
						
						
						

						finding.setField("Name", reportType.getSheetName());

						findingList.add(finding);
					}
					SaveResult[] findingSaveResults = pCon.create(findingList.toArray(new SObject[reportBatches.size()]));// saving
																							// findings
																							// into
																							// org
					for (int i = 0; i < findingSaveResults.length; i++) {

						if (!findingSaveResults[i].isSuccess()) {
							com.sforce.soap.partner.Error[] errors = findingSaveResults[i].getErrors();
							for (int j = 0; j < errors.length; j++) {
								System.out.println("ERROR creating record: " + errors[j].getMessage());
							}
						}
					}
				}
			}
			System.out.println("Writing scan results to Agile Pro completed.. ");
		} catch (ConnectionException e) {
			System.err.println("ConnectionException " + e.getMessage());
			e.printStackTrace();
		}
	}

	

}
