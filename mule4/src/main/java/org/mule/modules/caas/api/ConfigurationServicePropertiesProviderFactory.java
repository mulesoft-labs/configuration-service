package org.mule.modules.caas.api;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.internal.CaasConfigurationPropertiesProvider;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.SecurePropertyPlaceholderModule;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.util.Preconditions;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    public static final String ENABLECLIENTDEC_PARAM = "enableClientDecryption";
    public static final String CLIENTDECKS_PARAM = "clientDecryptionKeyStore";
    public static final String CLIENTDECKSPW_PARAM = "clientDecryptionKeyStorePassword";
    public static final String CLIENTDECMAC_PARAM = "macKeyAlias";
    public static final String CLIENTDECMACPW_PARAM = "macKeyPassword";
    public static final String CLIENTDECWRAP_PARAM = "wrapKeyAlias";
    public static final String CLIENTDECWRAPPW_PARAM = "wrapKeyPassword";
    //secure propertis
    public static final String ENCRYPTION_ALGORITHM = "encryptionAlgorithm";
    public static final String KEY = "key";
    public static final String ENCRYPTION_MODE = "encryptionMode";
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServicePropertiesProviderFactory.class);

    public static final ComponentIdentifier CONFIG_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("configuration-service")
                    .name("config")
                    .build();

    public static final ComponentIdentifier HEADERS_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("configuration-service")
                    .name("custom-headers")
                    .build();

    public static final ComponentIdentifier HEADER_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("configuration-service")
                    .name("custom-header")
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

            String trustStore = getOptionalStringParemeter(parameters, TRUSTSTORE_PARAM);
            String localEnvironmentName = getOptionalStringParemeter(parameters, LOCALENV_PARAM);
            boolean disableDnsLookup = Boolean.parseBoolean(getOptionalStringParemeter(parameters, DISABLEDNS_PARAM));

            //all encryption settings.
            boolean enableClientDecryption = Boolean.parseBoolean(getOptionalStringParemeter(parameters, ENABLECLIENTDEC_PARAM));
            String clientDecryptionKeyStore = getOptionalStringParemeter(parameters, CLIENTDECKS_PARAM);
            String clientDecryptionKeyStorePassword = getOptionalStringParemeter(parameters, CLIENTDECKSPW_PARAM);
            String macKeyAlias = getOptionalStringParemeter(parameters, CLIENTDECMAC_PARAM);
            String macKeyPassword = getOptionalStringParemeter(parameters, CLIENTDECMACPW_PARAM);
            String wrapKeyAlias = getOptionalStringParemeter(parameters, CLIENTDECWRAP_PARAM);
            String wrapKeyPassword = getOptionalStringParemeter(parameters, CLIENTDECWRAPPW_PARAM);

            //secure properties
            String encryptionAlgorithm = getDefaultStringParemeter(parameters, ENCRYPTION_ALGORITHM);
            String encryptionMode = getDefaultStringParemeter(parameters, ENCRYPTION_MODE);
            String encryptionKey = getDefaultStringParemeter(parameters, KEY);
            SecurePropertyPlaceholderModule securePropertyPlaceholderModule = new SecurePropertyPlaceholderModule();
            securePropertyPlaceholderModule.setEncryptionMode(EncryptionMode.valueOf(encryptionMode));
            securePropertyPlaceholderModule.setEncryptionAlgorithm(EncryptionAlgorithm.valueOf(encryptionAlgorithm));
            securePropertyPlaceholderModule.setKey(encryptionKey);

            String keyPassword = securePropertyPlaceholderModule.convertPropertyValue(getOptionalStringParemeter(parameters, KEYPASSWD_PARAM));
            String trustPassword = securePropertyPlaceholderModule.convertPropertyValue(getOptionalStringParemeter(parameters, TRUSTPASSWD_PARAM));
            Map<String, String> headers = new LinkedHashMap<>();

            List<ConfigurationParameters> headerConfigs = parameters.getComplexConfigurationParameter(HEADERS_IDENTIFIER);

            //this could be just one
            Optional<ConfigurationParameters> headersParam = headerConfigs.stream().findAny();

            if (headersParam.isPresent()) {
                List<ConfigurationParameters> headersParams = headersParam.get().getComplexConfigurationParameter(HEADER_IDENTIFIER);
                headersParams.forEach(hp -> {
                    String key = getOptionalStringParemeter(hp, "key");
                    String value = getOptionalStringParemeter(hp, "value");
                    logger.debug("Header Name: {},  value: {}", key, value);
                    headers.put(key, value);
                });
            }


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

            config.setEnableClientDecryption(enableClientDecryption);
            config.setClientDecryptionKeyStore(clientDecryptionKeyStore);
            config.setClientDecryptionKeyStorePassword(clientDecryptionKeyStorePassword);
            config.setWrapKeyAlias(wrapKeyAlias);
            config.setWrapKeyPassword(wrapKeyPassword);
            config.setMacKeyAlias(macKeyAlias);
            config.setMacKeyPassword(macKeyPassword);

            config.setCustomHeaders(headers);
            //secure properties
            config.setKey(encryptionKey);
            config.setEncryptionAlgorithm(EncryptionAlgorithm.valueOf(encryptionAlgorithm));
            config.setEncryptionMode(EncryptionMode.valueOf(encryptionMode));
            config.setSecurePropertyPlaceholderModule(securePropertyPlaceholderModule);

            ApplicationDataProvider provider = ApplicationDataProvider.factory.newApplicationDataProvider(config);

            ApplicationConfiguration appConfig = provider.loadApplicationConfiguration(app, ver, env);

            //store in static config cache for further use.
            StaticConfigCache.get().store(name, config, appConfig);

            return new CaasConfigurationPropertiesProvider(url, appConfig, config, securePropertyPlaceholderModule);
        } catch (ConfigurationServiceException ex) {
            logger.error("Error while loading configuration!", ex);
            throw new RuntimeException("Error while loading configuration", ex);
        }
    }

    private String getOptionalStringParemeter(ConfigurationParameters params, String key) {
        try {
            return params.getStringParameter(key);
        } catch (NullPointerException ex) {
            logger.debug("Parameter {} is not present as part of configuration Parameters: ", key, params);
            //GRRRR
            return null;
        }
    }

    private String getDefaultStringParemeter(ConfigurationParameters params, String key) {
        try {
            return params.getStringParameter(key);
        } catch (NullPointerException ex) {
            logger.debug("Parameter {} is not present as part of configuration Parameters: ", key, params);
            //GRRRR
            return "";
        }
    }
}