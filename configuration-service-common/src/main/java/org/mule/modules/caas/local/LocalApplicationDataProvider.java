package org.mule.modules.caas.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.ConfigurationNotFoundException;
import org.mule.modules.caas.ConfigurationServiceException;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationConfigurationBuilder;
import org.mule.modules.caas.model.ApplicationDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mule.modules.caas.util.ConfigurationServiceUtil.*;

/**
 * Provider that loads data from the classpath instead of reaching out to configuration service.
 * Local config file should be called {localEnvName}/config.json and documents will be under {localEnvName}/documents
 * in the classpath. User need to be very intentional about the config json and we will not scan the classpath but blindly
 * try to load documents that are under the documents section of the json. Import configs are not supported.
 */
public class LocalApplicationDataProvider implements ApplicationDataProvider {

    private final String localEnvironmentName;

    private static final Logger logger = LoggerFactory.getLogger(LocalApplicationDataProvider.class);

    public LocalApplicationDataProvider(String localEnvironmentName) {
        this.localEnvironmentName = localEnvironmentName;
    }

    @Override
    public Map<String, Object> loadApplication(String name, String version, String environment) throws ConfigurationNotFoundException {

        logger.info("Loading local enviroment: {}", localEnvironmentName);
        logger.info("Will attempt to load classpathResource {}/config.json", localEnvironmentName);
        logger.info("Documents declared in config.json must be placed in {}/documents/");

        InputStream is = loadClasspathResource(localEnvironmentName + "/config.json");

        if (is == null) {
            throw new RuntimeException("Local environment settings not found!! " +
                    "Please check that " + localEnvironmentName + "/config.json exists in classpath!");
        }

        ObjectMapper om = new ObjectMapper();

        try {
            return om.readValue(is, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream loadDocument(ApplicationDocument documentName, ApplicationConfiguration app) throws ConfigurationNotFoundException {
        logger.info("Attempting to load {}/documents/{}", localEnvironmentName, documentName.getName());
        return loadClasspathResource(localEnvironmentName + "/documents/" + documentName.getName());
    }

    @Override
    public ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment) throws ConfigurationServiceException {

        ApplicationConfigurationBuilder builder = ApplicationConfiguration.builder();

        Map<String, Object> app = loadApplication(name, version, environment);

        builder.setName(name)
                .setVersion(version)
                .setEnvironment(localEnvironmentName)
                .setProperties((Map)app.get("properties"));

        //load the documents
        List<Map<String, String>> documents = (List) app.get("documents");

        if (documents == null) {
            documents = Collections.emptyList();
        }

        for (Map<String, String> document : documents) {
            String key = document.get("key");
            String type = document.get("type");

            builder.document(new ApplicationDocument(type, key));
        }

        if (app.containsKey("imports")) {
            logger.warn("Local environment contains imports definition, which are not supported currently!");
        }

        return builder.build();
    }

}
