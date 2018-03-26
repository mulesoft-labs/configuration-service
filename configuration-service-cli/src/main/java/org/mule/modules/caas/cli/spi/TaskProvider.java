package org.mule.modules.caas.cli.spi;

import org.apache.commons.cli.Option;
import org.mule.modules.caas.cli.CommandLineTask;

/**
 * SPI to help keep CLI functionality relatively independent and with low coupling to the core.
 */
public interface TaskProvider {

    /**
     * Provide an apache commons CLI option object to contribute to the options list.
     * @return
     */
    Option buildCommandLineOption();


    /**
     * Contribute with a configuration validator that will check on the configuration object only
     * for validity of the settings it is concerned about.
     * @return
     */
    CommandLineTask buildTask();

}
