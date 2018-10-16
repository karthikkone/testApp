package com.infy.services.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class ConfigSettings {
	
	@NotNull
    @Size(min = 5, max = 50)
	private String sfdcUser; 
	
	@NotNull
    @Size(min = 5, max = 50)
	private String sfdcPassword;
	
	
    @Size(min = 8, max = 50)
	private String proxyUser;
	

    @Size(min = 8, max = 50)
	private String proxyPassword;
	
	@NotNull
    @Size(min = 8, max = 50)
	private String authUrl;
	
	
    @Size(min = 5, max = 50)
	private String agileProUser;	
    @Size(min = 5, max = 50)
	private String agileProPwd;
    @Size(min = 8, max = 50)
	private String 	agileProURL;
	
	
	private String BestPractices;
	private String CodeStyle;
	private String ErrorProne;
	private String Performance;
	
	private String Design;
	private String Security;
	
	private String RemoteAuth;
	
	
	public String getRemoteAuth() {
		return RemoteAuth;
	}

	public void setRemoteAuth(String remoteAuth) {
		RemoteAuth = remoteAuth;
	}

	
	/*private String RemoteAuthName;
	


	public String getRemoteAuthName() {
		return RemoteAuthName;
	}

	public void setRemoteAuthName(String remoteAuthName) {
		RemoteAuthName = remoteAuthName;
	}*/

	public String getBestPractices() {
		return BestPractices;
	}

	public void setBestPractices(String bestPractices) {
		BestPractices = bestPractices;
	}
	public String getCodeStyle() {
		return CodeStyle;
	}
	public void setCodeStyle(String codeStyle) {
		CodeStyle = codeStyle;
	}
	public String getErrorProne() {
		return ErrorProne;
	}
	public void setErrorProne(String errorProne) {
		ErrorProne = errorProne;
	}
	public String getPerformance() {
		return Performance;
	}
	public void setPerformance(String performance) {
		Performance = performance;
	}
	public String getDesign() {
		return Design;
	}
	public void setDesign(String design) {
		Design = design;
	}

	public String getSecurity() {
		return Security;
	}

	public void setSecurity(String security) {
		Security = security;
	}


	
	
	
	

    
    
	
    public String getAgileProUser() {
		return agileProUser;
	}

	public void setAgileProUser(String agileProUser) {
		this.agileProUser = agileProUser;
	}

	public String getAgileProPwd() {
		return agileProPwd;
	}

	public void setAgileProPwd(String agileProPwd) {
		this.agileProPwd = agileProPwd;
	}

	public String getAgileProURL() {
		return agileProURL;
	}

	public void setAgileProURL(String agileProURL) {
		this.agileProURL = agileProURL;
	}

	
	
	public String getSfdcPassword() {
		return sfdcPassword;
	}

	public void setSfdcPassword(String sfdcPassword) {
		this.sfdcPassword = sfdcPassword;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}
	
	public String getSfdcUser() {
		return sfdcUser;
	}

	public void setSfdcUser(String sfdcUser) {
		this.sfdcUser = sfdcUser;
	}

	


}
