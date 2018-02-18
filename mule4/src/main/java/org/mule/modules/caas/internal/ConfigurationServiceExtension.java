package org.mule.modules.caas.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;

@Extension(name = "Configuration Service")
@Configurations(ConfigurationServiceConfig.class)
@Operations(ReadDocumentOperation.class)
@Export(resources = "META-INF/services/org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory")
public class ConfigurationServiceExtension {
}
