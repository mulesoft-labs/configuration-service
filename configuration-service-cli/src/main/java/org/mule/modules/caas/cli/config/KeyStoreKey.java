package org.mule.modules.caas.cli.config;

import java.io.Serializable;

/**
 * Pojo representing general information of an encryption key.
 */
public class KeyStoreKey implements Serializable {

    private String alias;

    private char[] password;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
