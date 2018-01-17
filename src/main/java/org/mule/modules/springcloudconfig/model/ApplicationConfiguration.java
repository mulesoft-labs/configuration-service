package org.mule.modules.springcloudconfig.model;

import org.mule.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ApplicationConfiguration implements Serializable {

    private final String name;
    private final String version;
    private final String environment;
    private final Map<String, String> properties;
    private final List<ApplicationConfiguration> parents;
    private final List<ApplicationDocument> documents;

    public static ApplicationConfigurationBuilder builder() {
        return new ApplicationConfigurationBuilder();
    }

    ApplicationConfiguration(String name, String version, String environment, Map<String, String> properties, List<ApplicationConfiguration> parents, List<ApplicationDocument> documents) {
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.properties = properties;
        this.parents = parents;
        this.documents = documents;
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
        return properties;
    }

    public List<ApplicationConfiguration> getParents() {
        return parents;
    }

    public List<ApplicationDocument> getDocuments() {
        return documents;
    }

    public String readProperty(String key) {

        //try with this one
        String prop = properties.get(key);

        if (prop != null) {
            return prop;
        }

        //if the property is not here, may be in one of the parents.
        //this method will navigate all the hierarchy looking for properties.
        for (ApplicationConfiguration parent : parents) {
            prop = parent.readProperty(key);
            if (prop != null) {
                return prop;
            }
        }
        return null;
    }

    public ApplicationDocument findDocument(String name) {

        for (ApplicationDocument doc : documents) {
            if (StringUtils.equals(name, doc.getName())) {
                return doc;
            }
        }

        //or else, return from any of the parents.
        for (ApplicationConfiguration parent : parents) {
            ApplicationDocument doc = parent.findDocument(name);
            if (doc != null) {
                return doc;
            }
        }

        return null;
    }

}
