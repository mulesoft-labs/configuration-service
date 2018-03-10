package org.mule.modules.caas.client;

import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationNotFoundException;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationConfigurationBuilder;
import org.mule.modules.caas.model.ApplicationDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultApplicationDataProvider implements ApplicationDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationDataProvider.class);


    private final String baseUrl;
    private final Client restClient;

    public DefaultApplicationDataProvider(String baseUrl, Client restClient) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;
    }

    @Override
    public Map<String, Object> loadApplication(String name, String version, String environment) {

        WebTarget target = restClient.target(baseUrl)
                .path(name)
                .path(version)
                .path(environment);

        //read it as a java map
        Map<String, Object> result = target.request().accept(MediaType.APPLICATION_JSON).get(Map.class);

        logger.debug("Got settings from configuration server: {}", result);


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

        return target.request().accept(doc.getContentType()).get(InputStream.class);
    }

    @Override
    public ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment) throws ConfigurationServiceException {
        return loadApplicationConfiguration(name, version, environment, 50);
    }

    private ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment, int depth) throws ConfigurationServiceException {
        ApplicationConfigurationBuilder retBuilder = ApplicationConfiguration.builder()
                .setName(name)
                .setEnvironment(environment)
                .setVersion(version);

        if (depth == 0) {
            logger.warn("Reached depth 0 while recursively load parent configurations. This may indicate a cycle in the parent/child relationship.");
            return retBuilder.build();
        }

        //load from the API
        Map<String, Object> appData = loadApplication(name, version, environment);

        //get the properties
        Map<String, String> properties = (Map) appData.get("properties");
        if (properties == null) {
            properties = Collections.emptyMap();
        }

        retBuilder.setProperties(properties);

        //get the parent apps
        List<Map<String, String>> parents = (List) appData.get("parents");

        if (parents == null) {
            parents = Collections.emptyList();
        }

        //go recursively through the parents to build the list.
        for (Map<String, String> parent : parents) {

            String parentName = parent.get("application");
            String parentVersion = parent.get("version");
            String parentEnvironment = parent.get("environment");

            ApplicationConfiguration parentConfig = loadApplicationConfiguration(parentName, parentVersion, parentEnvironment, depth - 1);

            retBuilder.parent(parentConfig);
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

}
