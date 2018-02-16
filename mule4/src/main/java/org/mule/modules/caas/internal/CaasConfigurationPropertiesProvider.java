package org.mule.modules.caas.internal;

import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;

import java.util.Optional;

public class CaasConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {
    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return null;
    }
}
