package org.mule.modules.caas.cli.config;

import java.io.Serializable;

/**
 * Pojo representing general configuration for a key store.
 */
public class KeyStoreConfig implements Serializable {

    private String location;

    private char[] password;

    private String type;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
