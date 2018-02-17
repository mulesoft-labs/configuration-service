package org.mule.modules.caas.internal;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.client.DefaultApplicationDataProvider;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import javax.ws.rs.client.ClientBuilder;
import java.util.Optional;

public class CaasConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

    private final ApplicationConfiguration config;
    private final String serviceUrl;

    public CaasConfigurationPropertiesProvider(String serviceUrl, ApplicationConfiguration config) throws ConfigurationServiceException {
        this.config = config;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {

        String value = config.readProperty(configurationAttributeKey);

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(new ConfigurationProperty() {
            @Override
            public Object getSource() {
                return value;
            }

            @Override
            public Object getRawValue() {
                return value;
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
