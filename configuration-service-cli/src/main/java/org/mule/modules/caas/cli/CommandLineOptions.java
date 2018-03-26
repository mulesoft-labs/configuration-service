package org.mule.modules.caas.cli;

import org.apache.commons.cli.*;
import org.mule.modules.caas.cli.spi.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CommandLineOptions {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineOptions.class);

    private final String[] args;

    public CommandLineOptions(String[] args) {
        this.args = args;
    }

    public Map<CommandLineTask, String[]> parseTasks() {

        Options options = new Options();

        Option help = Option.builder("h")
                .desc("Prints the help")
                .longOpt("help")
                .build();

        options.addOption(help);

        List<TaskProvider> providers = findProviders();
        Map<String, TaskProvider> providersMap = new HashMap<>();

        for(TaskProvider provider : providers) {
            Option o = provider.buildCommandLineOption();
            options.addOption(o);
            providersMap.put(o.getOpt(), provider);
        }

        LinkedHashMap ret = new LinkedHashMap();
        String[] emptyArgs = new String[0];

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine result = parser.parse(options, args);

            for(Option opt :result.getOptions()) {
                TaskProvider tp = providersMap.get(opt.getOpt());
                String[] optValues = opt.getValues();
                if (optValues == null) {
                    optValues = new String[0];
                }

                ret.put(tp.buildTask(), optValues);
            }

            if (result.getOptions().length == 0 || result.hasOption("h")) {
                ret.put((CommandLineTask)(config, logger, arguments) -> {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("cmd1 {aruments} cmd2 {arguments} cmd3..."
                            ,"Commands are:"
                            , options
                            , "");
                    return true;
                }, emptyArgs);
            }

            return ret;
        } catch (ParseException ex) {
            logger.error("Could not parse command line options {}...", Arrays.toString(args));
            return new LinkedHashMap<>();
        }
    }

    private List<TaskProvider> findProviders() {

        //go to SPI and find all available implementations.
        ServiceLoader<TaskProvider> providers = ServiceLoader.load(TaskProvider.class);

        List<TaskProvider> ret = new LinkedList<>();
        providers.forEach(ret::add);

        return ret;
    }

}
