package org.mule.modules.caas.cli.keys;

import org.apache.commons.cli.Option;
import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.spi.TaskProvider;

public class GenerateKeysProvider implements TaskProvider {
    @Override
    public Option buildCommandLineOption() {
        return Option.builder("k")
                .longOpt("generate-keys")
                .desc("Generate keystores usable both for client and service.")
                .hasArg()
                .argName("keystore-password")
                .optionalArg(true)
                .build();
    }

    @Override
    public CommandLineTask buildTask() {
        return new GenerateKeysTask();
    }
}
