package org.mule.modules.caas.internal.operations;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationNotFoundException;
import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.api.ReadDocumentAttributes;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationDocument;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class ReadDocumentOperation {

    private static final Logger logger = LoggerFactory.getLogger(ReadDocumentOperation.class);

    @MediaType("*/*")

    public Result<InputStream, ReadDocumentAttributes> readDocument(@Optional String configId, String key) throws ConfigurationServiceException {

        //retrieve the configuration in the static cache.
        java.util.Optional<ApplicationConfiguration> appConfig = StaticConfigCache.get()
                .find(configId);

        if (!appConfig.isPresent()) {
            StaticConfigCache.get().findOne();
        }
        Map<String, Object> doc = appConfig.get().findAppConfigAndDocument(key, appConfig.get());
        if (doc == null)
            throw new ConfigurationServiceException("Could not find document " + key + " in application " + appConfig.get().getName());
        ConfigurationServiceConfig serviceConfig = StaticConfigCache.get().
                getServiceUrl(configId).orElseThrow(() -> new ConfigurationServiceException("Cannot find config"));

        //fix for importing app document
        ApplicationDocument applicationDocument = (ApplicationDocument) doc.get("document");
        ApplicationConfiguration applicationConfiguration = (ApplicationConfiguration) doc.get("applicationConfiguration");
        ApplicationDataProvider provider = ApplicationDataProvider.factory.newApplicationDataProvider(serviceConfig);

        try {
            return Result.<InputStream, ReadDocumentAttributes>builder()
                    .attributes(new ReadDocumentAttributes(applicationDocument.getName(), applicationDocument.getContentType()))
                    .mediaType(org.mule.runtime.api.metadata.MediaType.parse(applicationDocument.getContentType()))
                    .output(provider.loadDocument(applicationDocument, applicationConfiguration))
                    .build();

        } catch (ConfigurationNotFoundException ex) {
            throw new ConfigurationServiceException(ex);
        }

    }


}
