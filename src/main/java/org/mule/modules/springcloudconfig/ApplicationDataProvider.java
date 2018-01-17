package org.mule.modules.springcloudconfig;

import org.mule.modules.springcloudconfig.model.ApplicationConfiguration;
import org.mule.modules.springcloudconfig.model.ApplicationDocument;

import java.io.InputStream;
import java.util.Map;

/**
 * Abstracts access to Rest API
 */
public interface ApplicationDataProvider {

    public Map<String, Object> loadApplication(String name, String version, String environment) throws ConfigurationNotFoundException;

    public InputStream loadDocument(ApplicationDocument documentName, ApplicationConfiguration app) throws ConfigurationNotFoundException;

}
