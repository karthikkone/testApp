/**
* 
* Implementations for Health Check Rules in BP Enforcer
* 
*
* @author  Mamatha
* @version 1.0
* @since   2017-09-28
*/
package com.infy.bpe.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.infy.report.model.ReportType;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.BPEnforcerUtility;
import com.sforce.soap.tooling.Annotation;
import com.sforce.soap.tooling.QueryResult;
import com.sforce.soap.tooling.SymbolTable;
import com.sforce.soap.tooling.ToolingConnection;
import com.sforce.soap.tooling.sobject.ApexClass;
import com.sforce.soap.tooling.sobject.ApexCodeCoverageAggregate;
import com.sforce.soap.tooling.sobject.ApexPage;
import com.sforce.soap.tooling.sobject.ApexTrigger;
import com.sforce.soap.tooling.sobject.SObject;
import com.sforce.soap.tooling.sobject.SecurityHealthCheck;
import com.sforce.ws.ConnectionException;

public class BPHealthCheckerRules {
	//constants defined
	private static final String SOQL_REGEX = "^(?!\\s*[//|/*])(.*\\bSELECT\\b.*)";;

	private static final String DML_REGEX = "^(?!\\s*[//|/*])(Database\\.)?(.*\\b(insert|update|delete|upsert|undelete)\\b.*)";

	/**
	 * 
	 * @param classes
	 * @throws IOException
	 */
	public static void findLinesOfCodeApexClass(ArrayList<SObject> classes) throws IOException {
		String apexBody;
		for (SObject c : classes) {
			if (c instanceof ApexClass) {
				apexBody = ((ApexClass) c).getBody();
				System.out.println("Apex Class " + ((ApexClass) c).getName());
				String[] lines = apexBody.split("\n");
				System.out.println("Lines -->" + lines.length);
			}
		}
	}

	/**
	 * 
	 * @param classes
	 * @throws IOException
	 */
	public static Integer findLinesOfCodeApexPage(ArrayList<SObject> classes) throws IOException { 
		String apexBody;
		String pageName;
		int lineCount = 0;
		int totalLineCnt = 0;
		ApexPage apexPage;
		for (SObject c : classes) {
			if (c instanceof ApexPage) {
				apexPage = (ApexPage) c;
				apexBody = apexPage.getMarkup();
				pageName = apexPage.getName();
				lineCount = BPEnforcerUtility.getPageLineCount(apexBody);
				totalLineCnt+=lineCount;
				//System.out.println(pageName + " Lines -->" + lineCount);
			}
		}
		return totalLineCnt;
	}

	/**
	 * 
	 * @param con
	 * @return
	 * @throws ConnectionException
	 */
	public static Integer securityHealthCheckRisks(ToolingConnection con) throws ConnectionException {
		Integer score = 0;
		String query = "SELECT Score FROM SecurityHealthCheck";
		QueryResult qres = con.query(query.toString());
		System.out.println("Entity type name " + qres.getEntityTypeName() + " " + qres.getSize());
		if (null != qres && qres.getSize() > 0) {
			SecurityHealthCheck securityHealthCheck = (SecurityHealthCheck) qres.getRecords()[0];
			score = Integer.valueOf(securityHealthCheck.getScore());
			//System.out.println("Score type name " + score);
		}
		return score;
	}

	/**
	 * 
	 * @param classes
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static Integer checkForSOQLInApexClass(List<SObject> classes) throws ConnectionException, IOException {

		String apexBody = null;
		int soqlCnt = 0;
		int totalSoqlCnt = 0;
		start: for (SObject sObject : classes) {
			if (sObject instanceof ApexClass) {
				apexBody = ((ApexClass) sObject).getBody();
				SymbolTable symbolTable = ((ApexClass) sObject).getSymbolTable();
				if (null != symbolTable && null != symbolTable.getTableDeclaration()
						&& symbolTable.getTableDeclaration().getAnnotations().length > 0) {
					for (Annotation annotation : symbolTable.getTableDeclaration().getAnnotations()) {
						if (BPEnforcerConstants.TEST_ANNOTATION.equalsIgnoreCase(annotation.getName())) {

							continue start;
						}

					}

				}
				soqlCnt = BPEnforcerUtility.findDMLSOQLCount(apexBody, SOQL_REGEX);
				//System.out.println(" ApexClass----->" + ((ApexClass) sObject).getName() + " SOQL Count " + soqlCnt);
				totalSoqlCnt += soqlCnt;

			}

		}
		System.out.println("Total SoQL Count in Apex Class " + totalSoqlCnt);
		return totalSoqlCnt;
	}

	/**
	 * 
	 * @param classes
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static Integer checkForSOQLInApexTrigger(List<SObject> classes) throws ConnectionException, IOException {

		String apexBody = null;

		int soqlCnt = 0;
		int totalSoqlCnt = 0;
		for (SObject sObject : classes) {
			if (sObject instanceof ApexTrigger) {
				apexBody = ((ApexTrigger) sObject).getBody();
				soqlCnt = BPEnforcerUtility.findDMLSOQLCount(apexBody, SOQL_REGEX);
				/*System.out
						.println(" Apex Trigger----->" + ((ApexTrigger) sObject).getName() + " SOQL Count " + soqlCnt);*/
				totalSoqlCnt += soqlCnt;

			}

		}
		System.out.println("Total SoQL Count in Apex Trigger " + totalSoqlCnt);
		return totalSoqlCnt;
	}

	/**
	 * 
	 * @param classes
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static Integer checkForDMLInApexClass(List<SObject> classes) throws ConnectionException, IOException {

		String apexBody = null;
		int dmlCnt = 0;
		int totalDMLCnt = 0;
		start: for (SObject sObject : classes) {
			if (sObject instanceof ApexClass) {
				apexBody = ((ApexClass) sObject).getBody();
				SymbolTable symbolTable = ((ApexClass) sObject).getSymbolTable();
				if (null != symbolTable && null != symbolTable.getTableDeclaration()
						&& symbolTable.getTableDeclaration().getAnnotations().length > 0) {
					for (Annotation annotation : symbolTable.getTableDeclaration().getAnnotations()) {
						if (BPEnforcerConstants.TEST_ANNOTATION.equalsIgnoreCase(annotation.getName())) {
							continue start;					
							}

					}

				}
				dmlCnt = BPEnforcerUtility.findDMLSOQLCount(apexBody, DML_REGEX);
			//	System.out.println(" ApexClass----->" + ((ApexClass) sObject).getName() + " SOQL Count " + dmlCnt);
				totalDMLCnt += dmlCnt;

			}

		}
		System.out.println("Total DML Count in Apex Class " + totalDMLCnt);
		return totalDMLCnt;
	}

	/**
	 * 
	 * @param classes
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static Integer checkForDMLInApexTrigger(List<SObject> classes) throws ConnectionException, IOException {

		String apexBody = null;
		int dmlCnt = 0;
		int totalDMLCnt = 0;
		for (SObject sObject : classes) {
			if (sObject instanceof ApexTrigger) {
				apexBody = ((ApexTrigger) sObject).getBody();
				dmlCnt = BPEnforcerUtility.findDMLSOQLCount(apexBody, DML_REGEX);
				//System.out.println(" Apex Trigger----->" + ((ApexTrigger) sObject).getName() + " SOQL Count " + dmlCnt);
				totalDMLCnt += dmlCnt;
			}

		}
		System.out.println("Total DML Count in  Apex Trigger " + totalDMLCnt);
		return totalDMLCnt;
	}

	/**
	 * 
	 * @param con
	 * @param classes
	 * @throws ConnectionException
	 */
	public static Map<String, Integer> codeCoverage(ToolingConnection con, List<SObject> classes)
			throws ConnectionException {
		Integer testClsLessCoverageCnt = 0;
		Integer countTotalLines = 0;
		Map<String, Integer> codeCoverageDtls = new HashMap<>();
		String idsStr = "";
		ApexClass apexClassDtl;
		Set<String> apexClassesCnt = new TreeSet<String>();
		start:for (SObject sObject : classes) {
			if (sObject instanceof ApexClass) {
				apexClassDtl = ((ApexClass) sObject);
				CodeAnalyser.getApexClassModifiedDetails().put(apexClassDtl.getName(), new ReportType(apexClassDtl.getLastModifiedBy().getName(),apexClassDtl.getLastModifiedDate()));
				SymbolTable symbolTable = ((ApexClass) sObject).getSymbolTable();
				if (null != symbolTable && null != symbolTable.getTableDeclaration()
						&& symbolTable.getTableDeclaration().getAnnotations().length > 0) {
					for (Annotation annotation : symbolTable.getTableDeclaration().getAnnotations()) {
						if (BPEnforcerConstants.TEST_ANNOTATION.equalsIgnoreCase(annotation.getName())) {
							continue start;					
							}

					}

				}
				apexClassesCnt.add( sObject.getId());
				idsStr = "'" + sObject.getId() + "'," + idsStr;
			}
			
		}
		int covered;
		int percent = 0;
		int total;

			if(null!=idsStr && idsStr.length()>1){
				String query = "select id,Coverage,ApexClassOrTrigger.Name,NumLinesCovered,NumLinesUncovered from ApexCodeCoverageAggregate where ApexClassorTriggerId in ("
						+ idsStr.substring(0, idsStr.length() - 1) + ")";
				
				System.out.println("query length"+query.toString().length());
				
				System.out.println("query :  "+query.toString());
				
				

				QueryResult qres = con.query(query.toString());
				
				for (SObject s : qres.getRecords()) {
					if (s instanceof ApexCodeCoverageAggregate) {
						covered = ((ApexCodeCoverageAggregate) s).getNumLinesCovered();
						total = (((ApexCodeCoverageAggregate) s).getNumLinesCovered()
								+ ((ApexCodeCoverageAggregate) s).getNumLinesUncovered());
		
						if (total > 0) {
							percent = (covered / total) * 100;
						}
						//System.out.println("percent"+percent);

						countTotalLines += total;
						// System.out.println("percent "+percent +" Lines Covered
						// "+((ApexCodeCoverageAggregate)s).getNumLinesCovered()+ " test
						// "+((ApexCodeCoverageAggregate)s).getNumLinesUncovered()+"
						// NAME
						// "+((ApexCodeCoverageAggregate)s).getApexClassOrTrigger().getName());
						
						
						if (percent < BPEnforcerConstants.TEST_COVERAGE_PERCENT) {
							testClsLessCoverageCnt++;
							/*System.out.println("Class  NAME  having less coverage "
									+ ((ApexCodeCoverageAggregate) s).getApexClassOrTrigger().getName());*/
						}
					}
		
				}

			}
		

		
		codeCoverageDtls.put(BPEnforcerConstants.LOC_APEX, countTotalLines);
		codeCoverageDtls.put(BPEnforcerConstants.TESTCLS_APEXCOVERAGE, testClsLessCoverageCnt);
		codeCoverageDtls.put(BPEnforcerConstants.APEX_CLASS, apexClassesCnt.size());
		return codeCoverageDtls;

	}

}
