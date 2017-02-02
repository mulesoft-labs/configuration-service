package org.mule.modules.springcloudconfig.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@Configuration(friendlyName = "Spring Cloud Configuration")
public class ConnectorConfig {
	
	@Configurable
	@Default("http://localhost:8888/")
	private String configServerBaseUrl;
	
	@Configurable
	private String applicationName;
	
	@Configurable
	@Optional
	private String profile;
	
	@Configurable
	@Optional
	private String label;


	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getConfigServerBaseUrl() {
		return configServerBaseUrl;
	}

	public void setConfigServerBaseUrl(String configServerBaseUrl) {
		this.configServerBaseUrl = configServerBaseUrl;
	}

	@Override
	public String toString() {
		return "ConnectorConfig [configServerBaseUrl=" + configServerBaseUrl + ", applicationName=" + applicationName
				+ ", profile=" + profile + ", label=" + label + "]";
	}
	
	
}