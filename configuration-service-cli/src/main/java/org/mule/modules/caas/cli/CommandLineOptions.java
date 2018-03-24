package org.mule.modules.caas.cli;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CommandLineOptions {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineOptions.class);

    private final String[] args;

    public CommandLineOptions(String[] args) {
        this.args = args;
    }

    public Map<CommandLineTask, String[]> parseTasks() {

        Options options = new Options();

        Option help = Option.builder()
                .argName("h")
                .desc("Prints the help")
                .longOpt("help")
                .build();

        options.addOption(help);

        LinkedHashMap ret = new LinkedHashMap();
        String[] emptyArgs = new String[0];

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine result = parser.parse(options, args);

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

}
