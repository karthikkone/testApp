package com.infy.bpe.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.infy.report.model.ReportType;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;
import com.sforce.soap.enterprise.sobject.AggregateResult;
import com.sforce.soap.enterprise.sobject.CronTrigger;
import com.sforce.soap.enterprise.sobject.Profile;
import com.sforce.soap.enterprise.sobject.User;
import com.sforce.ws.ConnectionException;

public class EnterpriseOperations {
	public static ArrayList<ReportType> dashboard() throws ConnectionException {
		// System.out.println(ent == null);
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ReportType reportType;
		com.sforce.soap.enterprise.QueryResult qr = DataStore.ent.query(
				"select Count(ID) cnt,DashboardId dashing from DashboardComponent Group by DashboardId Order by Count(ID) asc");
		if (qr.isDone()) {
			for (com.sforce.soap.enterprise.sobject.SObject dash : qr.getRecords()) {
				AggregateResult dc = (AggregateResult) dash;
				/*
				 * System.out.println(dc.getField("cnt") + "----" +
				 * dc.getField("dashing"));
				 */
				if (Integer.parseInt((String.valueOf(dc.getField("cnt")))) < DataStore.DASHBOARDCOMPONENTS) {
					reportType = new ReportType((String) dc.getField("dashing"), CategoryConstants.DASHBOARD, "five or less dashboard components",0);
					reportType.setComponentType(BPEnforcerConstants.DASHBOARD);
					reportType.setComponentId(dc.getId());
					reportType.setIssueType(BPEnforcerConstants.LOW);
					reportType.setSheetName(BPEnforcerConstants.DASHBOARD_COMPONENTS);
					reporttypelist.add(reportType);
				}
			}

		}
		return reporttypelist;
	}

	public static ArrayList<ReportType> scheduledJobs() throws ConnectionException {
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ReportType reportType;
		com.sforce.soap.enterprise.QueryResult qrt = DataStore.ent
				.query("SELECT id,createdbyid,CronJobDetailId,CronJobDetail.name FROM CronTrigger");
		if (qrt.isDone()) {
			for (com.sforce.soap.enterprise.sobject.SObject schedule : qrt.getRecords()) {
				CronTrigger sc = (CronTrigger) schedule;
				// System.out.println(sc.getId() + "-----" +
				// sc.getCreatedById());

				if (!sc.getCreatedById().equals(DataStore.GENERIC_USERID)) {
					reportType = new ReportType(sc.getCronJobDetail().getName(), 5,
							"batch jobs not scheduled by generic account", 0);
					reportType.setComponentId(schedule.getId());
					reportType.setComponentType(BPEnforcerConstants.JOBS);
					reportType.setIssueType(BPEnforcerConstants.LOW);
					reportType.setSheetName(BPEnforcerConstants.SCHEDULED_JOBS);
					reporttypelist.add(reportType);
					/*
					 * reporttypelist.add(new ReportType(sc.getId(), 5,
					 * "batch jobs not scheduled by generic account", 0));
					 */
				}
			}
		}
		return reporttypelist;

	}

	public static ArrayList<ReportType> profile() throws ConnectionException, InterruptedException {
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		HashMap<String, String> profilemap = new HashMap<String, String>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		String sysAdminId = null;
		int sysAdminCount = 0;
		com.sforce.soap.enterprise.QueryResult qrt2 = DataStore.ent.query("SELECT id,name from profile");

		if (qrt2.isDone()) {
			for (com.sforce.soap.enterprise.sobject.SObject sob : qrt2.getRecords()) {
				Profile p = (Profile) sob;
				// System.out.println(p.getId() + p.getName());
				if (p.getName().equalsIgnoreCase("System Administrator"))
					sysAdminId = p.getId();
				profilemap.put(p.getId(), p.getName());
			}
		}
		com.sforce.soap.enterprise.QueryResult qrt = DataStore.ent
				.query("SELECT id,profileid from user where isactive=true");
		// while (!qrt.isDone()) {
		// Thread.sleep(5000);
		// }
		if (qrt.isDone()) {
			for (com.sforce.soap.enterprise.sobject.SObject sob : qrt.getRecords()) {
				User u = (User) sob;
				// System.out.println(u.getProfileId());
				if (u.getProfileId().equalsIgnoreCase(sysAdminId)) {
					sysAdminCount++;
				}

				Integer i = countmap.get(u.getProfileId());
				if (i == null) {
					i = 0;
				}
				i++;
				countmap.put(u.getProfileId(), i);
			}
		}
		/*
		 * System.out.println("1234" + sysAdminCount); for (String s :
		 * countmap.keySet()) { System.out.println(s + "----" +
		 * countmap.get(s)); }
		 */
		return reporttypelist;
	}

	// mary changes
	public static ArrayList<ReportType> inactiveuser() throws ConnectionException {
		ArrayList<ReportType> reporttypelist = new ArrayList<ReportType>();
		ReportType reportType;
		com.sforce.soap.enterprise.QueryResult qrt = DataStore.ent
				.query("SELECT id,name,lastmodifieddate,LastModifiedBy.name from user where isactive=false");
		System.out.println("Checking for inactive user...");
		if (qrt.getSize() > 0) {
			boolean done = false;
			while (!done) {
				for (com.sforce.soap.enterprise.sobject.SObject sob : qrt.getRecords()) {
					User userRec = (User) sob;
					// System.out.println("User name "+userRec.getName());
					reportType = new ReportType(userRec.getId(), CategoryConstants.INACTIVE_USER, "inactive user", 0);
					reportType.setComponentType(BPEnforcerConstants.INACTIVE_USER);
					reportType.setIssueType(BPEnforcerConstants.LOW);
					reportType.setSheetName(BPEnforcerConstants.INACTIVE_USER);
					if (null != userRec.getLastModifiedBy()) {
						reportType.setLastModifiedBy(userRec.getLastModifiedBy().getName());
					}

					reportType.setLastModifiedDate(userRec.getLastModifiedDate());
					reporttypelist.add(reportType);
				}
				if (qrt.isDone()) {
					done = true;
				} else {
					qrt = DataStore.ent.queryMore(qrt.getQueryLocator());
				}
			}
		}
		return reporttypelist;

	}
}
