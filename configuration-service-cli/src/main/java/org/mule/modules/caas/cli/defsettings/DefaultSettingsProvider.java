package org.mule.modules.caas.cli.defsettings;

import org.apache.commons.cli.Option;
import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.KeyStoreConfig;
import org.mule.modules.caas.cli.config.KeyStoreKey;
import org.mule.modules.caas.cli.config.Timeout;
import org.mule.modules.caas.cli.spi.TaskProvider;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DefaultSettingsProvider implements TaskProvider {
    @Override
    public Option buildCommandLineOption() {
        return Option.builder("d")
                .longOpt("create-default")
                .required(false)
                .desc("Create a default configuration file with dummy values in the working directory.")
                .build();
    }

    @Override
    public CommandLineTask buildTask() {
        return (config, outputLogger, taskArguments) -> {

            config = new CliConfig();

            outputLogger.info("Building default config file...");

            config.setBackupsDirectory("backups");
            config.setServiceUrl("http://localhost:8081/api/configuration");
            config.setEncryptionEnabled(false);
            config.setDecryptionEnabled(true);

            Timeout to = new Timeout();
            to.setUnit(TimeUnit.HOURS);
            to.setDuration(1);

            config.setJobTimeout(to);

            //we try to add all possible properties, for completeness
            KeyStoreConfig ksc = new KeyStoreConfig();
            ksc.setLocation("somekeystore.jceks");
            ksc.setPassword("sample");
            ksc.setType("JCEKS");

            config.setClientEncryptionKeyStore(ksc);

            ksc = new KeyStoreConfig();
            ksc.setLocation("keystore.jks");
            ksc.setPassword("samplepw");
            ksc.setType("JKS");

            config.setClientKeyStore(ksc);

            ksc = new KeyStoreConfig();
            ksc.setLocation("keystore.jks");
            ksc.setPassword("samplepw");
            ksc.setType("JKS");

            config.setClientTrustStore(ksc);

            config.setDisableSSLHostnameVerification(false);

            KeyStoreKey fakeKey = new KeyStoreKey();
            fakeKey.setAlias("mac-alias");
            fakeKey.setPassword("keyPW");

            config.setMacKey(fakeKey);

            fakeKey = new KeyStoreKey();
            fakeKey.setAlias("wrap-alias");
            fakeKey.setPassword("keyPW");

            config.setWrapKey(fakeKey);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("client_id", "someclientid");
            config.setCustomHeaders(headers);

            try {
                Representer representer = new Representer();
                representer.getPropertyUtils().setSkipMissingProperties(true);
                representer.addClassTag(config.getClass(), Tag.MAP);
                Constructor constructor = new Constructor(CliConfig.class);

                Yaml yaml = new Yaml(constructor, representer);

                FileOutputStream fos = new FileOutputStream("settings.yaml");
                yaml.dump(config, new OutputStreamWriter(fos));

            } catch (IOException ex) {
                outputLogger.error("Could not write config file", ex);
            }

            return true;
        };
    }
}
