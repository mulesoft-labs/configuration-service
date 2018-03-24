package org.mule.modules.caas.cli.config;

import org.mule.modules.caas.util.ConfigurationServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class CliConfigBuilder {

    /**
     * This logger may not be the one we want.
     */
    private Logger logger = LoggerFactory.getLogger(CliConfigBuilder.class);

    private String configFile;

    private boolean applyDefaults;

    /**
     * Validators should log output explaining what is wrong.
     */
    private List<ConfigurationValidator> validators;

    CliConfigBuilder() {
        validators = new LinkedList<>();
    }

    public CliConfigBuilder withConfigFile(String configFile) {
        this.configFile = configFile;
        return this;
    }

    public CliConfigBuilder withOutputLogger(Logger logger) {
        if (logger != null) {
            this.logger = logger;
        }

        return this;
    }

    public CliConfigBuilder withValidator(ConfigurationValidator validator) {

        if (validator != null) {
            this.validators.add(validator);
        }

        return this;
    }

    public CliConfigBuilder applyDefaults() {
        this.applyDefaults = true;

        return this;
    }

    /**
     * Basically, we won't return invalid configuration. If the user does not provide any
     * validator, we'll return the config model as-is.
     * @return the configuration if valid, and the config file is found.
     */
    public Optional<CliConfig> build() {

        //we load the yaml file
        CliConfig conf = loadYaml();

        if (applyDefaults) {
            setDefaults(conf);
        }

        //go through validators and validate
        boolean valid = validators.stream()
                .map(validator -> validator.isValid(conf, logger))
                .reduce((accum, actual) -> accum && actual)
                .get();

        if (!valid) {
            logger.error("Settings are not valid!");
            return Optional.empty();
        }

        return Optional.of(conf);
    }

    private CliConfig loadYaml() {

        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Constructor constructor = new Constructor(CliConfig.class);
        Yaml yaml = new Yaml(constructor, representer);

        //load the configuration file.
        //the configuration file is in the classpath
        InputStream is = ConfigurationServiceUtil.loadClasspathResource(configFile);

        if (is == null) {
            logger.warn("No configuration file {} found on classpath, conf/, or {}", configFile, System.getProperty("user.dir"));
            return new CliConfig();
        }

        return yaml.loadAs(is, CliConfig.class);
    }

    private void setDefaults(CliConfig config) {
        Timeout t = config.getJobTimeout();

        if (t == null) {
            t = new Timeout();
            config.setJobTimeout(t);
        }

        if (t.getDuration() == 0) {
            t.setDuration(1);
        }

        if (t.getUnit() == null) {
            t.setUnit(TimeUnit.HOURS);
        }

        logger.info("Tool will time out in {} {}", t.getDuration(), t.getUnit().name());
    }

}
