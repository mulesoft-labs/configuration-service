package org.mule.modules.caas.cli;

import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ConfigurationValidator;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Represents an action to be taken by the command line.
 */
public interface CommandLineTask {

    /**
     * Represents the overall process of the task.
     * @param config the configuration of the command line.
     * @param taskArguments the arguments to execute the task.
     * @return true if the task has completed successfully.
     */
    boolean runTask(CliConfig config, Logger outputLogger, String... taskArguments);


    /**
     * Build a specific validator based on the specific arguments supplied by the user.
     * @param taskArguments
     * @return
     */
    default Optional<ConfigurationValidator> validator(String... taskArguments) {
        return Optional.empty();
    };
}
