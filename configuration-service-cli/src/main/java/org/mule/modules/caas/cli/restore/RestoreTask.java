package org.mule.modules.caas.cli.restore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.mule.modules.caas.cli.backup.BackupValidator;
import org.mule.modules.caas.cli.common.AbstractAPITask;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ConfigurationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RestoreTask extends AbstractAPITask {

    private static final Logger internalLogger = LoggerFactory.getLogger(RestoreTask.class);

    @Override
    public boolean runTask(CliConfig config, Logger outputLogger, String... taskArguments) {

        //in general, restore is much more difficult than backup
        //because we have dependencies.

        //first things first
        try {
            File restoreFolder = locateRestoreFolder(config, outputLogger, taskArguments);
            if (restoreFolder == null) {
                return false;
            }

            if (restoreFolder.isFile()) {
                outputLogger.error("Cannot restore {} as it is a file!", restoreFolder.getPath());
                return false;
            }

            outputLogger.info("Restoring backup from location: " + restoreFolder.getPath());

            return doRestore(config, outputLogger, restoreFolder);
        } catch (Exception ex) {
            outputLogger.error("Could not restore backup!", ex);
            return false;
        }

    }

    private File locateRestoreFolder(CliConfig config, Logger outputLogger, String[] taskArguments) {

        if (ArrayUtils.getLength(taskArguments) > 0) {
            return new File(taskArguments[0]);
        }

        //try to locate latest backup
        File backupsFolder = new File(System.getProperty("app.home") + File.separator + config.getBackupsDirectory());

        //get the last modified one
        File[] backups = backupsFolder.listFiles();

        File latest = Arrays.stream(backups)
                .reduce((accumulator, current) -> accumulator.lastModified() >= current.lastModified() ? accumulator : current)
                .orElse(null);

        return latest;
    }

    private boolean doRestore(CliConfig config, Logger outputLogger, File restoreFolder) {

        //we need to list all the configs and recursively look for dependencies.
        Set<String> loadedApps = new HashSet<>();

        //list all the config json files in the folder.
        Set<File> configs = Arrays.stream(restoreFolder.listFiles())
                .filter(f -> f.getName().endsWith(".json") && f.getName().startsWith("app-"))
                .collect(Collectors.toSet());

        Client restClient = buildClient(config);

        try {

            for (File appConfigFile : configs) {
                restoreApplication(restClient, config, appConfigFile, configs, loadedApps, outputLogger);
            }

        } catch (Exception ex) {
            outputLogger.error("Could not perform restore!", ex);
            return false;
        }

        return true;
    }

    private void restoreApplication(Client restClient, CliConfig cliConfig, File appConfigFile, Set<File> configs, Set<String> loadedApps, Logger outputLogger) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Map<String, ?> app = om.readValue(new FileInputStream(appConfigFile), Map.class);

        internalLogger.debug("Attempting to load {}", appConfigFile.getName());

        if (loadedApps.contains(appConfigFile.getName())) {
            //app already loaded.
            internalLogger.debug("Config was already loaded.");
            return;
        }

        String applicationDataDir = appConfigFile.getParent() + File.separator + formatDirNameWithCoordinateMap(app);

        //go through the imports.
        List<Map<String, String>> imports = (List<Map<String, String>>) app.get("imports");

        if (imports == null) {
            imports = Collections.emptyList();
        }

        //we need to make sure we load the imports first, here infinite recursion may happen!
        for(Map<String, String> importedApp : imports) {

            String parentFile = formatDirNameWithCoordinateMap(importedApp) + ".json";

            File importedAppFile = configs.stream()
                    .filter(f -> f.getName().equals(parentFile))
                    .findAny().orElse(null);

            if (importedAppFile == null) {
                outputLogger.error("Application {}.json refers to unknown file: {}", applicationDataDir, parentFile);
                throw new RuntimeException("Inconsistent backup!");
            }
            //recurse
            restoreApplication(restClient, cliConfig, importedAppFile, configs, loadedApps, outputLogger);
        }

        //after making sure we've loaded the parents, we can now proceed to load the current app.
        outputLogger.info("Loading {}...", appConfigFile.getName());

        //imported app cannot have documents initially.
        List<Map<String, String>> documents = (List<Map<String, String>>) app.get("documents");

        //to satisfy the dependency.
        app.remove("documents");

        //try send the app
        tryPostOrPut(restClient, cliConfig, app, outputLogger);

        if (documents != null && !documents.isEmpty()) {
            //then post the documents
            tryPutDocuments(restClient, cliConfig, new File(applicationDataDir), app, documents, outputLogger);
        }

        //aaand we're done
        loadedApps.add(appConfigFile.getName());
    }


    private void tryPostOrPut(Client restClient, CliConfig cliConfig, Map<String, ?> app, Logger outputLogger) {

        //we want to post the configuration if this fails, it may exist, we can try
        //doing a put to replace existing.
        Response response = restClient.target(cliConfig.getServiceUrl())
                .request()
                .post(Entity.entity(app, MediaType.APPLICATION_JSON_TYPE));

        internalLogger.debug("Service responded with status {}", response.getStatus());

        if (response.getStatus() == 202) {
            outputLogger.info("Created configuration on configuration server.");
            return;
        }

        internalLogger.debug("Status message was: {}", response.readEntity(String.class));

        //otherwise try and put
        response = buildAppTarget(restClient, cliConfig, app)
                .request()
                .put(Entity.entity(app, MediaType.APPLICATION_JSON_TYPE));

        internalLogger.debug("Service responded to PUT with status {}", response.getStatus());

        if (response.getStatus() != 202) {
            internalLogger.debug("Status message was: {}", response.readEntity(String.class));
        }
    }

    private void tryPutDocuments(Client restClient, CliConfig cliConfig, File appDataDir, Map<String, ?> app, List<Map<String, String>> documents, Logger outputLogger) throws IOException {
        //with documents is easier on the one hand.
        if (!appDataDir.exists()) {
            outputLogger.error("Application has documents but backup does not contain a data directory for the app!");
            throw new RuntimeException("Inconsistent backup!");
        }

        for (Map<String, String> document : documents) {
            String key = document.get("key");
            String type = document.get("type");

            String docFile = appDataDir.getPath() + File.separator + key;

            outputLogger.info("Uploading {}...", key);

            //try to put
            Response resp = buildAppTarget(restClient, cliConfig, app)
                    .path("dynamic")
                    .path(key)
                    .request()
                    .put(Entity.entity(new FileInputStream(docFile), type));

            if (resp.getStatus() != 202) {
                outputLogger.error("Could not upload document!");
                internalLogger.debug("Sevice output: {}", resp.readEntity(String.class));
            }
        }

    }

    private WebTarget buildAppTarget(Client restClient, CliConfig cliConfig, Map<String, ?> app) {
        String name = (String) app.get("application");
        String version = (String) app.get("version");
        String environment = (String) app.get("environment");

        return restClient.target(cliConfig.getServiceUrl())
                .path(name)
                .path(version)
                .path(environment);

    }

    private String formatDirNameWithCoordinateMap(Map<String, ?> coordinateMap) {
        String name = (String) coordinateMap.get("application");
        String version = (String) coordinateMap.get("version");
        String environment = (String) coordinateMap.get("environment");
        return String.format("app-%s-%s-%s",name, version, environment);
    }

    @Override
    public Optional<ConfigurationValidator> validator(String... taskArguments) {

        String dir = null;

        if (taskArguments.length > 0) {
            dir = taskArguments[0];
        }

        //in general we can still use the same backup validation.
        return Optional.of(new BackupValidator(dir));
    }
}
