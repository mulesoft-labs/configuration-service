package org.mule.modules.caas.internal;

import org.mule.modules.caas.ServiceConfiguration;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.util.Map;

@Configuration
public class ConfigurationServiceConfig implements ServiceConfiguration {

    @Parameter
    @Optional(defaultValue = "http://localhost:8080/api/configuration")
    @Placement(order = 1)
    private String serviceUrl;
    @Parameter
    @Placement(order = 2)
    private String application;
    @Parameter
    @Placement(order = 3)
    private String version;
    @Parameter
    @Placement(order = 4)
    private String environment;

    @Parameter
    @Optional(defaultValue = "")
    @Placement(order = 5)
    private String localEnvironmentName;

    @Parameter
    @Optional
    @Placement(tab = Placement.SECURITY_TAB, order = 1)
    private String trustStore;

    /**
     * The password of the trust store.
     */
    @Parameter
    @Password
    @Optional
    @Placement(tab = Placement.SECURITY_TAB, order = 2)
    private String trustStorePassword;

    /**
     * The location of the keystore either in the classpath or in the filesystem.
     */
    @Parameter
    @Optional
    @Placement(tab = Placement.SECURITY_TAB, order = 3)
    private String keyStore;

    /**
     * The password of the keystore
     */
    @Parameter
    @Password
    @Optional
    @Placement(tab = Placement.SECURITY_TAB, order = 4)
    private String keyStorePassword;

    @Parameter
    @Optional(defaultValue = "false")
    @Placement(tab = Placement.SECURITY_TAB, order = 5)
    private boolean disableHostNameVerification;


    // @Parameter
    @Optional(defaultValue = "false")
    @Placement(tab = "Encryption", order = 1)
    private boolean enableClientDecryption;

    //@Parameter
    @Optional
    @Placement(tab = "Encryption", order = 2)
    private String clientDecryptionKeyStore;

    //@Parameter
    @Optional
    @Password
    @Placement(tab = "Encryption", order = 3)
    private String clientDecryptionKeyStorePassword;

    //@Parameter
    @Optional
    @Placement(tab = "Encryption", order = 4)
    private String macKeyAlias;

    //@Parameter
    @Optional
    @Password
    @Placement(tab = "Encryption", order = 5)
    private String macKeyPassword;

    //@Parameter
    @Optional
    @Placement(tab = "Encryption", order = 6)
    private String wrapKeyAlias;

    //@Parameter
    @Optional
    @Password
    @Placement(tab = "Encryption", order = 7)
    private String wrapKeyPassword;

    @Parameter
    @Optional
    @Placement(order = 6)
    private Map<String, String> customHeaders;

    @Parameter
    @Optional(defaultValue = "AES")
    @Placement(tab = "SecureProperties", order = 1)
    private EncryptionAlgorithm encryptionAlgorithm;

    @Parameter
    @Optional(defaultValue = "CBC")
    @Placement(tab = "SecureProperties", order = 2)
    private EncryptionMode encryptionMode;

    @Parameter
    @Placement(tab = "SecureProperties", order = 3)
    @Optional
    @Password
    private String key;

    private SecurePropertyPlaceholderModule securePropertyPlaceholderModule;

    public SecurePropertyPlaceholderModule getSecurePropertyPlaceholderModule() {
        return securePropertyPlaceholderModule;
    }

    public void setSecurePropertyPlaceholderModule(SecurePropertyPlaceholderModule securePropertyPlaceholderModule) {
        this.securePropertyPlaceholderModule = securePropertyPlaceholderModule;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public boolean isDisableHostNameVerification() {
        return disableHostNameVerification;
    }

    @Override
    public boolean isEnableClientDecryption() {
        return enableClientDecryption;
    }

    public void setEnableClientDecryption(boolean enableClientDecryption) {
        this.enableClientDecryption = enableClientDecryption;
    }

    @Override
    public String getClientDecryptionKeyStore() {
        return clientDecryptionKeyStore;
    }

    public void setClientDecryptionKeyStore(String clientDecryptionKeyStore) {
        this.clientDecryptionKeyStore = clientDecryptionKeyStore;
    }

    @Override
    public String getClientDecryptionKeyStorePassword() {
        return clientDecryptionKeyStorePassword;
    }

    public void setClientDecryptionKeyStorePassword(String clientDecryptionKeyStorePassword) {
        this.clientDecryptionKeyStorePassword = clientDecryptionKeyStorePassword;
    }

    @Override
    public String getMacKeyAlias() {
        return macKeyAlias;
    }

    public void setMacKeyAlias(String macKeyAlias) {
        this.macKeyAlias = macKeyAlias;
    }

    @Override
    public String getMacKeyPassword() {
        return macKeyPassword;
    }

    public void setMacKeyPassword(String macKeyPassword) {
        this.macKeyPassword = macKeyPassword;
    }

    @Override
    public String getWrapKeyAlias() {
        return wrapKeyAlias;
    }

    public void setWrapKeyAlias(String wrapKeyAlias) {
        this.wrapKeyAlias = wrapKeyAlias;
    }

    @Override
    public String getWrapKeyPassword() {
        return wrapKeyPassword;
    }

    public void setWrapKeyPassword(String wrapKeyPassword) {
        this.wrapKeyPassword = wrapKeyPassword;
    }

    public void setDisableHostNameVerification(boolean disableHostNameVerification) {
        this.disableHostNameVerification = disableHostNameVerification;
    }

    public String getLocalEnvironmentName() {
        return localEnvironmentName;
    }

    public void setLocalEnvironmentName(String localEnvironmentName) {
        this.localEnvironmentName = localEnvironmentName;
    }

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public EncryptionMode getEncryptionMode() {
        return encryptionMode;
    }

    public void setEncryptionMode(EncryptionMode encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

}
