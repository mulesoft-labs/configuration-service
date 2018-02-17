package org.mule.modules.caas.api;

import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.internal.CaasConfigurationPropertiesProvider;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.util.Preconditions;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;

import static org.mule.modules.caas.api.ConfigurationServiceExtensionLoadingDelegate.*;

public class ConfigurationServicePropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

    public static final ComponentIdentifier CONFIG_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("configuration-service")
                    .name("config")
                    .build();


    @Override
    public ComponentIdentifier getSupportedComponentIdentifier() {
        return CONFIG_IDENTIFIER;
    }

    @Override
    public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters, ResourceProvider externalResourceProvider) {
        try {
            String url = parameters.getStringParameter(URL_PARAM);
            String app = parameters.getStringParameter(APP_PARAM);
            String ver = parameters.getStringParameter(VER_PARAM);
            String env = parameters.getStringParameter(ENV_PARAM);

            Preconditions.checkArgument(url != null, "Service URL must not be null");
            Preconditions.checkArgument(app != null, "Application Name must not be null");
            Preconditions.checkArgument(ver != null, "Version must not be null");
            Preconditions.checkArgument(env != null, "Environment must not be null");

            return new CaasConfigurationPropertiesProvider(url, app, ver, env);
        } catch (ConfigurationServiceException ex) {
            throw new RuntimeException("Error while loading configuration", ex);
        }
    }
}
