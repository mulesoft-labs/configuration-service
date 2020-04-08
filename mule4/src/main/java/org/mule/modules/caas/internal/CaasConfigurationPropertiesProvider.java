package org.mule.modules.caas.internal;

import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.util.Optional;

public class CaasConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

    private final ApplicationConfiguration config;
    private final String serviceUrl;
    private ConfigurationServiceConfig connectorConfig;
    private SecurePropertyPlaceholderModule securePropertyPlaceholderModule;

    public CaasConfigurationPropertiesProvider(String serviceUrl, ApplicationConfiguration config, ConfigurationServiceConfig configurationServiceConfig, SecurePropertyPlaceholderModule securePropModule) throws ConfigurationServiceException {
        this.config = config;
        this.serviceUrl = serviceUrl;
        this.connectorConfig = configurationServiceConfig;
        this.securePropertyPlaceholderModule = securePropModule;
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        String value = config.readProperty(configurationAttributeKey);
        if (value == null) {

            //try to read from system properties now
            value = securePropertyPlaceholderModule.resolveEnvProperties(configurationAttributeKey);
        }
        if (value == null) {
            return Optional.empty();
        }
        String decryptedValue = securePropertyPlaceholderModule.convertPropertyValue(value);
        return Optional.of(new ConfigurationProperty() {
            @Override
            public Object getSource() {
                return decryptedValue;
            }

            @Override
            public Object getRawValue() {
                return decryptedValue;
            }

            @Override
            public String getKey() {
                return configurationAttributeKey;
            }
        });

    }

    @Override
    public String getDescription() {
        return "Configuration Service: " + serviceUrl + " and coordinates: {" +
                "application: " + config.getName() +
                ", version: " + config.getVersion() +
                ", environment: " + config.getEnvironment()
                + "}";
    }
}
