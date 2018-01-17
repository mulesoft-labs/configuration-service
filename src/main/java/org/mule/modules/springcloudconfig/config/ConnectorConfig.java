package org.mule.modules.springcloudconfig.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@Configuration(friendlyName = "Configuration Service Connector")
public class ConnectorConfig {
	
	/**
	 * The base URL where the Spring Cloud Config API is hosted.
	 */
	@Configurable
	@Default("http://localhost:8888/")
	private String configServerBaseUrl;
	
	/**
	 * The name of the application whose properties will be read. If not specified, mule app name will be
	 * used.
	 */
	@Configurable
    @Optional
	private String applicationName;


	/**
	 * The version of the application on the Configuration Service.
	 */
	@Configurable
	private String version;


	/**
	 * The environment of the application on the configuration service.
	 */
	@Configurable
	private String environment;


	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getConfigServerBaseUrl() {
		return configServerBaseUrl;
	}

	public void setConfigServerBaseUrl(String configServerBaseUrl) {
		this.configServerBaseUrl = configServerBaseUrl;
	}

	@Override
	public String toString() {
		return "ConnectorConfig{" +
				"configServerBaseUrl='" + configServerBaseUrl + '\'' +
				", applicationName='" + applicationName + '\'' +
				", version='" + version + '\'' +
				", environment='" + environment + '\'' +
				'}';
	}
}