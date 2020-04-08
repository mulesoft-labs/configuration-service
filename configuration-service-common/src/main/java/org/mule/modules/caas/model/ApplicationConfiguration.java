package org.mule.modules.caas.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConfiguration implements Serializable {

    private final String name;
    private final String version;
    private final String environment;
    private final Map<String, String> properties;
    private final List<ApplicationConfiguration> imports;
    private final List<ApplicationDocument> documents;
    private final ConfigurationDataWrapper dataWrapper;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    public static ApplicationConfigurationBuilder builder() {
        return new ApplicationConfigurationBuilder();
    }

    ApplicationConfiguration(String name, String version, String environment, Map<String, String> properties, List<ApplicationConfiguration> imports, List<ApplicationDocument> documents, ConfigurationDataWrapper dataWrapper) {
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.properties = properties;
        this.imports = imports;
        this.documents = documents;
        this.dataWrapper = dataWrapper;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getEnvironment() {
        return environment;
    }

    public Map<String, String> getProperties() {

        Map<String, String> properties = this.properties;

        if (dataWrapper != null) {
            return dataWrapper.wrapProperties(properties);
        }

        return properties;
    }

    public List<ApplicationConfiguration> getImports() {
        return imports;
    }

    public List<ApplicationDocument> getDocuments() {
        return documents;
    }

    public String readProperty(String key) {

        logger.debug("Call to read key {}", key);

        String prop = null;

        String wrappedKey = findWrappedKey(key);

        //try with this one
        prop = properties.get(wrappedKey);

        if (prop != null) {
            if (dataWrapper != null) {
                prop = dataWrapper.wrapValue(prop);
            }
            return prop;
        }

        //if the property is not here, may be in one of the imports.
        //this method will navigate all the hierarchy looking for properties.
        for (ApplicationConfiguration importedApp : imports) {
            prop = importedApp.readProperty(key);
            if (prop != null) {
                return prop;
            }
        }
        return null;

    }

    public ConfigurationDataWrapper getDataWrapper() {
        return dataWrapper;
    }

    public Map<String, Object> findAppConfigAndDocument(String documentName, ApplicationConfiguration applicationConfiguration) {
        {

            for (ApplicationDocument doc : applicationConfiguration.getDocuments()) {
                if (StringUtils.equals(documentName, doc.getName())) {
                    if (doc.getName() == null || doc.getContentType() == null) {
                        return null;
                    }
                    Map<String, Object> answer = new HashMap<>();
                    answer.put("applicationConfiguration", applicationConfiguration);
                    answer.put("document", doc);
                    return answer;

                }

            }

            //or else, return from any of the imports.
            for (ApplicationConfiguration importedApp : applicationConfiguration.getImports()) {
                Map<String, Object> doc = importedApp.findAppConfigAndDocument(documentName, importedApp);
                if (doc != null) {
                    return doc;
                }
            }

            return null;
        }

    }


    public ApplicationDocument findDocument(String name) {

        for (ApplicationDocument doc : documents) {
            if (StringUtils.equals(name, doc.getName())) {
                return doc;
            }
        }

        //or else, return from any of the imports.
        for (ApplicationConfiguration imporedApp : imports) {
            ApplicationDocument doc = imporedApp.findDocument(name);
            if (doc != null) {
                return doc;
            }
        }

        return null;
    }

    private String findWrappedKey(String plainKey) {

        if (dataWrapper == null) {
            return plainKey;
        }

        logger.debug("Invoking data wrapper to unwrap existing keys...");

        for (String wrappedKey : properties.keySet()) {
            if (dataWrapper.wrapKey(wrappedKey).equals(plainKey)) {
                return wrappedKey;
            }
        }

        logger.debug("key not found, resorting to unwrapped key...");

        return plainKey;
    }

}
