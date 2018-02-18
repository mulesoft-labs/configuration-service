package org.mule.modules.caas.api;

import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.client.DefaultApplicationDataProvider;
import org.mule.modules.caas.internal.CaasConfigurationPropertiesProvider;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.util.Preconditions;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.core.internal.registry.MuleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;


public class ConfigurationServicePropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

    public static final String URL_PARAM = "serviceUrl";
    public static final String APP_PARAM = "application";
    public static final String VER_PARAM = "version";
    public static final String ENV_PARAM = "environment";
    public static final String NAME_PARAM = "name";

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServicePropertiesProviderFactory.class);

    @Inject
    private MuleRegistry registry;

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
            String name = parameters.getStringParameter(NAME_PARAM);

            Preconditions.checkArgument(url != null, "Service URL must not be null");
            Preconditions.checkArgument(app != null, "Application Name must not be null");
            Preconditions.checkArgument(ver != null, "Version must not be null");
            Preconditions.checkArgument(env != null, "Environment must not be null");

            DefaultApplicationDataProvider provider = new DefaultApplicationDataProvider(url, ClientBuilder.newClient());
            ApplicationConfiguration appConfig = provider.loadApplicationConfiguration(app, ver, env);

            //store in static config cache for further use.
            StaticConfigCache.get().store(name, url, appConfig);

            return new CaasConfigurationPropertiesProvider(url, appConfig);
        } catch (ConfigurationServiceException ex) {
            throw new RuntimeException("Error while loading configuration", ex);
        }
    }
}
