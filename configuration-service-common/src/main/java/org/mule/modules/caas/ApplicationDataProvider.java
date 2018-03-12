package org.mule.modules.caas;

import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationDocument;

import java.io.InputStream;
import java.util.Map;

/**
 * Abstracts access to Rest API
 */
public interface ApplicationDataProvider {

    /**
     * Convenience instance to improve readability of the code.
     */
    static ApplicationDataProviderFactory factory = new ApplicationDataProviderFactory();

    /**
     * Load the low level information of an application.
     * @param name
     * @param version
     * @param environment
     * @return
     * @throws ConfigurationNotFoundException
     */
    Map<String, Object> loadApplication(String name, String version, String environment) throws ConfigurationNotFoundException;

    /**
     * Load a document from the configuration service.
     * @param documentName
     * @param app
     * @return
     * @throws ConfigurationNotFoundException
     */
    InputStream loadDocument(ApplicationDocument documentName, ApplicationConfiguration app) throws ConfigurationNotFoundException;

    /**
     * Load the model for application configuration.
     * @param name
     * @param version
     * @param environment
     * @return
     * @throws ConfigurationServiceException
     */
    ApplicationConfiguration loadApplicationConfiguration(String name, String version, String environment) throws ConfigurationServiceException;
}
