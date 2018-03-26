package org.mule.modules.caas.cli.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.mule.modules.caas.cli.common.AbstractAPITask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ConfigurationValidator;
import org.mule.modules.caas.cli.config.ServiceConfigurationAdapter;
import org.mule.modules.caas.cli.utils.CliUtils;
import org.mule.modules.caas.client.ClientUtils;
import org.mule.modules.caas.client.EncryptionDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackupTask extends AbstractAPITask {

    private static final Logger internalLogger = LoggerFactory.getLogger(BackupTask.class);

    @Override
    public boolean runTask(CliConfig config, Logger outputLogger, String... taskArguments) {

        File backupDir = buildBackupDir(config.getBackupsDirectory(), taskArguments);

        outputLogger.info("Backing up configuration respository to {}", backupDir.getPath());

        if (!createBackupDir(backupDir)) {
            outputLogger.error("Could not create backup dir! {}", backupDir.getPath());
            return false;
        }

        if (backupDir.list().length != 0) {
            outputLogger.error("Backup directory is not empty!");
            return false;
        }

        return doBackup(config, backupDir, outputLogger);

    }

    private boolean createBackupDir(File backupDir) {

        if (backupDir.exists() && backupDir.isDirectory()) {
            return true;
        }

        if (backupDir.exists() && !backupDir.isDirectory()) {
            return false;
        }

        try {
            Files.createDirectories(backupDir.toPath());
            return true;
        } catch (IOException ex){
            internalLogger.error("Could not create directores!!", ex);
            return false;
        }
    }

    private File buildBackupDir(String backupsDirectory, String... commandArgs) {

        String effectiveDir = System.getProperty("app.home");
        String storageFolder = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());

        if (commandArgs.length != 0) {
            effectiveDir = commandArgs[0];
        } else {
            effectiveDir += File.separator + backupsDirectory + File.separator + storageFolder;
        }

        File ret = new File(effectiveDir);

        return ret;
    }

    private boolean doBackup(CliConfig config, File backupDir, Logger logger) {
        try {

            Client restClient = buildClient(config);


            List<Map<String, ?>> apps = retrieveApps(restClient, config, logger);

            for(Map<String, ?> appMap: apps) {
                backupApplication(restClient, config, backupDir, appMap, logger);
            }

            if (config.isEncryptionEnabled()) {
                backupEncryptionKey(restClient, config, backupDir, logger);
            }

            return true;
        } catch (Exception ex) {
            logger.error("IOException while writing backup...", ex);
            return false;
        }
    }

    private void backupApplication(Client restClient, CliConfig config, File backupDir, Map<String, ?> app, Logger logger) throws IOException {

        String name = (String) app.get("application");
        String version = (String ) app.get("version");
        String environment = (String) app.get("environment");


        logger.info("Backing up application [{}, {}, {}]", name, version, environment);

        String url = config.getServiceUrl();

        app = restClient.target(config.getServiceUrl())
                .path(name)
                .path(version)
                .path(environment)
                .request()
                .get(Map.class);

        String dirname = String.format("app-%s-%s-%s",name, version, environment);
        String fileName = String.format("%s.json", dirname);

        //store the Json file in the config directory.
        ObjectMapper om = new ObjectMapper();
        om.writeValue(new FileWriter(backupDir.getPath() + File.separator + fileName), app);

        List<Map<String, String>> docs = (List) app.getOrDefault("documents", null);

        if (docs == null) {
            return;
        }

        File docsDir = new File(backupDir.getPath() + File.separator + dirname);

        Files.createDirectory(docsDir.toPath());

        for(Map<String, String> doc : docs) {

            String key = doc.get("key");
            InputStream is = restClient.target(config.getServiceUrl())
                    .path(name)
                    .path(version)
                    .path(environment)
                    .path("dynamic")
                    .path(key)
                    .request()
                    .get(InputStream.class);

            File target = new File(docsDir.getPath() + File.separator + key);
            FileOutputStream fos = new FileOutputStream(target);

            IOUtils.copy(is, fos);
        }


    }

    private List<Map<String, ?>> retrieveApps(Client restClient, CliConfig config, Logger logger) {

        //make the api call to retrieve all applications.
        return restClient.target(config.getServiceUrl()).request().get(List.class);
    }

    private void backupEncryptionKey(Client restClient, CliConfig config, File backupDir, Logger logger) throws IOException, GeneralSecurityException {

        Map<String, String> encKeyData = retrieveEncryptionSettings(config, restClient);

        Key encKey = retrieveRemoteKey(encKeyData, config);

        String keyStoreFilename = backupDir.getPath() + File.separator + "enc-keys.jceks";
        String jsonFilename = backupDir.getPath() + File.separator + "enc-props.json";

        logger.info("Saving encryption keys to: {}", keyStoreFilename);

        KeyStore ks = KeyStore.getInstance(config.getClientEncryptionKeyStore().getType());
        ks.load(null, null);

        char[] keyPassword = CliUtils.readPassword("Please type a password for the key: ", logger, 4);

        ks.setKeyEntry("enc-key", encKey, keyPassword, null);

        char[] password = CliUtils.readPassword("Please type a password for the new keystore: ", logger, 4);
        ks.store(new FileOutputStream(keyStoreFilename), password);

        logger.info("Saving encryption options to: {}", jsonFilename);

        //remove info we stored in a better way.
        encKeyData.remove("encodedKey");
        encKeyData.remove("macSignature");

        ObjectMapper om = new ObjectMapper();

        om.writeValue(new FileWriter(jsonFilename), encKeyData);
    }



    @Override
    public Optional<ConfigurationValidator> validator(String... taskArguments) {

        String dir = taskArguments.length == 0 ? null : taskArguments[0];

        return Optional.of(new BackupValidator(dir));
    }
}
