package org.mule.modules.caas.internal.operations;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshDocumentsOperation {
    Logger LOGGER = LoggerFactory.getLogger(RefreshDocumentsOperation.class);

    @MediaType(MediaType.ANY)
    @DisplayName(value = "Refresh Documents")
    public void refreshDocuments(String configId) throws Exception {

        //retrieve the configuration in the static cache.
        ConfigurationServiceConfig serviceConfig = StaticConfigCache.get().
                getServiceUrl(configId).orElseThrow(() -> new ConfigurationServiceException("Cannot find config"));

        ApplicationDataProvider provider = ApplicationDataProvider.factory.newApplicationDataProvider(serviceConfig);
        LOGGER.info("Refreshing configuration for application {}, version {}, env {}", serviceConfig.getApplication(), serviceConfig.getVersion(), serviceConfig.getEnvironment());
        ApplicationConfiguration refreshedAppConfig = provider.loadApplicationConfiguration(serviceConfig.getApplication(), serviceConfig.getVersion(), serviceConfig.getEnvironment());
        //store in static config cache for further use.
        StaticConfigCache.get().store(configId, serviceConfig, refreshedAppConfig);
        LOGGER.info("Documents Reloaded successfully!");

    }

}
