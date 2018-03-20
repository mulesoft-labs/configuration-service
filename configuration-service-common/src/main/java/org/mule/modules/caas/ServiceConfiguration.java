package org.mule.modules.caas;

/**
 * Provides an abstraction of application configuration
 */
public interface ServiceConfiguration {

    String getServiceUrl();

    String getApplication();

    String getVersion();

    String getEnvironment();

    String getLocalEnvironmentName();

    String getTrustStore();

    String getTrustStorePassword();

    String getKeyStore();

    String getKeyStorePassword();

    boolean isDisableHostNameVerification();

    boolean isEnableClientDecryption();

    String getClientDecryptionKeyStore();

    String getClientDecryptionKeyStorePassword();

    String getMacKeyAlias();

    String getMacKeyPassword();

    String getWrapKeyAlias();

    String getWrapKeyPassword();

}
