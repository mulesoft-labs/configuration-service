package org.mule.modules.caas.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;


@Configuration(friendlyName = "Configuration Service Connector")
public class ConnectorConfig {
	
	/**
	 * The base URL where the Spring Cloud Config API is hosted.
	 */
	@Configurable
	@Default("http://localhost:8888/")
	@Placement(group = "Basic", order = 1)
	private String configServerBaseUrl;
	
	/**
	 * The name of the application whose properties will be read. If not specified, mule app name will be
	 * used.
	 */
	@Configurable
    @Optional
	@Placement(group = "Basic", order = 2)
	private String applicationName;


	/**
	 * The version of the application on the Configuration Service.
	 */
	@Configurable
	@Placement(group = "Basic", order = 3)
	private String version;


	/**
	 * The environment of the application on the configuration service.
	 */
	@Configurable
	@Placement(group = "Basic", order = 4)
	private String environment;


	/**
	 * Defines the environment name that should be considered as local. This setting is useful for
	 * development as avoids the initial need to have a configuration service present.
	 */
	@Configurable
	@Optional
	@Placement(group = "Basic", order = 5)
	private String localEnvironmentName;

	/**
	 * The location of the trust store either in the classpath or in the filesystem.
	 */
	@Configurable
	@Optional @Placement(group = "SSL", tab = "SSL", order = 1)
	private String trustStore;

	/**
	 * The password of the trust store.
	 */
	@Configurable
	@Password
	@Optional @Placement(group = "SSL", tab = "SSL", order = 2)
	private String trustStorePassword;

	/**
	 * The location of the keystore either in the classpath or in the filesystem.
	 */
	@Configurable
	@Optional @Placement(group = "SSL", tab = "SSL", order = 3)
	private String keyStore;

	/**
	 * The password of the keystore
	 */
	@Configurable
	@Password
	@Optional @Placement(group = "SSL", tab = "SSL", order = 4)
	private String keyStorePassword;

	@Configurable
	@Default("false")
	@Optional @Placement(group = "SSL", tab = "SSL", order = 5)
	private boolean disableHostNameVerification;

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

	public String getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public boolean isDisableHostNameVerification() {
		return disableHostNameVerification;
	}

	public void setDisableHostNameVerification(boolean disableHostNameVerification) {
		this.disableHostNameVerification = disableHostNameVerification;
	}

	public String getLocalEnvironmentName() {
		return localEnvironmentName;
	}

	public void setLocalEnvironmentName(String localEnvironmentName) {
		this.localEnvironmentName = localEnvironmentName;
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