package org.mule.modules.caas.internal.operations;

import org.apache.commons.lang3.StringUtils;
import org.mule.modules.caas.ApplicationDataProvider;
import org.mule.modules.caas.api.ConfigurationServiceException;
import org.mule.modules.caas.internal.ConfigurationServiceConfig;
import org.mule.modules.caas.internal.StaticConfigCache;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationDocument;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ListAllDocumentsOperation {
    Logger LOGGER = LoggerFactory.getLogger(RefreshDocumentsOperation.class);

    @MediaType(MediaType.ANY)
    @DisplayName(value = "List All Documents")
    public List<String> listDocuments(String configId) throws Exception {

        //retrieve the configuration in the static cache.

        Optional<ApplicationConfiguration> appConfig = StaticConfigCache.get()
                .find(configId);
        ConfigurationServiceConfig serviceConfig = StaticConfigCache.get().
                getServiceUrl(configId).orElseThrow(() -> new ConfigurationServiceException("Cannot find config"));

        List<String> documents = new ArrayList<>();
        documents = listDocuments(documents, appConfig.get());
        LOGGER.info("documents size {}" , documents.size());
        return documents;

    }


    private List<String> listDocuments(List<String> documents, ApplicationConfiguration applicationConfiguration) {
        List<ApplicationConfiguration> imports = applicationConfiguration.getImports();
        List<ApplicationDocument> applicationDocumentList = applicationConfiguration.getDocuments();
        for (ApplicationDocument document : applicationDocumentList) {
            documents.add(document.getName());
        }
        //read import first
        for (ApplicationConfiguration appImport : imports) {
                listDocuments(documents, appImport);
        }

        return documents;
    }


}
