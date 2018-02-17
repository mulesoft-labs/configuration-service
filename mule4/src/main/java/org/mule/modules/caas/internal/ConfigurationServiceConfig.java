package org.mule.modules.caas.internal;

import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@Configuration
@Operations(ReadDocumentOperation.class)
public class ConfigurationServiceConfig {

    @Parameter
    private String serviceUrl;
    @Parameter
    private String application;
    @Parameter
    private String version;
    @Parameter
    private String environment;

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

}
