package org.mule.modules.caas.client;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationNotFoundException;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.ServiceConfiguration;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationConfigurationBuilder;
import org.mule.modules.caas.model.ApplicationDocument;
import org.mule.modules.caas.model.ConfigurationDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

public class DefaultApplicationDataProvider implements ApplicationDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationDataProvider.class);


    private final String baseUrl;
    private final Client restClient;
    private final ServiceConfiguration config;

    public DefaultApplicationDataProvider(String baseUrl, Client restClient, ServiceConfiguration config) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;
        this.config = config;
    }

    @Override
    public Map<String, Object> loadApplication(String name, String version, String environment) {

        WebTarget target = restClient.target(baseUrl)
                .path(name)
                .path(version)
                .path(environment);

        //read it as a java map
        Map<String, Object> result = applyHeaders(target.request(), config).accept(MediaType.APPLICATION_JSON).get(Map.class);

        //this potentially logs sensitive information
        //we need to remove the properties before logging.

        if (logger.isDebugEnabled()) {
            Map<String, Object> loggableResult = new LinkedHashMap<>(result);
            loggableResult.remove("properties");
            logger.debug("Got settings from configuration server: {}", loggableResult);
        }

        return result;
    }

    @Override
    public InputStream loadDocument(ApplicationDocument doc, ApplicationConfiguration app) throws ConfigurationNotFoundException {

        WebTarget target = restClient.target(baseUrl)
                .path(app.getName())
                .path(app.getVersion())
                .path(app.getEnvironment())
                .path("dynamic")
                .path(doc.getName());

        InputStream ret = applyHeaders(target.request(), config).accept(doc.getContentType()).get(InputStream.class);

        if (app.getDataWrapper() != null) {
            ret = app.getDataWrapper().wrapStream(ret);
        }

        return ret;
    }

    @Override
    public ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment) throws ConfigurationServiceException {

        ConfigurationDataWrapper wrapper = loadEncryptionDataWrapper();

        return loadApplicationConfiguration(name, version, environment, wrapper, 50);
    }

    private ConfigurationDataWrapper loadEncryptionDataWrapper() {

        if (!config.isEnableClientDecryption()) {
            return null;
        }

        String adminPath = ClientUtils.buildAdminBaseUrl(baseUrl);

        WebTarget target = restClient.target(adminPath)
                .path("security")
                .path("wrappedKey");

        Map<String, String> encKey = applyHeaders(target.request(), config).accept(MediaType.APPLICATION_JSON_TYPE).get(Map.class);


        return EncryptionDataWrapper.builder()
                .withServiceConfiguration(config)
                .withWrappedKeyData(encKey)
                .build();
    }

    private ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment, ConfigurationDataWrapper wrapper, int depth) throws ConfigurationServiceException {
        ApplicationConfigurationBuilder retBuilder = ApplicationConfiguration.builder()
                .setName(name)
                .setEnvironment(environment)
                .setVersion(version);

        if (depth == 0) {
            logger.warn("Reached depth 0 while recursively load importApp configurations. This may indicate a cycle in the importApp/child relationship.");
            return retBuilder.build();
        }

        if (wrapper != null) {
            retBuilder.setDataWrapper(wrapper);
        }

        //load from the API
        Map<String, Object> appData = loadApplication(name, version, environment);

        //get the properties
        Map<String, String> properties = (Map) appData.get("properties");
        if (properties == null) {
            properties = Collections.emptyMap();
        }

        retBuilder.setProperties(properties);

        //get the importApp apps
        List<Map<String, String>> imports = (List) appData.get("imports");

        if (imports == null) {
            imports = Collections.emptyList();
        }

        //go recursively through the imports to build the list.
        for (Map<String, String> importedApp : imports) {

            String importName = importedApp.get("application");
            String importVersion = importedApp.get("version");
            String importEnvironment = importedApp.get("environment");

            ApplicationConfiguration importConfig = loadApplicationConfiguration(importName, importVersion, importEnvironment, wrapper, depth - 1);

            retBuilder.importApp(importConfig);
        }

        //get the documents
        List<Map<String, String>> documents = (List) appData.get("documents");

        if (documents == null) {
            documents = Collections.emptyList();
        }

        for (Map<String, String> document : documents) {
            String key = document.get("key");
            String type = document.get("type");

            retBuilder.document(new ApplicationDocument(type, key));
        }

        return retBuilder.build();
    }

    private Invocation.Builder applyHeaders(Invocation.Builder builder, ServiceConfiguration config) {

        logger.debug("Called apply headers, configured headers are: {}", config.getCustomHeaders());

        if (config.getCustomHeaders() == null) {
            return builder;
        }

        for(Map.Entry<String, String> header : config.getCustomHeaders().entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        return builder;
    }

}
