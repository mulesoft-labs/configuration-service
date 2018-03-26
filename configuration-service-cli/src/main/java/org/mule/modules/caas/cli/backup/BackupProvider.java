package org.mule.modules.caas.cli.backup;

import org.apache.commons.cli.Option;
import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.spi.TaskProvider;

public class BackupProvider implements TaskProvider {
    @Override
    public Option buildCommandLineOption() {
        return Option.builder("backup")
                .required(false)
                .hasArg()
                .argName("location")
                .optionalArg(true)
                .desc("Optional, the location where the backup will be placed")
                .build();
    }

    @Override
    public CommandLineTask buildTask() {
        return new BackupTask();
    }
}
