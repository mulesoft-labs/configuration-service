package org.mule.modules.caas.internal;

import org.mule.modules.caas.internal.operations.DecryptValueOperation;
import org.mule.modules.caas.internal.operations.ListAllDocumentsOperation;
import org.mule.modules.caas.internal.operations.ReadDocumentOperation;
import org.mule.modules.caas.internal.operations.RefreshDocumentsOperation;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;

@Extension(name = "Configuration Service")
@Configurations(ConfigurationServiceConfig.class)
@Operations({ReadDocumentOperation.class, RefreshDocumentsOperation.class, DecryptValueOperation.class, ListAllDocumentsOperation.class})
@Export(resources = "META-INF/services/org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory")
public class ConfigurationServiceExtension {
}
