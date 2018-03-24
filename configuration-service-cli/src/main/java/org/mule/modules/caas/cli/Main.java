package org.mule.modules.caas.cli;

import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.CliConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception{

        logger.debug("CaaS Command Line...");

        final Logger outputLogger = LoggerFactory.getLogger("output");

        //present command line options.
        CommandLineOptions options = new CommandLineOptions(args);

        logger.debug("Parsing command line arguments...");
        Map<CommandLineTask, String[]> taskMap = options.parseTasks();

        logger.debug("Reading config file...");

        CliConfigBuilder configBuilder = CliConfig.builder()
                .withConfigFile("settings.yaml")
                .withOutputLogger(outputLogger)
                .applyDefaults();

        configBuilder.withValidator((config, logger) -> config.getJobTimeout() != null);

        taskMap.entrySet().stream()
                .forEach( e -> configBuilder.withValidator(e.getKey().validator(e.getValue()).orElse(null)));

        //read the config.
        CliConfig config = configBuilder.build().orElseThrow(() -> new RuntimeException("Could not load config!"));

        JobScheduler js = new JobScheduler(outputLogger, config, taskMap);
        js.execute();

    }
}
