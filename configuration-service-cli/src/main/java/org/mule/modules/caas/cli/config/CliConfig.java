package org.mule.modules.caas.cli.config;

import java.io.Serializable;

/**
 * Pojo representing the configuration of this CLI tool.
 * We are not going to take many command line arguments as the service typically will take
 * a lot of parameters. Instead, we're going to use a yaml config file that we will auto-generate
 * populated with dummy values at the user's command.
 */
public class CliConfig implements Serializable {

    public static CliConfigBuilder builder() {
        return new CliConfigBuilder();
    }

    private String serviceUrl;

    private String backupsDirectory;

    private KeyStoreConfig clientKeyStore;

    private KeyStoreConfig clientTrustStore;

    private boolean disableSSLHostnameVerification;

    private boolean encryptionEnabled;

    private KeyStoreConfig clientEncryptionKeyStore;

    private KeyStoreKey wrapKey;

    private KeyStoreKey macKey;

    private Timeout jobTimeout;

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getBackupsDirectory() {
        return backupsDirectory;
    }

    public void setBackupsDirectory(String backupsDirectory) {
        this.backupsDirectory = backupsDirectory;
    }

    public KeyStoreConfig getClientKeyStore() {
        return clientKeyStore;
    }

    public void setClientKeyStore(KeyStoreConfig clientKeyStore) {
        this.clientKeyStore = clientKeyStore;
    }

    public KeyStoreConfig getClientTrustStore() {
        return clientTrustStore;
    }

    public void setClientTrustStore(KeyStoreConfig clientTrustStore) {
        this.clientTrustStore = clientTrustStore;
    }

    public boolean isDisableSSLHostnameVerification() {
        return disableSSLHostnameVerification;
    }

    public void setDisableSSLHostnameVerification(boolean disableSSLHostnameVerification) {
        this.disableSSLHostnameVerification = disableSSLHostnameVerification;
    }

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public KeyStoreConfig getClientEncryptionKeyStore() {
        return clientEncryptionKeyStore;
    }

    public void setClientEncryptionKeyStore(KeyStoreConfig clientEncryptionKeyStore) {
        this.clientEncryptionKeyStore = clientEncryptionKeyStore;
    }

    public KeyStoreKey getWrapKey() {
        return wrapKey;
    }

    public void setWrapKey(KeyStoreKey wrapKey) {
        this.wrapKey = wrapKey;
    }

    public KeyStoreKey getMacKey() {
        return macKey;
    }

    public void setMacKey(KeyStoreKey macKey) {
        this.macKey = macKey;
    }

    public Timeout getJobTimeout() {
        return jobTimeout;
    }

    public void setJobTimeout(Timeout jobTimeout) {
        this.jobTimeout = jobTimeout;
    }
}
