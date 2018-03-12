package org.mule.modules.caas.api;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.internal.CaasConfigurationPropertiesProvider;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.util.Preconditions;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigurationServicePropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

    public static final String URL_PARAM = "serviceUrl";
    public static final String APP_PARAM = "application";
    public static final String VER_PARAM = "version";
    public static final String ENV_PARAM = "environment";
    public static final String NAME_PARAM = "name";
    public static final String KEYSTORE_PARAM = "keyStore";
    public static final String KEYPASSWD_PARAM = "keyStorePassword";
    public static final String TRUSTSTORE_PARAM = "trustStore";
    public static final String TRUSTPASSWD_PARAM = "trustStorePassword";
    public static final String DISABLEDNS_PARAM = "disableHostNameVerification";
    public static final String LOCALENV_PARAM = "localEnvironmentName";


    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServicePropertiesProviderFactory.class);

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
            String keystore = getOptionalStringParemeter(parameters, KEYSTORE_PARAM);
            String keyPassword = getOptionalStringParemeter(parameters, KEYPASSWD_PARAM);
            String trustStore = getOptionalStringParemeter(parameters, TRUSTSTORE_PARAM);
            String trustPassword = getOptionalStringParemeter(parameters, TRUSTPASSWD_PARAM);
            String localEnvironmentName = getOptionalStringParemeter(parameters, LOCALENV_PARAM);
            boolean disableDnsLookup = Boolean.parseBoolean(getOptionalStringParemeter(parameters, DISABLEDNS_PARAM));



            Preconditions.checkArgument(url != null, "Service URL must not be null");
            Preconditions.checkArgument(app != null, "Application Name must not be null");
            Preconditions.checkArgument(ver != null, "Version must not be null");
            Preconditions.checkArgument(env != null, "Environment must not be null");


            ConfigurationServiceConfig config = new ConfigurationServiceConfig();
            config.setApplication(app);
            config.setDisableHostNameVerification(disableDnsLookup);
            config.setEnvironment(env);
            config.setVersion(ver);
            config.setKeyStore(keystore);
            config.setKeyStorePassword(keyPassword);
            config.setServiceUrl(url);
            config.setTrustStore(trustStore);
            config.setTrustStorePassword(trustPassword);
            config.setLocalEnvironmentName(localEnvironmentName);

            ApplicationDataProvider provider = ApplicationDataProvider.factory.newApplicationDataProvider(config);

            ApplicationConfiguration appConfig = provider.loadApplicationConfiguration(app, ver, env);

            //store in static config cache for further use.
            StaticConfigCache.get().store(name, config, appConfig);

            return new CaasConfigurationPropertiesProvider(url, appConfig);
        } catch (ConfigurationServiceException ex) {
            throw new RuntimeException("Error while loading configuration", ex);
        }
    }

    private String getOptionalStringParemeter(ConfigurationParameters params, String key) {
        try {
            return params.getStringParameter(key);
        } catch (NullPointerException ex) {
            //GRRRR
            return null;
        }
    }

}
