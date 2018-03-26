package org.mule.modules.caas.cli.restore;

import org.apache.commons.cli.Option;
import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.spi.TaskProvider;

public class RestoreProvider implements TaskProvider {
    @Override
    public Option buildCommandLineOption() {
        return Option.builder("restore")
                .required(false)
                .hasArg()
                .argName("location")
                .optionalArg(true)
                .desc("Restore a backup to the service. Optional param, the location where the backup will be read")
                .build();
    }

    @Override
    public CommandLineTask buildTask() {
        return new RestoreTask();
    }
}
