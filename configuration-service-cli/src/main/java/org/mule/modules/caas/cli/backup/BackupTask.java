package org.mule.modules.caas.cli.backup;

import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.common.ApiCallValidator;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ConfigurationValidator;
import org.slf4j.Logger;

import java.util.Optional;

public class BackupTask implements CommandLineTask {
    @Override
    public boolean runTask(CliConfig config, Logger outputLogger, String... taskArguments) {

        outputLogger.info("Call backup");


        return false;
    }

    @Override
    public Optional<ConfigurationValidator> validator(String... taskArguments) {
        return Optional.of(new ApiCallValidator());
    }
}
