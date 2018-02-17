package org.mule.modules.caas.internal;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationNotFoundException;
import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.api.ReadDocumentAttributes;
import org.mule.modules.caas.client.DefaultApplicationDataProvider;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationDocument;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.InputStream;

public class ReadDocumentOperation {

    private static final Logger logger = LoggerFactory.getLogger(ReadDocumentOperation.class);

    @MediaType("*/*")
    public Result<InputStream, ReadDocumentAttributes> readDocument(@Config ConfigurationServiceConfig config,  String key) throws ConfigurationServiceException {

        //retrieve the configuration in the static cache.
        ApplicationConfiguration appConfig = StaticConfigCache.get()
                .find(config.getServiceUrl(), config.getApplication(), config.getVersion(), config.getEnvironment())
                .orElseThrow(() -> new RuntimeException("Application configuration not loaded."));

        ApplicationDocument doc = appConfig.findDocument(key);

        if (doc == null) throw new ConfigurationServiceException("Could not find document " + key + " in application " + appConfig.getName());

        Client client = ClientBuilder.newClient();
        ApplicationDataProvider provider = new DefaultApplicationDataProvider(config.getServiceUrl(), client);

        try {
            return Result.<InputStream, ReadDocumentAttributes>builder()
                    .attributes(new ReadDocumentAttributes(doc.getName(), doc.getContentType()))
                    .mediaType(org.mule.runtime.api.metadata.MediaType.parse(doc.getContentType()))
                    .output(provider.loadDocument(doc, appConfig))
                    .build();

        } catch (ConfigurationNotFoundException ex) {
            throw new ConfigurationServiceException(ex);
        }

    }


}
