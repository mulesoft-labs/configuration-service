package org.mule.modules.caas.cli.keys;

import org.apache.commons.lang3.ArrayUtils;
import org.mule.modules.caas.cli.CommandLineTask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.utils.CliUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

public class GenerateKeysTask implements CommandLineTask {

    private static final Logger logger = LoggerFactory.getLogger(GenerateKeysTask.class);


    @Override
    public boolean runTask(CliConfig config, Logger outputLogger, String... taskArguments) {

        logger.debug("Invoking generate keys...");

        try {

            //check if JCE is present.
            if (!isJcePresent()) {
                outputLogger.error("JCE Extension is not installed on JDK, unable to generate strong encryption Keys!");
                return false;
            }


            //generate the 3 keys.
            outputLogger.info("Generating Encryption key...");
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(256);;
            Key encKey = gen.generateKey();

            outputLogger.info("Generating Wrapping key...");
            gen = KeyGenerator.getInstance("Blowfish");
            gen.init(256);
            Key wrapKey = gen.generateKey();

            outputLogger.info("Generating MacKey...");
            gen = KeyGenerator.getInstance("HMacSHA256");
            gen.init(256);
            Key macKey = gen.generateKey();

            char[] kspw = populateKeyStorePassword(outputLogger, taskArguments);
            char[] encKeyPassword = kspw;
            char[] wrapKeyPassword = kspw;
            char[] macKeyPassword = kspw;

            if (shouldPrompt(taskArguments)) {
                encKeyPassword = CliUtils.readPassword("Please enter the encryption key password, leave blank for same as keystore", outputLogger, 4, true);
                if (encKeyPassword == null) encKeyPassword = kspw;

                wrapKeyPassword = CliUtils.readPassword("Please enter the wrapping key password, leave blank for same as keystore", outputLogger, 4, true);
                if (wrapKeyPassword == null) wrapKeyPassword = kspw;

                macKeyPassword = CliUtils.readPassword("Please enter the mac key password, leave blank for same as keystore", outputLogger, 4, true);
                if (macKeyPassword == null) macKeyPassword = kspw;
            }


            //generate the server keystore
            KeyStore serverStore = KeyStore.getInstance("JCEKS");
            KeyStore clientStore = KeyStore.getInstance("JCEKS");

            //load the keystore
            serverStore.load(null, null);
            clientStore.load(null, null);

            //store the 3 keys
            serverStore.setKeyEntry("enc-key", encKey, encKeyPassword, null);
            serverStore.setKeyEntry("wrap-key", wrapKey, wrapKeyPassword, null);
            serverStore.setKeyEntry("mac-key", macKey, macKeyPassword, null);
            clientStore.setKeyEntry("wrap-key", wrapKey, wrapKeyPassword, null);
            clientStore.setKeyEntry("mac-key", macKey, macKeyPassword, null);


            outputLogger.info("Writing server.jceks...");
            serverStore.store(new FileOutputStream("server.jceks"), kspw);
            outputLogger.info("Writing client.jceks...");
            clientStore.store(new FileOutputStream("client.jceks"), kspw);

        } catch (Exception ex) {
            logger.error("Could not generate encryption keys...", ex);
            outputLogger.error("Could not generate keys: {}", ex.getMessage());
            return false;
        }

        return true;
    }

    private char[] populateKeyStorePassword(Logger outputLogger, String[] taskArguments) throws Exception {

        if (ArrayUtils.getLength(taskArguments) != 0) {
            return taskArguments[0].toCharArray();
        }

        //otherwise prompt
        return CliUtils.readPassword("Please enter KeyStore Password:", outputLogger, 4);
    }


    private boolean shouldPrompt(String[] taskArguments) {
        return ArrayUtils.getLength(taskArguments) == 0;
    }

    private boolean isJcePresent() throws NoSuchAlgorithmException {
        return Cipher.getMaxAllowedKeyLength("AES") > 128;
    }

}
