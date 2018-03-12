package org.mule.modules.caas.internal;

import org.mule.modules.caas.ServiceConfiguration;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

@Configuration
@Operations(ReadDocumentOperation.class)
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

    public void setDisableHostNameVerification(boolean disableHostNameVerification) {
        this.disableHostNameVerification = disableHostNameVerification;
    }

    public String getLocalEnvironmentName() {
        return localEnvironmentName;
    }

    public void setLocalEnvironmentName(String localEnvironmentName) {
        this.localEnvironmentName = localEnvironmentName;
    }
}
