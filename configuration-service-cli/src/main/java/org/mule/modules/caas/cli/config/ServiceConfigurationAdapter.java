package org.mule.modules.caas.cli.config;

import org.mule.modules.caas.ServiceConfiguration;

/**
 * Utility class so we can use the libraries present in the main connector that takes service configuration.
 * as a parameter.
 */
public class ServiceConfigurationAdapter implements ServiceConfiguration {

    private final CliConfig delegate;

    public ServiceConfigurationAdapter get(CliConfig config) {
        return new ServiceConfigurationAdapter(config);
    }

    private ServiceConfigurationAdapter(CliConfig config) {
        this.delegate = config;
    }

    @Override
    public String getServiceUrl() {
        return delegate.getServiceUrl();
    }

    @Override
    public String getApplication() {
        return "cli";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getEnvironment() {
        return "dummy";
    }

    @Override
    public String getLocalEnvironmentName() {
        return "local";
    }

    @Override
    public String getTrustStore() {

        if (delegate.getClientTrustStore() == null) {
            return null;
        }

        return delegate.getClientTrustStore().getLocation();
    }

    @Override
    public String getTrustStorePassword() {

        if (delegate.getClientTrustStore() == null) {
            return null;
        }

        return new String(delegate.getClientTrustStore().getPassword());
    }

    @Override
    public String getKeyStore() {

        if (delegate.getClientKeyStore() == null) {
            return null;
        }


        return delegate.getClientKeyStore().getLocation();
    }

    @Override
    public String getKeyStorePassword() {

        if (delegate.getClientKeyStore() == null) {
            return null;
        }

        return new String(delegate.getClientKeyStore().getPassword());
    }

    @Override
    public boolean isDisableHostNameVerification() {
        return delegate.isDisableSSLHostnameVerification();
    }

    @Override
    public boolean isEnableClientDecryption() {
        return delegate.isEncryptionEnabled();
    }

    @Override
    public String getClientDecryptionKeyStore() {

        if (delegate.getClientEncryptionKeyStore() == null) {
            return null;
        }

        return delegate.getClientEncryptionKeyStore().getLocation();
    }

    @Override
    public String getClientDecryptionKeyStorePassword() {
        if (delegate.getClientEncryptionKeyStore() == null) {
            return null;
        }

        return new String(delegate.getClientEncryptionKeyStore().getPassword());
    }

    @Override
    public String getMacKeyAlias() {

        if (delegate.getMacKey() == null) {
            return null;
        }

        return delegate.getMacKey().getAlias();
    }

    @Override
    public String getMacKeyPassword() {

        if (delegate.getMacKey() == null) {
            return null;
        }

        return new String(delegate.getMacKey().getPassword());
    }

    @Override
    public String getWrapKeyAlias() {
        if (delegate.getWrapKey() == null) {
            return null;
        }

        return delegate.getWrapKey().getAlias();
    }

    @Override
    public String getWrapKeyPassword() {
        if (delegate.getWrapKey() == null) {
            return null;
        }

        return new String(delegate.getWrapKey().getPassword());
    }
}
