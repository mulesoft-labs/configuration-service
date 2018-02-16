package org.mule.modules.caas;

import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationDocument;

import java.io.InputStream;
import java.util.Map;

/**
 * Abstracts access to Rest API
 */
public interface ApplicationDataProvider {

    public Map<String, Object> loadApplication(String name, String version, String environment) throws ConfigurationNotFoundException;

    public InputStream loadDocument(ApplicationDocument documentName, ApplicationConfiguration app) throws ConfigurationNotFoundException;

}
