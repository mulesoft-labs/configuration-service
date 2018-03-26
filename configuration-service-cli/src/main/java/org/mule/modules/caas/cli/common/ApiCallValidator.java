package org.mule.modules.caas.cli.common;

import org.apache.commons.lang3.StringUtils;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ConfigurationValidator;
import org.slf4j.Logger;

public class ApiCallValidator implements ConfigurationValidator {
    @Override
    public boolean isValid(CliConfig config, Logger loggerToUse) {

        if (StringUtils.isEmpty(config.getServiceUrl())) {
            loggerToUse.error("Configuration does not contain configuration service URL.");
            return false;
        }

        return true;
    }
}
