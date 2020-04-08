package org.mule.modules.caas.internal.operations;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.SecurePropertyPlaceholderModule;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DecryptValueOperation {
    Logger LOGGER = LoggerFactory.getLogger(DecryptValueOperation.class);

    @MediaType(MediaType.ANY)
    @DisplayName(value = "Decrypt Value")
    public String decryptValue(String configId, String encryptedValue) throws Exception {
        //retrieve the configuration in the static cache.
        ConfigurationServiceConfig serviceConfig = StaticConfigCache.get().
                getServiceUrl(configId).orElseThrow(() -> new ConfigurationServiceException("Cannot find config"));

        SecurePropertyPlaceholderModule securePropertyPlaceholderModule = serviceConfig.getSecurePropertyPlaceholderModule();
        return securePropertyPlaceholderModule.convertPropertyValue(encryptedValue);
    }

}
