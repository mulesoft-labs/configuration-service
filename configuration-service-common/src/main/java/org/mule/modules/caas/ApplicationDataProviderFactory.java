package org.mule.modules.caas;

import org.apache.commons.lang3.StringUtils;
import org.mule.modules.caas.client.ClientUtils;
import org.mule.modules.caas.client.DefaultApplicationDataProvider;
import org.mule.modules.caas.local.LocalApplicationDataProvider;

import javax.ws.rs.client.Client;

public class ApplicationDataProviderFactory {

    public ApplicationDataProvider newApplicationDataProvider(ServiceConfiguration config) {

        if (StringUtils.isNotEmpty(config.getLocalEnvironmentName()) &&
                StringUtils.equals(config.getEnvironment(), config.getLocalEnvironmentName())) {
            return new LocalApplicationDataProvider(config.getLocalEnvironmentName());
        } else {
            Client client = ClientUtils.buildRestClient(config);
            return new DefaultApplicationDataProvider(config.getServiceUrl(), client);
        }

    }

}
