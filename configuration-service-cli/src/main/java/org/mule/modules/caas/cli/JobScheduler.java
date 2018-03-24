package org.mule.modules.caas.cli;

import org.mule.modules.caas.cli.config.CliConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class JobScheduler {

    public static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final Logger outputLogger;

    private final CliConfig config;

    private final Map<CommandLineTask, String[]> taskMap;

    public JobScheduler(Logger outputLogger, CliConfig config, Map<CommandLineTask, String[]> taskMap) {
        this.outputLogger = outputLogger;
        this.config = config;
        this.taskMap = taskMap;
    }

    public void execute() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        logger.debug("Starting executor for command line tasks...");
        service.execute(() -> {
            Thread.currentThread().setName("caas-cli-worker");
            boolean result = true;

            try {

                for (Map.Entry<CommandLineTask, String[]> taskEntry : taskMap.entrySet()) {
                    result = taskEntry.getKey().runTask(config, outputLogger, taskEntry.getValue());
                    if (!result) {
                        outputLogger.error("Task step failed");
                        break;
                    }
                }

                if (result) {
                    outputLogger.info("Successfully completed all the tasks.");
                }

            } catch (Throwable t) {
                logger.error("Could not complete tasks: ", t);
            }

        });

        service.shutdown();
        boolean result = service.awaitTermination(config.getJobTimeout().getDuration(), config.getJobTimeout().getUnit());
        if (result) {
            logger.info("Service has completed execution");
        } else {
            logger.info("Service terminated before concluding...");
        }
    }
}
