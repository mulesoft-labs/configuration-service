package org.mule.modules.caas.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.caas.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Configuration(friendlyName = "Configuration Service Connector")
public class ConnectorConfig implements ServiceConfiguration{

    public enum SystemPropertiesMode {
        SYSTEM_PROPERTIES_MODE_NEVER,
        SYSTEM_PROPERTIES_MODE_FALLBACK,
        SYSTEM_PROPERTIES_MODE_OVERRIDE
    }

	/**
	 * The base URL where the Spring Cloud Config API is hosted.
	 */
	@Configurable
	@Default("http://localhost:8888/")
    @FriendlyName("Configuration Service URL")
	@Placement(group = "Basic", order = 1)
	private String serviceUrl;
	
	/**
	 * The name of the application whose properties will be read. If not specified, mule app name will be
	 * used.
	 */
	@Configurable
    @Optional
    @FriendlyName("Application Name")
	@Placement(group = "Basic", order = 2)
	private String application;


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


	@Configurable
    @Optional @Default("0")
    @Placement(group = "Property Placeholder", order = 2)
	private int order;


    @Configurable
    @Default("false")
    @Placement(group = "Property Placeholder", order = 1)
	private boolean ignoreUnresolvablePlaceholders;

    @Configurable
    @Default("SYSTEM_PROPERTIES_MODE_FALLBACK")
    @Placement(group = "Property Placeholder", order = 3)
    private SystemPropertiesMode systemPropertiesMode;


    //encryption settings.
	@Configurable
	@Optional @Default("false")
	@Placement(tab = "Encryption", order = 1)
    private boolean enableClientDecryption;

	@Configurable
	@Optional @Placement(tab = "Encryption", order = 2)
    private String clientDecryptionKeyStore;

	@Configurable
	@Optional @Password @Placement(tab = "Encryption", order = 3)
    private String clientDecryptionKeyStorePassword;

	@Configurable
	@Optional @Placement(tab = "Encryption", order = 4)
    private String macKeyAlias;

	@Configurable
	@Optional @Password @Placement(tab = "Encryption", order = 5)
    private String macKeyPassword;

	@Configurable
	@Optional @Placement(tab = "Encryption", order = 6)
	private String wrapKeyAlias;

	@Configurable
	@Optional @Password @Placement(tab = "Encryption", order = 7)
	private String wrapKeyPassword;

    @Override
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = readEnvironmentalProperties(application);
    }

    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = readEnvironmentalProperties(version);
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = readEnvironmentalProperties(environment);
	}

    @Override
    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = readEnvironmentalProperties(serviceUrl);
    }

    public String getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(String trustStore) {
		this.trustStore = readEnvironmentalProperties(trustStore);
	}

	public String getTrustStorePassword() {
		return readEnvironmentalProperties(trustStorePassword);
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = readEnvironmentalProperties(trustStorePassword);
	}

	public String getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(String keyStore) {
		this.keyStore = readEnvironmentalProperties(keyStore);
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = readEnvironmentalProperties(keyStorePassword);
	}

	public boolean isDisableHostNameVerification() {
		return disableHostNameVerification;
	}

	@Override
	public boolean isEnableClientDecryption() {
		return enableClientDecryption;
	}

	@Override
	public String getClientDecryptionKeyStore() {
		return clientDecryptionKeyStore;
	}

	@Override
	public String getClientDecryptionKeyStorePassword() {
		return clientDecryptionKeyStorePassword;
	}

	@Override
	public String getMacKeyAlias() {
		return macKeyAlias;
	}

	@Override
	public String getMacKeyPassword() {
		return macKeyPassword;
	}

	@Override
	public String getWrapKeyAlias() {
		return wrapKeyAlias;
	}

	@Override
	public String getWrapKeyPassword() {
		return wrapKeyPassword;
	}

	public void setEnableClientDecryption(boolean enableClientDecryption) {
		this.enableClientDecryption = enableClientDecryption;
	}

	public void setClientDecryptionKeyStore(String clientDecryptionKeyStore) {
		this.clientDecryptionKeyStore = readEnvironmentalProperties(clientDecryptionKeyStore);
	}

	public void setClientDecryptionKeyStorePassword(String clientDecryptionKeyStorePassword) {
		this.clientDecryptionKeyStorePassword = readEnvironmentalProperties(clientDecryptionKeyStorePassword);
	}

	public void setMacKeyAlias(String macKeyAlias) {
		this.macKeyAlias = readEnvironmentalProperties(macKeyAlias);
	}

	public void setMacKeyPassword(String macKeyPassword) {
		this.macKeyPassword = readEnvironmentalProperties(macKeyPassword);
	}

	public void setWrapKeyAlias(String wrapKeyAlias) {
		this.wrapKeyAlias = readEnvironmentalProperties(wrapKeyAlias);
	}

	public void setWrapKeyPassword(String wrapKeyPassword) {
		this.wrapKeyPassword = readEnvironmentalProperties(wrapKeyPassword);
	}

	public void setDisableHostNameVerification(boolean disableHostNameVerification) {
		this.disableHostNameVerification = disableHostNameVerification;
	}

	public String getLocalEnvironmentName() {
		return localEnvironmentName;
	}

	public void setLocalEnvironmentName(String localEnvironmentName) {
		this.localEnvironmentName = readEnvironmentalProperties(localEnvironmentName);
	}

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isIgnoreUnresolvablePlaceholders() {
        return ignoreUnresolvablePlaceholders;
    }

    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public SystemPropertiesMode getSystemPropertiesMode() {
        return systemPropertiesMode;
    }

    public void setSystemPropertiesMode(SystemPropertiesMode systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }

    @Override
	public String toString() {
		return "ConnectorConfig{" +
				"serviceUrl='" + serviceUrl + '\'' +
				", application='" + application + '\'' +
				", version='" + version + '\'' +
				", environment='" + environment + '\'' +
				'}';
	}

    ///UTILITY METHODS TO ALLOW USAGE OF PLACEHOLDERS WITHIN THE PLACEHOLDERS


    public String readEnvironmentalProperties(String text)
    {
        Pattern propertyPatter = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher propertyMatcher = propertyPatter.matcher(text);
        String modifiedText = text;
        while (propertyMatcher.find())
        {
            String property = propertyMatcher.group(1);
            modifiedText = replaceProperty(modifiedText, property);
        }

        return modifiedText;
    }

    private String replaceProperty(String modifiedText, String property)
    {
        String propertyValue = System.getProperty(property);
        checkForPropertyExistence(property, propertyValue);
        String pattern = "\\$\\{(" + property + ")\\}";
        Pattern replacement = Pattern.compile(pattern);
        Matcher replacementMatcher = replacement.matcher(modifiedText);
        replacementMatcher.find();
        return replacementMatcher.replaceAll(Matcher.quoteReplacement(propertyValue));
    }

    private void checkForPropertyExistence(String property, String propertyValue)
    {
        if (propertyValue == null)
        {
            throw new RuntimeException("Property " + property + " could not be found");
        }
    }

}