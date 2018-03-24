package org.mule.modules.caas.cli.config;

import org.slf4j.Logger;

/**
 * Represents the validation action for a particular config.
 * This is useful because we may have different requirements for specific
 * actions on the CLI.
 */
public interface ConfigurationValidator {

    boolean isValid(CliConfig config, Logger loggerToUse);

}
