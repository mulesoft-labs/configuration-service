package org.mule.modules.caas.cli.config;

import java.io.Serializable;

/**
 * Pojo representing general information of an encryption key.
 */
public class KeyStoreKey implements Serializable {

    private String alias;

    private String password;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
