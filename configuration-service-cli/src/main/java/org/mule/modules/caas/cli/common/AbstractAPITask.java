package org.mule.modules.caas.cli.common;

import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ServiceConfigurationAdapter;
import org.mule.modules.caas.client.ClientUtils;

import javax.ws.rs.client.Client;

public abstract class AbstractAPITask implements CommandLineTask{

    protected final Client buildClient(CliConfig config) {
        return ClientUtils.buildRestClient(ServiceConfigurationAdapter.get(config));
    }

}
