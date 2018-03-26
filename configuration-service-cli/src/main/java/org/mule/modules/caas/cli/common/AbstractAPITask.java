package org.mule.modules.caas.cli.common;

import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ServiceConfigurationAdapter;
import org.mule.modules.caas.client.ClientUtils;
import org.mule.modules.caas.client.EncryptionDataWrapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Map;

public abstract class AbstractAPITask implements CommandLineTask {

    protected final Client buildClient(CliConfig config) {
        return ClientUtils.buildRestClient(ServiceConfigurationAdapter.get(config));
    }

    protected Map<String, String> retrieveEncryptionSettings(CliConfig config, Client restClient) {
        String adminPath = ClientUtils.buildAdminBaseUrl(config.getServiceUrl());

        WebTarget target = restClient.target(adminPath)
                .path("security")
                .path("wrappedKey");

        Map<String, String> encKeyData = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get(Map.class);

        return encKeyData;
    }

    protected Key retrieveRemoteKey(Map<String, String> encKeyData, CliConfig config) throws IOException, GeneralSecurityException {
        return EncryptionDataWrapper.builder()
                .withServiceConfiguration(ServiceConfigurationAdapter.get(config))
                .withWrappedKeyData(encKeyData)
                .buildWrapperKey();
    }

}
