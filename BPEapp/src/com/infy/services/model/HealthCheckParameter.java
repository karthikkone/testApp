/**
*
* Model bean for Health Check Parameters
* @author  Mamatha HR
* @version 1.0
* @since   2017-09-28
*/
package com.infy.services.model;

public class HealthCheckParameter {
	
	private Integer securityScore;
	
	private Integer apexClassLines;
	
	private Integer apexPageLines;
	
	private Integer apexClsSoql;
	
	private Integer apexTriggerSoql;
	
	private Integer apexClsDML;
	
	private Integer apexTriggerDML;
	
	private Integer lessCodeCoverageCnt;
	
	private Integer totalApexCls;
	
	private Integer totalApexPage;
	
	private Integer totalApexComponents;

	/**
	 * @return the securityScore
	 */
	public Integer getSecurityScore() {
		return securityScore;
	}

	/**
	 * @param securityScore the securityScore to set
	 */
	public void setSecurityScore(Integer securityScore) {
		this.securityScore = securityScore;
	}

	/**
	 * @return the apexClassLines
	 */
	public Integer getApexClassLines() {
		return apexClassLines;
	}

	/**
	 * @param apexClassLines the apexClassLines to set
	 */
	public void setApexClassLines(Integer apexClassLines) {
		this.apexClassLines = apexClassLines;
	}

	
	/**
	 * @return the apexPageLines
	 */
	public Integer getApexPageLines() {
		return apexPageLines;
	}

	/**
	 * @param apexPageLines the apexPageLines to set
	 */
	public void setApexPageLines(Integer apexPageLines) {
		this.apexPageLines = apexPageLines;
	}

	/**
	 * @return the apexClsSoql
	 */
	public Integer getApexClsSoql() {
		return apexClsSoql;
	}

	/**
	 * @param apexClsSoql the apexClsSoql to set
	 */
	public void setApexClsSoql(Integer apexClsSoql) {
		this.apexClsSoql = apexClsSoql;
	}

	/**
	 * @return the apexTriggerSoql
	 */
	public Integer getApexTriggerSoql() {
		return apexTriggerSoql;
	}

	/**
	 * @param apexTriggerSoql the apexTriggerSoql to set
	 */
	public void setApexTriggerSoql(Integer apexTriggerSoql) {
		this.apexTriggerSoql = apexTriggerSoql;
	}

	/**
	 * @return the apexClsDML
	 */
	public Integer getApexClsDML() {
		return apexClsDML;
	}

	/**
	 * @param apexClsDML the apexClsDML to set
	 */
	public void setApexClsDML(Integer apexClsDML) {
		this.apexClsDML = apexClsDML;
	}

	/**
	 * @return the apexTriggerDML
	 */
	public Integer getApexTriggerDML() {
		return apexTriggerDML;
	}

	/**
	 * @param apexTriggerDML the apexTriggerDML to set
	 */
	public void setApexTriggerDML(Integer apexTriggerDML) {
		this.apexTriggerDML = apexTriggerDML;
	}

	/**
	 * @return the lessCodeCoverageCnt
	 */
	public Integer getLessCodeCoverageCnt() {
		return lessCodeCoverageCnt;
	}

	/**
	 * @param lessCodeCoverageCnt the lessCodeCoverageCnt to set
	 */
	public void setLessCodeCoverageCnt(Integer lessCodeCoverageCnt) {
		this.lessCodeCoverageCnt = lessCodeCoverageCnt;
	}

	/**
	 * @return the totalApexCls
	 */
	public Integer getTotalApexCls() {
		return totalApexCls;
	}

	/**
	 * @param totalApexCls the totalApexCls to set
	 */
	public void setTotalApexCls(Integer totalApexCls) {
		this.totalApexCls = totalApexCls;
	}

	/**
	 * @return the totalApexPage
	 */
	public Integer getTotalApexPage() {
		return totalApexPage;
	}

	/**
	 * @param totalApexPage the totalApexPage to set
	 */
	public void setTotalApexPage(Integer totalApexPage) {
		this.totalApexPage = totalApexPage;
	}

	/**
	 * @return the totalApexComponents
	 */
	public Integer getTotalApexComponents() {
		return totalApexComponents;
	}

	/**
	 * @param totalApexComponents the totalApexComponents to set
	 */
	public void setTotalApexComponents(Integer totalApexComponents) {
		this.totalApexComponents = totalApexComponents;
	}

}
