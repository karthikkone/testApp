package com.infy.bpe.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.infy.report.model.ReportType;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;
import com.sforce.soap.tooling.QueryResult;
import com.sforce.soap.tooling.ToolingConnection;
import com.sforce.soap.tooling.metadata.FlowRecordUpdate;
import com.sforce.soap.tooling.sobject.ApexClass;
import com.sforce.soap.tooling.sobject.ApexPage;
import com.sforce.soap.tooling.sobject.ApexTrigger;
import com.sforce.soap.tooling.sobject.Flow;
import com.sforce.soap.tooling.sobject.SObject;
import com.sforce.soap.tooling.sobject.ValidationRule;
import com.sforce.soap.tooling.sobject.WorkflowFieldUpdate;
import com.sforce.soap.tooling.sobject.WorkflowRule;
import com.sforce.ws.ConnectionException;

public class ToolingOperations {

	public static ArrayList<SObject> retrieveToolingComponents(ToolingConnection con, String type)
			throws ConnectionException {
		// System.out.println("Inside RTC new method");
		QueryResult qr = null;
		if (!type.equalsIgnoreCase("MenuItem"))
			qr = con.query("SELECT Id FROM " + type);
		// qr = con.query("SELECT Id FROM " + type);

		else
			qr = con.query("SELECT AppId FROM " + type + " WHERE MenuType='AppSwitcher'");
		SObject[] init = qr.getRecords();
		int total_counter = 0;
		int batches = init.length;
		ArrayList<SObject> records = new ArrayList<SObject>();
		StringBuilder query = new StringBuilder();
		if (type.equalsIgnoreCase("apexclass")) {
			query = new StringBuilder(
					"SELECT Id, Name, Body ,SymbolTable,lastmodifiedbyid, lastmodifieddate,Status,LastModifiedBy.name FROM "
							+ type + " where NAMESPACEPREFIX="+ BPEnforcerConstants.NAMESPACEPREFIX);
		} else if (type.equalsIgnoreCase("apextrigger")) {
			query = new StringBuilder("SELECT Id, Name, Body ,lastmodifiedbyid, lastmodifieddate FROM " + type
					+ " WHERE NAMESPACEPREFIX="+ BPEnforcerConstants.NAMESPACEPREFIX +" AND ID IN (");
		} else if (type.equalsIgnoreCase("apexpage")) {
			query = new StringBuilder("SELECT Id, Name, markup,lastmodifiedbyid, lastmodifieddate FROM " + type
					+ " WHERE NAMESPACEPREFIX="+BPEnforcerConstants.NAMESPACEPREFIX);
		} else if (type.equalsIgnoreCase("ApexComponent")) {
			query = new StringBuilder("SELECT Id, Name, markup,lastmodifiedbyid, lastmodifieddate FROM " + type
					+ " WHERE NAMESPACEPREFIX="+BPEnforcerConstants.NAMESPACEPREFIX);
		} else if (type.equalsIgnoreCase("customobject")) {
			query = new StringBuilder("SELECT Id, developerName,Description,LastModifiedById, lastmodifieddate FROM "
					+ type + " WHERE NAMESPACEPREFIX="+ BPEnforcerConstants.NAMESPACEPREFIX+" AND ID IN (");
		} else if (type.equalsIgnoreCase("workflowrule")) {
			query = new StringBuilder(
					"SELECT Id, Name,TableEnumOrId,NamespacePrefix,LastModifiedById, lastmodifieddate,LastModifiedBy.name FROM "
							+ type + " WHERE NAMESPACEPREFIX="+BPEnforcerConstants.NAMESPACEPREFIX+" AND ID IN (");
		} else if (type.equalsIgnoreCase("RemoteProxy")) {
			query = new StringBuilder("SELECT Id,SiteName,Description,EndpointUrl FROM " + type + " WHERE ID IN (");
		} else if (type.equalsIgnoreCase("Profile")) {
			query = new StringBuilder("SELECT Id,Name FROM " + type + " WHERE ID IN (");
		} else if (type.equalsIgnoreCase("USER")) {
			query = new StringBuilder("SELECT Id,Name FROM User " + type);
		} else if (type.equalsIgnoreCase("ValidationRule")) {
			query = new StringBuilder(
					"Select Id,ValidationName,Active,Description,EntityDefinition.DeveloperName,ErrorDisplayField, ErrorMessage FROM "
							+ type + " WHERE ID IN (");
		} else if (type.equalsIgnoreCase("WorkflowFieldUpdate")) {

			query = new StringBuilder("SELECT Id FROM " + type + " WHERE NAMESPACEPREFIX="
					+ BPEnforcerConstants.NAMESPACEPREFIX + " AND ID IN (");
		} else if (type.equalsIgnoreCase("Flow")) {

			query = new StringBuilder("SELECT Id FROM " + type + " WHERE Status='Active' AND ID IN (");
		}

		if (!type.equalsIgnoreCase("CustomField") && !type.equalsIgnoreCase("apexclass") && !type.equalsIgnoreCase(
				"ApexComponent")/* && !type.equalsIgnoreCase("apextrigger") */ && !type.equalsIgnoreCase("USER")
				&& !type.equalsIgnoreCase("apexpage")) {
			while (total_counter < batches) {
				query.append("'" + init[total_counter].getId() + "',");
				total_counter++;
			}
			if (total_counter > 0) {
				query.deleteCharAt(query.length() - 1);
				query.append(")");
			} else {
				// if no records found append empty string
				query.append("'')");
			}

		}
		System.out.println("query  " + query);
		QueryResult qres = con.query(query.toString());

		if (type.equalsIgnoreCase("user") || type.equalsIgnoreCase("apexclass") || type.equalsIgnoreCase("apexpage")) {
			boolean done = false;
			if (qres.getSize() > 0) {
				// System.out.println("Logged-in user can see a total of " +
				// qres.getSize() + " contact records.");
				while (!done) {
					SObject[] rec = qres.getRecords();
					for (int i = 0; i < rec.length; ++i) {

						records.add(rec[i]);
					}
					if (qres.isDone()) {
						done = true;
					} else {
						System.out.println("Doing QueryMore!!! wait");
						qres = con.queryMore(qres.getQueryLocator());
					}
				}
			}
		} else {
			List<String> lstIds = new ArrayList<String>();
			List<StringBuilder> lstStrngBuilder = new ArrayList<>();
			for (SObject s : qres.getRecords()) {
				records.add(s);
				lstIds.add(s.getId());
			}
			if (type.equalsIgnoreCase("apextrigger")) {
				records = new ArrayList<SObject>();
				for (String strIds : lstIds) {
					query = new StringBuilder(
							"SELECT Id,Name, Body ,EntityDefinition.DeveloperName,EntityDefinition.FullName,EntityDefinitionId,UsageAfterDelete,UsageBeforeDelete,UsageAfterInsert,UsageAfterUpdate,UsageBeforeInsert,UsageBeforeUpdate,lastmodifiedbyid, lastmodifieddate,LastModifiedBy.name FROM "
									+ type + " WHERE ID = '" + strIds + "'");

					lstStrngBuilder.add(query);

				}

			} else if (type.equalsIgnoreCase("Workflowrule")) {
				for (String strIds : lstIds) {
					query = new StringBuilder(
							"SELECT Id,Name,metadata,FullName,TableEnumOrId,NamespacePrefix,LastModifiedById, lastmodifieddate,LastModifiedBy.name FROM  "
									+ type + " WHERE ID = '" + strIds + "'");
					qres = con.query(query.toString());

					if (null != qres.getRecords() && qres.getRecords().length > 0) {
						records.add(qres.getRecords()[0]);
					}

				}
			} else if (type.equalsIgnoreCase("WorkflowFieldUpdate")) {
				records = new ArrayList<SObject>();
				for (String strIds : lstIds) {
					query = new StringBuilder(
							"SELECT Id,Name,metadata,EntityDefinition.DeveloperName,EntityDefinition.FullName FROM "
									+ type + " WHERE ID = '" + strIds + "'");
				
					qres = con.query(query.toString());
					if (null != qres.getRecords() && qres.getRecords().length > 0) {
						records.add(qres.getRecords()[0]);
					}

				}

			} else if (type.equalsIgnoreCase("Flow")) {
				records = new ArrayList<SObject>();
				for (String strIds : lstIds) {
					query = new StringBuilder("SELECT Id,metadata,ProcessType,Status,FullName  FROM " + type
							+ " WHERE ID = '" + strIds + "'");					
					qres = con.query(query.toString());

					if (null != qres.getRecords() && qres.getRecords().length > 0) {
						records.add(qres.getRecords()[0]);
					}

				}

			}

		}

		return records;
	}

	public static ArrayList<ReportType> findquery(ArrayList<SObject> classes) throws IOException

	{
		System.out.println("Checking test classes SeeAllData annotation...");
		String REGEX = "(.*)@ISTEST(.*)";
		String REGEX3 = "(.*)seealldata=true(.*)";

		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ApexClass apexComponent = null;
		for (SObject c : classes) {
			apexComponent = null;
			int lineNo = 1;
			String INPUT = null;
			if (c instanceof ApexClass) {
				if (((ApexClass) c).getName().startsWith("CINTest")) {
					break;
				}
				INPUT = ((ApexClass) c).getBody();
				apexComponent = ((ApexClass) c);
			}

			BufferedReader br = new BufferedReader(new StringReader(INPUT));
			String line;
			boolean isTestFound = false;
			while ((line = br.readLine()) != null) {

				Matcher m1 = p1.matcher(line);

				if (m1.matches()) {
					isTestFound = true;
					break;

				}
			}
			if (isTestFound) {
				lineNo = 1;
				boolean allData = false;
				br = new BufferedReader(new StringReader(INPUT));
				while ((line = br.readLine()) != null) {

					Matcher m3 = p3.matcher(line);

					if (m3.matches() && !allData) {
						allData = true;

					}
					lineNo++;
				}

				if (allData) {
					reporttypelist.add(setClassReportDtls(apexComponent, 1, "SeeAllData is set to true", lineNo,
							BPEnforcerConstants.MEDIUM, BPEnforcerConstants.SEE_ALL_DATA,
							BPEnforcerConstants.BEST_PRACTICES));
				}
			}

		}

		return reporttypelist;
	}

	private static ReportType setClassReportDtls(ApexClass apexComponent, Integer category, String issueDesc,
			int lineNo, String issueType, String sheetName, String ruleCategory) {
	
		ReportType reportType = new ReportType();
		reportType.setClassName(apexComponent.getName());
		reportType.setCategory(category);
		reportType.setIssueDesc(issueDesc);
		reportType.setLineNumber(lineNo);
		reportType.setComponentId(apexComponent.getId());
		reportType.setIssueType(issueType);
		reportType.setComponentType(BPEnforcerConstants.APEX_CLASS);
		reportType.setSheetName(sheetName);
		reportType.setRuleCategory(ruleCategory);
		reportType.setLastModifiedBy(apexComponent.getLastModifiedBy().getName());
		reportType.setLastModifiedDate(apexComponent.getLastModifiedDate());

		return reportType;
	}

	public static ReportType setClassReportDtls(String className, int category, String issueDesc, int lineNo,
			String issueType, String sheetName, String severity, String ruleCategory) {

	
		ReportType reportType = new ReportType();
		reportType.setClassName(className);
		reportType.setCategory(category);
		reportType.setIssueDesc(issueDesc);
		reportType.setLineNumber(lineNo);
		reportType.setComponentType("Apex Class");
		reportType.setSheetName(sheetName);
		reportType.setSeverity(severity);
		reportType.setIssueType(issueType);
		reportType.setRuleCategory(ruleCategory);
		ReportType lastModifiedDtls = CodeAnalyser.getApexClassModifiedDetails().get(className);	
		reportType.setLastModifiedBy((null != lastModifiedDtls) ? lastModifiedDtls.getLastModifiedBy() : null);
		reportType.setLastModifiedDate((null != lastModifiedDtls) ? lastModifiedDtls.getLastModifiedDate() : null);
		if (lineNo > 0)
			reportType.setLineNumber(lineNo);
		return reportType;
	}

	public static ReportType setCycloBeanDtls(String className, int category, String issueDesc, int cycloComplexity,
			String issueType, String sheetName, String ruleCategory) {

		ReportType reportType = new ReportType();
		reportType.setClassName(className);
		reportType.setCategory(CategoryConstants.CYLCOMATIC_COMPLEXITY);
		reportType.setIssueDesc(issueDesc);
		reportType.setComponentType("Apex Class");
		reportType.setSheetName(sheetName);
		reportType.setRuleCategory(ruleCategory);
		reportType.setIssueType(issueType);
		reportType.setCycloComplexity(cycloComplexity);
		ReportType lastModifiedDtls = CodeAnalyser.getApexClassModifiedDetails().get(className);
		reportType.setLastModifiedBy((null != lastModifiedDtls) ? lastModifiedDtls.getLastModifiedBy() : null);
		reportType.setLastModifiedDate((null != lastModifiedDtls) ? lastModifiedDtls.getLastModifiedDate() : null);
		return reportType;
	}

	private static ReportType setPagesReportDtls(ApexPage apexComponent, Integer category, String issueDesc, int lineNo,
			String issueType, String sheetName, String ruleCategory) {	
		ReportType reportType = new ReportType();
		reportType.setClassName(apexComponent.getName());
		reportType.setCategory(category);
		reportType.setIssueDesc(issueDesc);
		reportType.setLineNumber(lineNo);
		reportType.setLastModifiedDate(apexComponent.getLastModifiedDate());
		reportType.setComponentId(apexComponent.getId());
		reportType.setIssueType(issueType);
		reportType.setComponentType(BPEnforcerConstants.APEX_PAGE);
		reportType.setSheetName(sheetName);
		reportType.setRuleCategory(ruleCategory);
		return reportType;
	}

	public static ArrayList<ReportType> findSystemAssert(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking test classes for System.assert statements...");
		String REGEX = "(.*)@ISTEST(.*)";
		String REGEX2 = "(.*)System.assert(.*)";
		String REGEX3 = ".*(public|private|protected|global)*(testMethod).*";
		String REGEX4 = ".*(public|private|protected|global|static)\\s+(?!class)+.*";
		String OPEN = "(.*)\\{(.*)";
		String CLOSE = "(.*)\\}(.*)";
		String methodName = null;
		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);
		Pattern pClose = Pattern.compile(CLOSE, Pattern.CASE_INSENSITIVE);
		Pattern pOpen = Pattern.compile(OPEN, Pattern.CASE_INSENSITIVE);
		int openBracket = 0;
		boolean systemAssertPresent = false;
		boolean isMethodFound = false;
		String previousLine = "";
		ApexClass apexComponent = null;
		int lineNo;
		String INPUT;
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		for (SObject c : classes) {
			apexComponent = null;
			lineNo = 1;
			INPUT = null;
			if (c instanceof ApexClass) {
				if (((ApexClass) c).getName().startsWith("CINTest")) {
					break;
				}
				INPUT = ((ApexClass) c).getBody();
				apexComponent = ((ApexClass) c);
			}

			BufferedReader br = new BufferedReader(new StringReader(INPUT));
			String line;
			boolean isTestFound = false;
			while ((line = br.readLine()) != null) {

				Matcher m1 = p1.matcher(line);

				if (m1.matches()) {
					isTestFound = true;
					break;

				}
			}
			if (isTestFound) {
				lineNo = 1;
				br = new BufferedReader(new StringReader(INPUT));
				method: while ((line = br.readLine()) != null) {
					lineNo++;
					systemAssertPresent = false;
					isMethodFound = false;
					Matcher testAnnotation = p1.matcher(previousLine);
					previousLine = "";
					openBracket = 0;

					if (Pattern.matches(REGEX4, line) && testAnnotation.matches()) {
						isMethodFound = true;
					}
					Matcher m2 = p3.matcher(line);
					testAnnotation = p1.matcher(line);

					if (m2.matches() || testAnnotation.matches() || isMethodFound) {
						previousLine = line;
						methodName = line;
						Matcher mClose = pClose.matcher(line);
						Matcher mOpen = pOpen.matcher(line);
						if (mOpen.matches()) {
							++openBracket;
						}

						while ((openBracket > 0 || isMethodFound) && (line = br.readLine()) != null) {

							mOpen = pOpen.matcher(line);
							if (mOpen.matches()) {
								++openBracket;
							}
							methodName = Pattern.matches(REGEX4, line) ? line : methodName;
							Matcher systemAssert = p2.matcher(line);
							if (systemAssert.matches()) {

								systemAssertPresent = true;
								break;
							}

							mClose = pClose.matcher(line);
							if (mClose.matches()) {
								--openBracket;

								if (openBracket == 0 && !systemAssertPresent) {
									reporttypelist.add(setClassReportDtls(apexComponent, 19,
											"System.assert not present inside method " + getMethodName(methodName),
											lineNo, BPEnforcerConstants.LOW, BPEnforcerConstants.SYSTEM_ASSERT_CHECK,
											BPEnforcerConstants.BEST_PRACTICES));
									continue method;
								}

							}
						}

					}

				}
			}

		}

		return reporttypelist;
	}

	/*
	 * public static ArrayList<ReportType> multipletriggers(ArrayList<SObject>
	 * triggers) { System.out.println(
	 * "Checking if there are multiple triggers on each object in the org...");
	 * HashMap<String, Integer> countmap = new HashMap<String, Integer>();
	 * ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>(); for
	 * (SObject s : triggers) { ApexTrigger trigger = (ApexTrigger) s; int
	 * startIndex = trigger.getBody().indexOf("trigger " + trigger.getName() +
	 * " on"); int offset = ("trigger " + trigger.getName() + " on").length();
	 * String obj = trigger.getBody().substring(startIndex + offset,
	 * trigger.getBody().indexOf("(", startIndex)); obj = obj.trim(); //
	 * System.out.println(trigger.getName() + "----" + obj); Integer cnt =
	 * countmap.get(obj); if (cnt == null) cnt = 0; cnt++; countmap.put(obj,
	 * cnt); } for (String s : countmap.keySet()) {
	 * 
	 * if (countmap.get(s) > 1) { reporttypelist.add(new ReportType(s, 3,
	 * "Multiple Trigger on Object", 0)); } } return reporttypelist; }
	 */

	public static ArrayList<ReportType> multipletriggers(ArrayList<SObject> triggers) {
		System.out.println("Checking if there are multiple triggers on each object in the org...");
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, ApexTrigger> triggerDetails = new HashMap<String, ApexTrigger>();
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		Pattern triggerNamePtrn = Pattern.compile("(?<=on\\s).*?(?=\\()", Pattern.CASE_INSENSITIVE);
		Matcher triggerWordMatcher;
		ApexTrigger trigger;
		String triggerObj = null;
		Integer cnt = null;
		for (SObject apexTrigger : triggers) {
			triggerObj = null;
			trigger = (ApexTrigger) apexTrigger;
			// System.out.println("TRIGGGER STATUS "+trigger.getStatus()+ "
			// MODIFIED BY "+trigger.getLastModifiedBy().getName()+"
			// "+trigger.getLastModifiedDate() );
			triggerWordMatcher = triggerNamePtrn.matcher(trigger.getBody());
			if (BPEnforcerConstants.ACTIVE.equalsIgnoreCase(trigger.getStatus()) && triggerWordMatcher.find()) {
				triggerObj = triggerWordMatcher.group().trim();
				cnt = countmap.get(triggerObj);
				if (cnt == null)
					cnt = 0;
				cnt++;
				countmap.put(triggerObj, cnt);
				triggerDetails.put(triggerObj, trigger);
			}

		}
		for (String triggerObjName : countmap.keySet()) {
			if (countmap.get(triggerObjName) > 1) {
				reporttypelist.add(setTriggerReportDtls(triggerDetails.get(triggerObjName),
						CategoryConstants.MUTIPLE_TRIGGERS, "Multiple Trigger on Object", 0, BPEnforcerConstants.MEDIUM,
						BPEnforcerConstants.MULTIPLE_TRIGGERS, BPEnforcerConstants.BEST_PRACTICES));

			}
		}
		return reporttypelist;
	}

	public static ArrayList<ReportType> triggerNameCheck(ArrayList<SObject> triggers) {
		System.out.println("Checking if triggers are named with the word 'Trigger' as suffix...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		Pattern triggerNamePtrn = Pattern.compile("(?<=on\\s).*?(?=\\()", Pattern.CASE_INSENSITIVE);
		Matcher triggerWordMatcher;
		ApexTrigger trigger;
		for (SObject s : triggers) {
			trigger = (ApexTrigger) s;
			triggerWordMatcher = triggerNamePtrn.matcher(trigger.getBody());
			if (BPEnforcerConstants.ACTIVE.equalsIgnoreCase(trigger.getStatus()) && triggerWordMatcher.find()
					&& !triggerWordMatcher.group().trim().concat("trigger").equalsIgnoreCase(trigger.getName())) {
				reporttypelist.add(setTriggerReportDtls(trigger, CategoryConstants.TRIGGER_NAMING_CONVENTION,
						"Improper Trigger Naming convention", 0, BPEnforcerConstants.LOW,
						BPEnforcerConstants.TRIGGER_NAMING_CONVENTION, BPEnforcerConstants.BEST_PRACTICES));
			}

		}

		return reporttypelist;
	}

	public static ArrayList<ReportType> checkInActiveTrigger(ArrayList<SObject> triggers) {
		System.out.println("Checking for Inactive Triggers...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ApexTrigger trigger;
		for (SObject sObject : triggers) {
			trigger = (ApexTrigger) sObject;
			if (BPEnforcerConstants.INACTIVE.equalsIgnoreCase(trigger.getStatus())) {
				reporttypelist.add(setTriggerReportDtls(trigger, CategoryConstants.INACTIVE_TGR, "Inactive Trigger", 0,
						BPEnforcerConstants.LOW, BPEnforcerConstants.INACTIVE_TRIGGER,
						BPEnforcerConstants.BEST_PRACTICES));
			}

		}
		return reporttypelist;
	}

	private static ReportType setTriggerReportDtls(ApexTrigger trigger, int category, String issueDesc, int lineNo,
			String issueType, String sheetName, String ruleCategory) {
		// ReportType reportType = new ReportType(trigger.getName(), category,
		// issueDesc, lineNo);
		// reportType.setLastModifiedBy(trigger.getLastModifiedBy().getName());
		ReportType reportType = new ReportType();

		reportType.setClassName(trigger.getName());
		reportType.setCategory(category);
		reportType.setIssueDesc(issueDesc);
		reportType.setLineNumber(lineNo);
		reportType.setLastModifiedDate(trigger.getLastModifiedDate());
		reportType.setComponentId(trigger.getId());
		reportType.setIssueType(issueType);
		reportType.setComponentType(BPEnforcerConstants.APEX_TRIGGER);
		reportType.setSheetName(sheetName);
		reportType.setRuleCategory(ruleCategory);
		return reportType;
	}

	private static ReportType setVFReportDtls(ValidationRule validationRule, int category, String issueDesc, int lineNo,
			String issueType, String sheetName, String ruleCategory) {
		// ReportType reportType = new
		// ReportType(validationRule.getEntityDefinition().getDeveloperName()+"."+validationRule.getValidationName(),
		// category, issueDesc, lineNo);
		ReportType reportType = new ReportType();

		reportType.setClassName(
				validationRule.getEntityDefinition().getDeveloperName() + "." + validationRule.getValidationName());
		reportType.setIssueDesc(issueDesc);
		reportType.setLineNumber(lineNo);
		reportType.setCategory(category);
		reportType.setComponentId(validationRule.getId());
		reportType.setIssueType(issueType);
		reportType.setComponentType(BPEnforcerConstants.VALIDATION_RULE);
		reportType.setSheetName(sheetName);
		reportType.setRuleCategory(ruleCategory);
		return reportType;
	}

	private static ReportType setWRReportDtls(WorkflowRule workflowRule, int category, String issueDesc, int lineNo,
			String issueType, String sheetName, String ruleCategory) {
		ReportType reportType = new ReportType(workflowRule.getFullName(), category, issueDesc, lineNo);
		reportType.setComponentType(BPEnforcerConstants.WORKFLOW_RULE);
		reportType.setIssueType(BPEnforcerConstants.LOW);
		reportType.setSheetName(BPEnforcerConstants.INACTIVE_WF);
		reportType.setLastModifiedBy(workflowRule.getLastModifiedBy().getName());
		reportType.setLastModifiedDate(workflowRule.getLastModifiedDate());
		return reportType;
	}

	public static ArrayList<ReportType> findSystemDebugCount(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking the count of System.debug statements in Apex classes and triggers...");
		String REGEX = "(.*)system.debug(.*)";

		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ApexClass apexComponent = null;
		for (SObject c : classes) {
			apexComponent = null;
			int count = 0;

			String INPUT = null;
			if (c instanceof ApexClass) {

				INPUT = ((ApexClass) c).getBody();
				apexComponent = ((ApexClass) c);
			}

			BufferedReader br = new BufferedReader(new StringReader(INPUT));
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m1 = p1.matcher(line);

				if (m1.matches()) {

					count = count + 1;
				}

			}
			if (count > 4) {

				String a = String.valueOf(count);
				reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.SYSTEM_DEBUG_CNT,
						"system.debug count is " + a, 0, BPEnforcerConstants.LOW,
						BPEnforcerConstants.SYSTEM_DEBUG_COUNT, BPEnforcerConstants.BEST_PRACTICES));
			}

		}

		return reporttypelist;

	}

	public static ArrayList<ReportType> findSystemRunas(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking for presence of System.runAs statements in test classes...");
		String REGEX = "(.*)@ISTEST(.*)";
		String REGEX2 = "(.*)System.runAs(.*)";
		String REGEX3 = ".*(public|private|protected|global)*(testMethod).*";
		String REGEX4 = ".*(public|private|protected|global|static)\\s+(?!class)+.*";
		String OPEN = "(.*)\\{(.*)";
		String CLOSE = "(.*)\\}(.*)";
		String methodName = null;
		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);
		Pattern pClose = Pattern.compile(CLOSE, Pattern.CASE_INSENSITIVE);
		Pattern pOpen = Pattern.compile(OPEN, Pattern.CASE_INSENSITIVE);
		String line = null;
		ApexClass apexComponent = null;
		String input = null;
		int openBracket = 0;
		BufferedReader br;
		boolean isTestFound = false;
		boolean systemRunAsPresent = false;
		String previousLine = "";
		boolean isMethodFound = false;
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		for (SObject c : classes) {
			apexComponent = null;
			input = null;
			line = null;
			isTestFound = false;
			if (c instanceof ApexClass) {

				input = ((ApexClass) c).getBody();
				apexComponent = ((ApexClass) c);
			}

			br = new BufferedReader(new StringReader(input));

			while ((line = br.readLine()) != null) {

				Matcher m1 = p1.matcher(line);

				if (m1.matches()) {
					isTestFound = true;
					break;

				}
			}
			if (isTestFound) {
				br = new BufferedReader(new StringReader(input));
				method: while ((line = br.readLine()) != null) {
					systemRunAsPresent = false;
					Matcher testAnnotation = p1.matcher(previousLine);
					isMethodFound = false;
					previousLine = "";
					openBracket = 0;
					if (Pattern.matches(REGEX4, line) && testAnnotation.matches()) {
						isMethodFound = true;
					}
					Matcher m2 = p3.matcher(line);
					testAnnotation = p1.matcher(line);
					if (m2.matches() || testAnnotation.matches() || isMethodFound) {
						previousLine = line;
						methodName = line;
						Matcher mClose = pClose.matcher(line);
						Matcher mOpen = pOpen.matcher(line);
						if (mOpen.matches()) {
							++openBracket;
						}
						while ((openBracket > 0 || isMethodFound) && (line = br.readLine()) != null) {
							mOpen = pOpen.matcher(line);
							if (mOpen.matches()) {
								++openBracket;
							}
							methodName = Pattern.matches(REGEX4, line) ? line : methodName;
							Matcher systemAssert = p2.matcher(line);
							if (systemAssert.matches()) {
								systemRunAsPresent = true;
								break;
							}

							mClose = pClose.matcher(line);
							if (mClose.matches()) {
								--openBracket;
								if (openBracket == 0 && !systemRunAsPresent) {
									reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.SYSTEM_RUNAS,
											"System.Runas not present inside method " + getMethodName(methodName), 0,
											BPEnforcerConstants.LOW, BPEnforcerConstants.SYSTEM_RUN_AS_CHECK,
											BPEnforcerConstants.ERROR_PRONE));
									continue method;
								}

							}
						}

					}

				}

			}

		}

		return reporttypelist;

	}

	/* Krishnan Changes End */
	/* Swathi Changes Start */
	public static ArrayList<ReportType> findTest(ArrayList<SObject> classes) throws IOException

	{
		System.out.println(
				"Checking if DML/SOQL statements are written in between startTest and stopTest method calls...");
		String REGEX1 = "\\s*@ISTEST(.*)";

		String REGEX2 = "(.*)Test\\.startTest\\(\\);(.*)";

		String REGEX3 = "(.*)Test\\.stopTest\\(\\);(.*)";

		String REGEX4 = "(.*)\\b(insert|update|upsert|delete|undelete|merge|SELECT)\\b\\s(.*)";
		String REGEX6 = ".*(public|private|protected|global)*(testMethod).*";

		String REGEX8 = ".*(public|private|protected|global)\\s(class).*";

		String REGEX9 = ".*(void).*";
		String REGEX10 = "(.*)\\{(.*)";
		String REGEX11 = "(.*)\\}(.*)";

		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);

		Pattern p2 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);

		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);

		Pattern p4 = Pattern.compile(REGEX4, Pattern.CASE_INSENSITIVE);

		Pattern p8 = Pattern.compile(REGEX8, Pattern.CASE_INSENSITIVE);

		Pattern p9 = Pattern.compile(REGEX9, Pattern.CASE_INSENSITIVE);

		Pattern p10 = Pattern.compile(REGEX10, Pattern.CASE_INSENSITIVE);

		Pattern p11 = Pattern.compile(REGEX11, Pattern.CASE_INSENSITIVE);

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		int lineNo = 0;

		int open = 0;
		int close = 0;
		ApexClass apexComponent = null;
		for (SObject c : classes) {

			String INPUT = null;
			INPUT = ((ApexClass) c).getBody();
			apexComponent = ((ApexClass) c);

			BufferedReader br = new BufferedReader(new StringReader(INPUT));

			String line;
			boolean isTest = false;
			boolean isClass = false;
			boolean isMethodfound = false;
			boolean flag1 = true;
			boolean flagcl = false;

			int lno = 0;

			while ((line = br.readLine()) != null) {

				lineNo++;

				Matcher m1 = p1.matcher(line);

				Matcher m8 = p8.matcher(line);

				if (m1.matches()) {

					flag1 = true;
					continue;
				}

				if (m8.matches() && flag1) {

					isClass = true;
					break;
				}
			}

			if (isClass) {

				Matcher m10 = p10.matcher(line);
				Matcher m11 = p11.matcher(line);

				BufferedReader brs = new BufferedReader(new StringReader(INPUT));

				while ((line = brs.readLine()) != null) {

					if (Pattern.matches(REGEX1, line)) {
						flagcl = true;
						continue;
					}
					Matcher m9 = p9.matcher(line);
					if (m9.matches() && flagcl) {
						isMethodfound = true;

						break;
					}

				}
				LineNumberReader rdr = new LineNumberReader(new StringReader(INPUT));
				if (isMethodfound) {
					int i = 0;
					;
					int j = 0;
					;
					int k = 0;

					while ((line = rdr.readLine()) != null) {

						Matcher m2 = p2.matcher(line);
						Matcher m3 = p3.matcher(line);
						Matcher m4 = p4.matcher(line);

						if (m2.matches()) {

							i = rdr.getLineNumber();

						}
						if (m3.matches()) {

							// LineNumberReader rdr=new LineNumberReader(inss);
							j = rdr.getLineNumber();

						}
						if (m4.matches()) {

							// LineNumberReader rdr=new LineNumberReader(inss);
							k = rdr.getLineNumber();

						}

					}
					if (k != 0 && (k < i || k > j)) {
						reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.SQL_DML_SARTSTOP_TEST,
								"DML statements/SOQL statements should be inside test.start() and test.stop()", lno,
								BPEnforcerConstants.MEDIUM, BPEnforcerConstants.TEST_START_AND_STOP,
								BPEnforcerConstants.BEST_PRACTICES));

					}

				}

			}
			// for Methods with testMethod
			// ***************************************************
			boolean flagClass = false;

			while ((line = br.readLine()) != null) {
				Matcher m1 = p1.matcher(line);
				if (m1.matches()) {

					while ((line = br.readLine()) != null) {

						Matcher m8 = p8.matcher(line);

						if (m8.matches()) {

							flagClass = true;
							break;

						}

					}
					if (flagClass) {
						while ((line = br.readLine()) != null) {
							// if(Pattern.matches(REGEX1,line)){
							// line = br.readLine();
							LineNumberReader rdr = new LineNumberReader(new StringReader(INPUT));
							if (Pattern.matches(REGEX6, line)) {
								int i = 0;
								;
								int j = 0;
								;
								int k = 0;

								while ((line = rdr.readLine()) != null) {

									Matcher m2 = p2.matcher(line);
									Matcher m3 = p3.matcher(line);
									Matcher m4 = p4.matcher(line);

									if (m2.matches()) {

										i = rdr.getLineNumber();

									}
									if (m3.matches()) {

										// LineNumberReader rdr=new
										// LineNumberReader(inss);
										j = rdr.getLineNumber();

									}
									if (m4.matches()) {

										// LineNumberReader rdr=new
										// LineNumberReader(inss);
										k = rdr.getLineNumber();

									}

								}
								if (k != 0 && (k < i || k > j)) {
									reporttypelist.add(setClassReportDtls(apexComponent, 16,
											"DML statements/SOQL statements should be inside test.start() and test.stop()",
											lno, BPEnforcerConstants.MEDIUM, BPEnforcerConstants.TEST_START_AND_STOP,
											BPEnforcerConstants.BEST_PRACTICES));

								}

							}
							// }

						}

					}

				}

			}

		}
		return reporttypelist;
	}

	public static ArrayList<ReportType> sharingclasses(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking for classes without sharing...");
		ApexClass apexComponent = null;

		String REGEX = "(.*)\\bwithout sharing\\b(.*)";
		String REGEX2 = "(.*)\\bwithout\\b(.*)";
		String REGEX3 = "(.*)\\bsharing\\b(.*)";
		String REGEX4 = "(.*)\\bclass\\b(.*)";

		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);
		Pattern p4 = Pattern.compile(REGEX4, Pattern.CASE_INSENSITIVE);

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		for (SObject s : classes) {
			String INPUT = null;
			boolean flag1 = false;
			boolean flag2 = false;
			boolean flag3 = false;
			INPUT = ((ApexClass) s).getBody();
			apexComponent = ((ApexClass) s);
			BufferedReader br = new BufferedReader(new StringReader(INPUT));
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m1 = p1.matcher(line);
				Matcher m2 = p2.matcher(line);
				Matcher m3 = p3.matcher(line);

				Matcher m4 = p4.matcher(line);

				if (m1.matches()) {
					flag1 = true;
				}
				if (m2.matches()) {
					flag2 = true;
				}
				if (m3.matches()) {
					flag3 = true;
				}

				if (m4.matches()) {
					break;
				}

			}

			if (flag1 == true || flag2 == true && flag3 == true) {
				reporttypelist.add(setClassReportDtls(apexComponent, 9, "without sharing keyword is present", 0,
						BPEnforcerConstants.MEDIUM, BPEnforcerConstants.WITHOUT_SHARING_CLASSES,
						BPEnforcerConstants.SECURITY));
			}
		}

		return reporttypelist;
	}

	// try-catch block ****[Apex Classes]
	public static ArrayList<ReportType> errorhandling(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking if DML/SOQL statements are wrapped inside try-catch blocks...");
		String REGEX = "(?i)(.*)\\btry\\b(.*)";
		String REGEXcatch = "(?i)(.*)\\bcatch\\b(.*)";
		String REGEX1 = "(?i)(.*)\\b(insert|update|upsert|delete|undelete|merge|SELECT)\\b(.*)";
		String REGEX5 = "(?i)\\s*@ISTEST(.*)";

		Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern pcatch = Pattern.compile(REGEXcatch, Pattern.CASE_INSENSITIVE);
		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		Pattern p5 = Pattern.compile(REGEX5, Pattern.CASE_INSENSITIVE);
		ApexClass apexComponent = null;

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		for (SObject c : classes) {
			int lineNo = 0;
			String Input = null;
			Input = ((ApexClass) c).getBody();

			apexComponent = ((ApexClass) c);
			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;

			int openCount = 0;
			int closeCount = 0;

			boolean flag1 = false;
			boolean flagtry = false;
			boolean flag7 = false;
			boolean flag = false;
			boolean flagcatch = false;
			boolean flagcat = false;
			boolean flagTest = false;

			int lno = 0;
			while ((line = br.readLine()) != null) {
				lineNo++;

				Matcher m5 = p5.matcher(line);

				Matcher m1 = p1.matcher(line);

				boolean flagIs = false;
				if (m5.matches()) {
					flagTest = true;
				}

				if (m1.matches()) {
					flag1 = true;
				}
				Matcher r = p.matcher(line);
				if (r.matches()) {
					flagtry = true;
				}

				Matcher rcatch = pcatch.matcher(line);

				if (rcatch.matches()) {
					flagcat = true;
					flag = false;
					flagtry = false;
					flag1 = false;

				}

				if (flag1) {

					if (!flagtry) {
						if (!flagTest) {
							reporttypelist
									.add(setClassReportDtls(apexComponent, CategoryConstants.SQLDML_EXCEPTION_HANDLING,
											"SOQL or DML not inside try", lineNo, BPEnforcerConstants.MEDIUM,
											BPEnforcerConstants.ERROR_HANDLING, BPEnforcerConstants.ERROR_PRONE));

							flag1 = false;
						}
					}

				}

			}
		}
		return reporttypelist;

	}

	/* Swathi Changes End */
	/* Mary Changes Start */
	public static ArrayList<ReportType> checkRecordTypeInfo(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking if record type is being checked with RecordTypeInfo...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		String REGEX = "(.*)\\[\\s*SELECT(.*)where(.*)\\brecordtype\\b(.*)";

		Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		ApexClass apexComponent = null;
		for (SObject c : classes) {

			String Input = null;
			int noOfLines = 0;
			int lineNo = 0;
			boolean recPresent = false;

			Input = ((ApexClass) c).getBody();
			apexComponent = ((ApexClass) c);
			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;

			while ((line = br.readLine()) != null) {
				noOfLines++;
				Matcher m = p.matcher(line);

				if (m.matches()) {
					recPresent = true;
					lineNo = noOfLines;
					// System.out.println("line"+lineNo);
				}
				if (recPresent) {
					reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.RECORDTYE_INFO,
							"record type should be checked with RecordTypeInfo Method", lineNo,
							BPEnforcerConstants.MEDIUM, BPEnforcerConstants.RECOD_TYPE_INFO_CLASS,
							BPEnforcerConstants.BEST_PRACTICES));
					recPresent = false;
				}
			}
		}

		return reporttypelist;
	}

	public static ArrayList<ReportType> checkNoOfLines(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking the number of lines of code in all methods...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		String REGEX = ".*(public|private|protected|global)(.*)\\((.*)\\)(.*)";
		String REGEX1 = "(.*)\\{(.*)";
		String REGEX2 = "(.*)\\}(.*)";
		String REGEX3 = "^\n*$";
		String REGEX4 = ".*\\((.*)";

		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);
		Pattern p4 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);
		Pattern p5 = Pattern.compile(REGEX4);
		ApexClass apexComponent = null;
		for (SObject s : classes) {
			int noOfLines = 0;
			int openBracket = 0;
			int closeBracket = 0;

			String INPUT = null;
			boolean isExceeded = false;
			boolean isMethod = false;
			String line;
			String methodName = null;
			String originMethodName = null;

			INPUT = ((ApexClass) s).getBody();
			apexComponent = ((ApexClass) s);

			BufferedReader br = new BufferedReader(new StringReader(INPUT));
			while ((line = br.readLine()) != null) {
				Matcher m1 = p1.matcher(line);
				if (m1.matches()) {
					isMethod = true;
					noOfLines = 0;
					openBracket = 0;
					closeBracket = 0;
					methodName = line;
					originMethodName = line;
					String[] wordList = originMethodName.split(" ");

					for (int i = 0; i < wordList.length; i++) {
						Matcher m5 = p5.matcher(wordList[i]);
						if (m5.matches()) {
							// System.out.println(wordList[i]);
							methodName = wordList[i].substring(0, wordList[i].indexOf("("));
						}

					}

				}

				if (isMethod) {
					Matcher m4 = p4.matcher(line);
					if (!m4.matches()) {
						noOfLines++;
						Matcher m2 = p2.matcher(line);
						Matcher m3 = p3.matcher(line);
						if (m2.matches()) {
							openBracket++;
						}
						if (m3.matches()) {
							closeBracket++;
						}

						if (openBracket == closeBracket && openBracket != 0) {
							if (noOfLines >= 45) {
								isExceeded = true;

								reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.METHOD_TOOLARGE,
										"More number of lines in " + methodName, noOfLines, BPEnforcerConstants.MEDIUM,
										BPEnforcerConstants.LINES_OF_CODE, BPEnforcerConstants.BEST_PRACTICES));

							}
							isMethod = false;
						}

					}
				}

			}

		}

		return reporttypelist;
	}
	/* Mary Changes End */

	/* Mary Changes End */
	/* Subhitra Changes Start */
	public static ArrayList<ReportType> findID(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking if ID values are hardcoded...");
		Pattern alphaNumericPattern = Pattern.compile("\\b((?:[a-z]+\\S*\\d+|\\d\\S*[a-z]+)[a-z\\d]*)\\b",
				Pattern.CASE_INSENSITIVE);
		String REGEX = "(.*)((=?')|(,')|(\\('))([a-z0-9]{15}|[a-z0-9]{18})('.*)";
		String value;
		Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ApexClass apexComponent = null;
		for (SObject c : classes) {

			int lineNo = 0;
			String Input = null;

			Input = ((ApexClass) c).getBody();
			apexComponent = ((ApexClass) c);
			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;

			while ((line = br.readLine()) != null) {
				lineNo++;
				Matcher m = p.matcher(line);
				if (m.matches()) {
					// use the hardcoded value to check if it alphanumeric or
					// not.
					value = m.group(6);
					if (alphaNumericPattern.matcher(value).matches()) {
						reporttypelist.add(setClassReportDtls(apexComponent, 12, "Id is hardcoded ", lineNo,
								BPEnforcerConstants.MEDIUM, BPEnforcerConstants.HARDCODED_IDS,
								BPEnforcerConstants.ERROR_PRONE));
					}

				}

			}
		}

		return reporttypelist;
	}

	public static ArrayList<ReportType> findSOQL(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking if SOQL statements are written inside execute method of Batch classes...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		String REGEX = "(.*)global void execute(.*)";
		String REGEX1 = "(.*)\\bSELECT\\b(.*)";
		String REGEX2 = "(.*)\\{(.*)";
		String REGEX3 = "(.*)\\}(.*)";
		Pattern p = Pattern.compile(REGEX);
		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX2);
		Pattern p3 = Pattern.compile(REGEX3);
		ApexClass apexComponent = null;
		for (SObject c : classes) {
			int openCount = 0;
			int closeCount = 0;
			boolean flag = false;

			int lineNo = 0;
			String Input = null;

			Input = ((ApexClass) c).getBody();
			apexComponent = ((ApexClass) c);

			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;
			while ((line = br.readLine()) != null) {

				lineNo++;
				Matcher m = p.matcher(line);
				if (m.matches()) {
					flag = true;

				}

				if (flag) {

					Matcher op = p2.matcher(line);
					Matcher cl = p3.matcher(line);

					if (op.matches()) {

						openCount++;
					}
					if (cl.matches()) {
						closeCount++;
					}
					if (openCount == closeCount && openCount != 0) {
						flag = false;
					}

					Matcher m1 = p1.matcher(line);
					if (m1.matches()) {
						reporttypelist.add(setClassReportDtls(apexComponent, CategoryConstants.SOQL_IN_BATCH_CLS,
								"SOQL found inside Execute", lineNo, BPEnforcerConstants.HIGH,
								BPEnforcerConstants.SOQL_IN_EXECUTE, BPEnforcerConstants.BEST_PRACTICES));

					}
				}
			}
		}

		return reporttypelist;
	}

	/* Subhitra Changes End */

	public static ArrayList<ReportType> checkSelectStatement(ArrayList<SObject> classes) throws IOException {
		System.out.println("Checking if select statements does not contain limit or where condition...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		String REGEX1 = "(.*)\\bSELECT\\b(.*)";
		String REGEX2 = "(.*)\\bLIMIT\\b(.*)";
		String REGEX3 = "(.*)\\bWHERE\\b(.*)";
		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE);
		Pattern p3 = Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE);
		ApexClass apexClass = null;
		for (SObject c : classes) {

			boolean flag = false;

			int lineNo = 0;
			String Input = null;

			Input = ((ApexClass) c).getBody();
			apexClass = ((ApexClass) c);

			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;
			while ((line = br.readLine()) != null) {
				flag = false;
				lineNo++;
				Matcher m = p1.matcher(line);
				if (m.matches()) {
					flag = true;

				}
				if (flag) {
					Matcher lm = p2.matcher(line);
					Matcher wh = p3.matcher(line);
					if (!(lm.matches() || wh.matches())) {
						reporttypelist.add(setClassReportDtls(apexClass, CategoryConstants.SOQL_LIMIT,
								"SOQL does not contain limit or where condition inside select Clause", lineNo,
								BPEnforcerConstants.HIGH, BPEnforcerConstants.LIMIT_OR_WHERE_IN_SELECT,
								BPEnforcerConstants.BEST_PRACTICES));
					}
				}

			}
		}

		return reporttypelist;
	}
	/*
	 * public static ArrayList<ReportType> checkFutureTag(ArrayList<SObject>
	 * classes) throws IOException {
	 * System.out.println("checkFutureTag--Start"); ArrayList<ReportType>
	 * reporttypelist = new ArrayList<ReportType>(); String REGEX1 =
	 * ".*for(.*)\\((.*)\\)(.*)"; String REGEX2 = "(.*)\\{(.*)"; String REGEX3 =
	 * "(.*)\\}(.*)"; String REGEX4 = "\\s*@FUTURE(.*)"; String REGEX5 =
	 * ".*\\((.*)\\);"; String REGEX6 = "(.*)= new(.*)"; Pattern p1 =
	 * Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE); Pattern p2 =
	 * Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE); Pattern p3 =
	 * Pattern.compile(REGEX3, Pattern.CASE_INSENSITIVE); Pattern p4 =
	 * Pattern.compile(REGEX4, Pattern.CASE_INSENSITIVE); Pattern p5 =
	 * Pattern.compile(REGEX5, Pattern.CASE_INSENSITIVE); Pattern p6 =
	 * Pattern.compile(REGEX6, Pattern.CASE_INSENSITIVE); for (SObject d :
	 * classes) {
	 * 
	 * String classNames = ((ApexClass) d).getName(); // System.out.println(
	 * "Classes inside-----:"+classNames); if (d.getClass().getName() != null &&
	 * d.getClass().getName().equals("Product2")) { //
	 * System.out.println("String matched //
	 * method-----:"+d.getClass().getName()); } } String className = null; for
	 * (SObject apexClass : classes) {
	 * 
	 * int lineNo = 0; String Input = null; boolean flag = false; boolean
	 * futureflag = false; int openBracket = 0; int closeBracket = 0; Input =
	 * ((ApexClass) apexClass).getBody(); className = ((ApexClass)
	 * apexClass).getName();
	 * 
	 * BufferedReader br = new BufferedReader(new StringReader(Input)); String
	 * line;
	 * 
	 * while ((line = br.readLine()) != null) { lineNo++; Matcher m =
	 * p1.matcher(line); if (m.matches()) { flag = true;
	 * 
	 * } if (flag) { Matcher m2 = p2.matcher(line); Matcher m3 =
	 * p3.matcher(line); Matcher m5 = p5.matcher(line); Matcher m6 =
	 * p6.matcher(line); if (m5.matches()) { futureflag = checkTag(line, br);
	 * 
	 * } if (m6.matches()) { futureflag = checkTaginClass(line, br.readLine(),
	 * classes);
	 * 
	 * } if (m2.matches()) { openBracket++; } if (m3.matches()) {
	 * closeBracket++; }
	 * 
	 * if (openBracket == closeBracket && openBracket != 0) { if (futureflag) {
	 * reporttypelist .add(new ReportType(className, 18,
	 * "Future Tag present inside For loop", lineNo)); } } } } }
	 * System.out.println("checkFutureTag--End"); return reporttypelist; }
	 */

	private static boolean checkTag(String line, BufferedReader br) {
		System.out.println("checkTag--Start");
		String REGEX1 = "\\s*@FUTURE(.*)";
		String lines[] = line.split("=");
		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		String classline;
		boolean returnFlag = false;
		boolean futureFlag = false;
		try {
			while ((classline = br.readLine()) != null) {
				Matcher m = p1.matcher(classline);
				if (m.matches()) {
					futureFlag = true;
				}
				if (futureFlag) {
					for (int i = 0; i < lines.length; i++) {

						if (lines[i].contains(";")) {
							String s = lines[i].toString().replaceAll("\\;", "");
							if (classline.contains(s)) {
								returnFlag = true;
							}
						}
					}

				}
			}

		} catch (Exception e) {

		}
		System.out.println("checkTag--End");
		return returnFlag;
	}

	private static boolean checkTaginClass(String line, String linecheck, ArrayList<SObject> classes) {
		System.out.println("checkTaginClass--Start");
		String lines[] = line.split("=");
		String classname = "";
		try {
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].contains(";")) {
					String lines1[] = lines[i].split(" ");
					for (int j = 0; j < lines1.length; j++) {
						if (lines1[j].contains(";")) {

							String s = lines1[j].toString().replaceAll("\\;", "");
							String s1 = s.replaceAll("\\)", "");
							classname = s1.replaceAll("\\(", "");

						}

					}
				}

			}
			for (SObject d : classes) {

				String classNames = ((ApexClass) d).getName();
				// System.out.println("Classes inside-----:"+classNames);
				if (d.getClass().getName() != null && d.getClass().getName().equals("Product2")) {
					// System.out.println("String matched
					// method-----:"+classname);
				}
			}

		} catch (Exception e) {

		}
		System.out.println("checkTaginClass--End");
		return true;
	}

	public static ArrayList<ReportType> checkDmlInTry(ArrayList<SObject> triggers) throws IOException {
		System.out.println("Checking if DML/SOQL statements are wrapped inside try-catch blocks in Triggers...");
		String REGEX = "(?i)(.*)\\btry\\b(.*)";
		String REGEXcatch = "(?i)(.*)\\bcatch\\b(.*)";
		String REGEX1 = "(?i)(.*)\\b(insert|update|upsert|delete|undelete|merge|SELECT)\\b(.*)";
		String REGEX5 = "(?i)\\s*@ISTEST(.*)";
		String REGEX11 = "(?i)(.*)(After){1,}(.*)";
		String REGEX12 = "(?i)(.*)(Before){1,}(.*)";

		Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
		Pattern pcatch = Pattern.compile(REGEXcatch, Pattern.CASE_INSENSITIVE);
		Pattern p1 = Pattern.compile(REGEX1, Pattern.CASE_INSENSITIVE);
		Pattern p5 = Pattern.compile(REGEX5, Pattern.CASE_INSENSITIVE);
		Pattern p12 = Pattern.compile(REGEX11, Pattern.CASE_INSENSITIVE);
		Pattern p13 = Pattern.compile(REGEX12, Pattern.CASE_INSENSITIVE);

		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ApexTrigger apexComponent = null;
		for (SObject c : triggers) {

			int lineNo = 0;
			String Input = null;
			Input = ((ApexTrigger) c).getBody();
			apexComponent = ((ApexTrigger) c);

			BufferedReader br = new BufferedReader(new StringReader(Input));
			String line;
			boolean flag1 = false;
			boolean flagtry = false;
			boolean flag = false;
			boolean flagcat = false;
			boolean flagTest = false;
			while ((line = br.readLine()) != null) {
				lineNo++;
				boolean flagIs = false;
				Matcher m5 = p5.matcher(line);
				Matcher m1 = p1.matcher(line);
				Matcher m12 = p12.matcher(line);
				Matcher m13 = p13.matcher(line);
				if (m12.matches() || m13.matches()) {
					flagIs = true;
					continue;
				}
				if (m5.matches()) {
					flagTest = true;
				}

				if (m1.matches()) {
					flag1 = true;
				}
				Matcher r = p.matcher(line);
				if (r.matches()) {
					flagtry = true;
				}

				Matcher rcatch = pcatch.matcher(line);

				if (rcatch.matches()) {
					flagcat = true;
					flag = false;
					flagtry = false;
					flag1 = false;

				}

				if (flag1 && !flagIs) {
					if (!flagtry) {
						if (!flagTest) {
							reporttypelist
									.add(setTriggerReportDtls(apexComponent, CategoryConstants.SQLDML_EXCEHANDLING_TRG,
											"SOQL or DML not inside try", lineNo, BPEnforcerConstants.MEDIUM,
											BPEnforcerConstants.DML_SOQL_STATEMENT, BPEnforcerConstants.ERROR_PRONE));
							flag1 = false;
						}
					}

				}

			}
		}

		return reporttypelist;
	}

	/**
	 * 
	 * gets the method name for the passed string using regex
	 * 
	 * @param line
	 * @return String
	 */

	private static String getMethodName(final String line) {
		Pattern PARENTHESES_REGEX = Pattern.compile("(?U)([.\\w]+)\\s*\\((.*)\\)");

		Matcher matcher = PARENTHESES_REGEX.matcher(line);
		String methodName = null;
		while (matcher.find()) {
			methodName = matcher.group(1);

		}
		return methodName;
	}

	/**
	 * 
	 * @throws IOException
	 */
	/*
	 * public static ArrayList<ReportType>
	 * checkCommentsBtwAnnotation(ArrayList<SObject> classes) throws IOException
	 * { ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
	 * String annotation =
	 * "(.*)@(IsTest|Future|TestSetup|TestVisible|InvocableMethod|InvocableVariable|RemoteAction|ReadOnly|HttpDelete|HttpGet|"
	 * + "RestResource|HttpPatch|HttpPost|HttpPut|Deprecated|AuraEnabled)(.*)";
	 * String methodRegex =
	 * ".*(public|private|protected|global|static|void)\\s+(?!class)+.*"; String
	 * classRegex = ".*(public|private|protected|global)\\s+(class)+.*"; String
	 * input = null; String line; Pattern anotationPttrn =
	 * Pattern.compile(annotation, Pattern.CASE_INSENSITIVE); Pattern
	 * methodDeclPttrn = Pattern.compile(methodRegex, Pattern.CASE_INSENSITIVE);
	 * Matcher regexMatcher; Matcher methodDeclMatcher = null; int lineNo = 1;
	 * ApexClass apexComponent = null; BufferedReader br; for (SObject c :
	 * classes) { lineNo = 1; apexComponent = null; if (c instanceof ApexClass)
	 * { if (((ApexClass) c).getName().startsWith("CINTest")) { break; } input =
	 * ((ApexClass) c).getBody(); apexComponent = ((ApexClass) c); } br = new
	 * BufferedReader(new StringReader(input)); while ((line = br.readLine()) !=
	 * null) { lineNo++; regexMatcher = anotationPttrn.matcher(line);
	 * methodDeclMatcher = methodDeclPttrn.matcher(line); // if annotation is
	 * present in same line as method declaration if (regexMatcher.matches() &&
	 * !methodDeclMatcher.matches()) { line = br.readLine(); lineNo++;
	 * regexMatcher = methodDeclPttrn.matcher(line); if (!regexMatcher.matches()
	 * && !Pattern.matches(classRegex, line)) {
	 * reporttypelist.add(setClassReportDtls(apexComponent, 20,
	 * "Annotation is not present above method decalartion at line " + (lineNo -
	 * 2), (lineNo -
	 * 2),BPEnforcerConstants.ERROR,BPEnforcerConstants.COMMENTS_BTW_ANNOTATIONS
	 * ));
	 * 
	 * } } } } return reporttypelist; }
	 */

	/* Changes Start */
	public static ArrayList<ReportType> annotationbetweencomments(ArrayList<SObject> classes) throws IOException

	{
		System.out.println("Checking for comments between annotation...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		String annotation = "(.*)@(IsTest|Future|TestSetup|TestVisible|InvocableMethod|InvocableVariable|RemoteAction|ReadOnly|HttpDelete|HttpGet|"
				+ "RestResource|HttpPatch|HttpPost|HttpPut|Deprecated|AuraEnabled)(.*)";
		String methodRegex = "(.*)(?i)(public|private|protected|static){1,}(.*)\\({1}(.*)";
		String newword = "(.*)\\bnew\\b(.*)";
		String classRegex = ".*(?i)(public|private|protected|global)\\s+(\\bclass\\b).*";
		String slash = "(.*)/\\/(.*)";
		String MultilineCommnt = "(?s)(/\\*|/\\*\\*).*?";
		String line;

		Pattern anotationPttrn = Pattern.compile(annotation, Pattern.CASE_INSENSITIVE);
		Pattern anotationnew = Pattern.compile(newword, Pattern.CASE_INSENSITIVE);
		Pattern methodDeclPttrn = Pattern.compile(methodRegex, Pattern.CASE_INSENSITIVE);
		Pattern ClassPttrn = Pattern.compile(classRegex, Pattern.CASE_INSENSITIVE);
		Pattern slahpattern = Pattern.compile(slash, Pattern.CASE_INSENSITIVE);
		Pattern slahpatternmultiline = Pattern.compile(MultilineCommnt, Pattern.CASE_INSENSITIVE);

		Matcher regexMatcher;
		Matcher regexMatcherclass;
		Matcher slahMatcher;
		Matcher slahMatchermulti;
		Matcher methodDeclMatcher;

		int lineNo = 1;
		boolean methd = false;

		boolean classflag = false;

		for (SObject c : classes) {
			String componentName = null;

			String INPUT = null;
			if (c instanceof ApexClass) {
				if (((ApexClass) c).getName().startsWith("CINTest")) {
					break;
				}
				INPUT = ((ApexClass) c).getBody();
				componentName = ((ApexClass) c).getName();
			}

			BufferedReader br = new BufferedReader(new StringReader(INPUT));

			while ((line = br.readLine()) != null) {

				Matcher methodDecnew = anotationnew.matcher(line);
				methodDeclMatcher = methodDeclPttrn.matcher(line);

				lineNo++;
				regexMatcher = anotationPttrn.matcher(line);
				regexMatcherclass = ClassPttrn.matcher(line);
				slahMatcher = slahpattern.matcher(line);
				slahMatchermulti = slahpatternmultiline.matcher(line);

				methodDeclMatcher = methodDeclPttrn.matcher(line);

				if (methodDeclMatcher.matches() && (!methodDecnew.matches())) {
					// System.out.println("method matched"+line+" "+methd);
					methd = true;
				}
				if (regexMatcherclass.matches()) {
					// System.out.println("class matched"+line+" "+classflag);
					classflag = true;

				}

				/*
				 * if((slahMatcher.matches())&& regexMatcher.matches()) {
				 * System.out.println("same line"+line+""+lineNo); }
				 */

				if (regexMatcher.matches()) {
					// System.out.println("annotation matched"+line);

					while ((line = br.readLine()) != null) {
						methodDeclMatcher = methodDeclPttrn.matcher(line);

						regexMatcherclass = ClassPttrn.matcher(line);
						lineNo++;
						slahMatcher = slahpattern.matcher(line);
						slahMatchermulti = slahpatternmultiline.matcher(line);

						methodDecnew = anotationnew.matcher(line);
						if (methodDeclMatcher.matches() && (!methodDecnew.matches())) {
							// System.out.println("method matched"+line+"
							// "+methd);
							methd = true;
						}
						if (regexMatcherclass.matches()) {
							// System.out.println("class matched"+line+"
							// "+classflag);
							classflag = true;

						}
						if ((slahMatcher.matches() || slahMatchermulti.matches()) && (!methd || !classflag)) {
							// System.out.println("class or method contains
							// comments between annotations"+line+""+lineNo);
							reporttypelist.add(new ReportType(componentName, CategoryConstants.INAPPR_COMMENTLOC,
									"Annotation is not present above method decalartion at line ", lineNo));
							methd = false;
							break;

						}
					}
				}

			}
		}

		return reporttypelist;
	}

	/* swathi's changes start */

	// Presence of doctype in VF page

	public static ArrayList<ReportType> Presence_Of_Doctype_VF(ArrayList<SObject> pages) throws IOException {
		System.out.println("Checking for doctype in VF page...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		String input = null;
		ApexPage apexPg = null;
		String line;
		BufferedReader br;
		String REGEX = "(?i)(.*)\\bdocType\\b(.*)";

		Pattern p1 = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

		for (SObject c : pages) {
			int lineNo = 0;
			if (c instanceof ApexPage) {
				input = ((ApexPage) c).getMarkup();
				apexPg = ((ApexPage) c);

			}
			br = new BufferedReader(new StringReader(input));
			while ((line = br.readLine()) != null) {
				lineNo++;
				Matcher m1 = p1.matcher(line);
				if (m1.matches() == false) {
					// System.out.println("Checking 1st line");

					reporttypelist.add(setPagesReportDtls(apexPg, CategoryConstants.DOCTYPE,
							"doctype is not present in VF page", lineNo, BPEnforcerConstants.MEDIUM,
							BPEnforcerConstants.DOCTYPE, BPEnforcerConstants.BEST_PRACTICES));
				}

				break;
			}

		}
		return reporttypelist;
	}

	/* swathi's changes end */

	/* Mary changes start */
	public static ArrayList<ReportType> checkinlinejs(ArrayList<SObject> pages) throws IOException {
		System.out.println("Checking for inline js...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		String input = null;
		String line = null;

		BufferedReader br;
		ApexPage apexPg = null;

		String jsmatch = "(?i)(.*)<\\bscript\\b(\\s*)(.*)>(.*)";

		Pattern jsPattern = Pattern.compile(jsmatch, Pattern.CASE_INSENSITIVE);
		Matcher matcher1;
		for (SObject c : pages) {
			int lineNo = 0;
			if (c instanceof ApexPage) {
				input = ((ApexPage) c).getMarkup();
				apexPg = ((ApexPage) c);

			}
			br = new BufferedReader(new StringReader(input));
			while ((line = br.readLine()) != null) {
				lineNo++;
				matcher1 = jsPattern.matcher(line);
				if (matcher1.matches()) {
					// System.out.println("inline js"+line);
					// System.out.println("inline js"+lineNo);
					reporttypelist.add(setPagesReportDtls(apexPg, CategoryConstants.INLINE_JAVASCRIPT,
							"Inline javascript", lineNo, BPEnforcerConstants.MEDIUM,
							BPEnforcerConstants.INLINE_JAVASCRIPT, BPEnforcerConstants.BEST_PRACTICES));
				}
				// System.out.println("remaining"+line);
			}
		}
		return reporttypelist;
	}

	public static ArrayList<ReportType> checkinlinecss(ArrayList<SObject> pages) throws IOException {
		System.out.println("Checking for inline css...");
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();

		String input = null;
		String line = null;

		BufferedReader br;
		ApexPage apexPg = null;

		String cssmatch = "(?i)(.*)<\\bstyle\\b(\\s*)(.*)>(.*)";
		String cssmatch1 = "(?i)(.*)<(.*)\\bstyle\\b(\\s*)=(\\s*)\"(.*)>(.*)";
		String cssmatch2 = "(?i)(.*)<link(\\s*)(.*)>(.*)";

		Pattern cssPattern = Pattern.compile(cssmatch, Pattern.CASE_INSENSITIVE);
		Pattern cssPattern1 = Pattern.compile(cssmatch1, Pattern.CASE_INSENSITIVE);
		Pattern cssPattern2 = Pattern.compile(cssmatch2, Pattern.CASE_INSENSITIVE);

		Matcher matcher1;
		Matcher matcher2;
		Matcher matcher3;
		for (SObject c : pages) {
			int lineNo = 0;
			if (c instanceof ApexPage) {
				input = ((ApexPage) c).getMarkup();
				apexPg = ((ApexPage) c);
			}
			br = new BufferedReader(new StringReader(input));
			while ((line = br.readLine()) != null) {
				lineNo++;
				matcher1 = cssPattern.matcher(line);
				matcher2 = cssPattern1.matcher(line);
				matcher3 = cssPattern2.matcher(line);
				if (matcher1.matches() || matcher2.matches() || matcher3.matches()) {

					reporttypelist.add(setPagesReportDtls(apexPg, CategoryConstants.INLINE_CSS, "Inline css style",
							lineNo, BPEnforcerConstants.MEDIUM, BPEnforcerConstants.INLINE_CSS,
							BPEnforcerConstants.BEST_PRACTICES));
				}
			}
		}
		return reporttypelist;
	}
	/* Changes End */

	// checking workflow names
	public static Map<String, ReportType> workflowNameCheck(ToolingConnection con) throws ConnectionException {
		System.out.println("Checking for inactive workflow rules...");
		Map<String, ReportType> workFlowDtls = new HashMap<>();
		ReportType reportType;
		ArrayList<SObject> workflows = new ArrayList<SObject>();
		StringBuilder query = new StringBuilder(
				"SELECT Id, Name,TableEnumOrId,NamespacePrefix,LastModifiedBy.name, lastmodifieddate FROM workflowrule");
		QueryResult qres = con.query(query.toString());
		for (SObject s : qres.getRecords()) {
			workflows.add(s);
		}
		List<String> workflowsNames = new ArrayList<String>();
		int i = 0;
		for (SObject s : workflows) {
			WorkflowRule cls = (WorkflowRule) s;

			if (!cls.getName().isEmpty()) {
				if (null != cls.getNamespacePrefix() && !cls.getNamespacePrefix().isEmpty()) {
					workflowsNames.add(cls.getTableEnumOrId() + "." + cls.getNamespacePrefix() + "__" + cls.getName());
				} else {
					workflowsNames.add(cls.getTableEnumOrId() + "." + cls.getName());
				}
				reportType = new ReportType();
				// reportType.setLastModifiedBy(cls.getLastModifiedBy().getName());
				reportType.setLastModifiedDate(cls.getLastModifiedDate());
				workFlowDtls.put(workflowsNames.get(i), reportType);
				i++;
			}
		}
		//DataStore.wfData = workflowsNames;
		// System.out.println("workflowsNames -->"+workflowsNames);
		return workFlowDtls;
	}

	/**
	 * Find the list of inactive validation rules.
	 * 
	 * @param validationRules
	 * @return List<ReportType>
	 */
	public static List<ReportType> inactiveValidationRule(ArrayList<SObject> validationRules) {
		List<ReportType> reporttypelist = new ArrayList<ReportType>();
		ValidationRule validationRule;
		for (SObject sObject : validationRules) {
			validationRule = (ValidationRule) sObject;
			if (!validationRule.getActive()) {
				reporttypelist.add(setVFReportDtls(validationRule, CategoryConstants.INACTIVE_VALIDATIONRULE,
						"Inactive Validation Rule", 0, BPEnforcerConstants.LOW, BPEnforcerConstants.INACTIVE_VR,
						BPEnforcerConstants.BEST_PRACTICES));

			}
		}
		return reporttypelist;
	}

	/**
	 * Find the list of inactive validation rules.
	 * 
	 * @param validationRules
	 * @return List<ReportType>
	 */
	public static List<ReportType> inactiveWorkFlowRule(ArrayList<SObject> workFlowRules) {
		List<ReportType> reporttypelist = new ArrayList<ReportType>();
		WorkflowRule workflowRule;

		for (SObject sObject : workFlowRules) {

			workflowRule = (WorkflowRule) sObject;

			if (null != workflowRule.getMetadata() && !workflowRule.getMetadata().isActive()) {
				reporttypelist.add(setWRReportDtls(workflowRule, CategoryConstants.INACTIVE_WORKFLOWRULE,
						"Inactive WorkFlow Rule", 0, BPEnforcerConstants.LOW, BPEnforcerConstants.WORKFLOW_RULE,
						BPEnforcerConstants.BEST_PRACTICES));
			}
		}
		return reporttypelist;
	}

	public static ArrayList<ReportType> multipleFieldUpdates(ArrayList<SObject> workflowfieldupdates,
			ArrayList<SObject> triggers, ArrayList<SObject> flows) {
		// System.out.println("Checking if there are multiple field updates on
		// each object in the org...");
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, ApexTrigger> triggerDetails = new HashMap<String, ApexTrigger>();
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		List<String> lstTrggObjName = new ArrayList<String>();
		List<String> lstWFFldUpdObjName = new ArrayList<String>();
		List<String> lstProcUpdateObj = new ArrayList<String>();
		List<String> lstTriggerName = new ArrayList<String>();
		List<String> lstWrkflowUpdate = new ArrayList<String>();
		List<String> lstProcBuilderUpd = new ArrayList<String>();
		ApexTrigger trigger;
		WorkflowFieldUpdate wfUpdate;
		for (SObject apexTrigger : triggers) {

			// apexTrigger.
			trigger = (ApexTrigger) apexTrigger;
			// System.out.println("trigger full name
			// trigger.getEntityDefinition().getFullName()"+trigger.getEntityDefinition().getFullName());

			if (trigger.getUsageAfterInsert() || trigger.getUsageBeforeInsert() || trigger.getUsageAfterUpdate()
					|| trigger.getUsageBeforeUpdate()) {
				// EntityDefinition entDef1 = trigger.getEntityDefinition();
				lstTrggObjName.add(trigger.getEntityDefinition().getFullName());
				lstTriggerName.add(trigger.getName());
			}

		}

		for (SObject wfUpdates : workflowfieldupdates) {

			wfUpdate = (WorkflowFieldUpdate) wfUpdates;
			// System.out.println("strFullName"+wfUpdate.getEntityDefinition().getFullName());
			String strWFObjAPiname = wfUpdate.getEntityDefinition().getFullName();
			if (lstTrggObjName.contains(strWFObjAPiname)) {
				lstWFFldUpdObjName.add(strWFObjAPiname);
				lstWrkflowUpdate.add(wfUpdate.getName());

			}

		}

		for (SObject flowTmp : flows) {

			Flow strTmpFlow = (Flow) flowTmp;
			String procType = strTmpFlow.getProcessType();
			String strStatus = strTmpFlow.getStatus();
			if (procType.equalsIgnoreCase("Workflow") && strStatus.equalsIgnoreCase("Active")) {

				com.sforce.soap.tooling.metadata.Flow metaFlow = strTmpFlow.getMetadata();
				FlowRecordUpdate[] fRcdUpdates = metaFlow.getRecordUpdates();
				for (FlowRecordUpdate fRcdUpdate : fRcdUpdates) {

					if (lstWFFldUpdObjName.contains(fRcdUpdate.getObject())) {
						lstProcUpdateObj.add(fRcdUpdate.getObject());
						lstProcBuilderUpd.add(strTmpFlow.getFullName());

					}

				}

			}

		}

		// System.out.println("lstProcUpdateObj --"+lstProcUpdateObj);
		for (int i = 0; i < lstProcUpdateObj.size(); i++) {
			// System.out.println("inside rpt");
			String strCompName = "Object -->" + lstProcUpdateObj.get(i) + System.lineSeparator() + "Process Builder -->"
					+ lstProcBuilderUpd.get(i) + System.lineSeparator() + "WorkFlow -->" + lstWrkflowUpdate.get(i)
					+ System.lineSeparator() + "Trigger -->" + lstTriggerName.get(i);
			reporttypelist.add(setFldUpdReportDtls(strCompName, CategoryConstants.MULTIPLE_FIELD_UPDATE,
					"Multiple Field Updates on an Object", 0, BPEnforcerConstants.ERROR,
					BPEnforcerConstants.MULTIPLE_FIELD_UPDATE));

		}
		// System.out.println("report size"+reporttypelist.size());
		return reporttypelist;
	}

	private static ReportType setFldUpdReportDtls(String strObjName, int category, String issueDesc, int lineNo,
			String issueType, String sheetName) {
		ReportType reportType = new ReportType(strObjName, category, issueDesc, lineNo);
		reportType.setIssueType(issueType);
		reportType.setComponentType(BPEnforcerConstants.MULTIPLE_FIELD_UPDATE);
		reportType.setSheetName(sheetName);
		return reportType;
	}
}
