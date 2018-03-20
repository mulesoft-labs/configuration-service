package org.mule.modules.caas.model;

import java.io.InputStream;
import java.util.Map;

/**
 * Configuration data on the client side is read only.
 */
public interface ConfigurationDataWrapper {

    /**
     * Perform a modification on a particular key.
     * @param key
     * @return
     */
    String wrapKey(String key);

    /**
     * Perform a modification on a particular value.
     * @param value
     * @return
     */
    String wrapValue(String value);

    /**
     * Wrap an input stream with another input stream that modifies its information.
     * @param is
     * @return
     */
    InputStream wrapStream(InputStream is);


    /**
     * Wrap an entire set of properties with a brand new one.
     * @param properties
     * @return
     */
    Map<String, String> wrapProperties(Map<String, String> properties);

}
