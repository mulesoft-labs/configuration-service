package org.mule.modules.caas.api;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.COMMUNITY;

public class ConfigurationServiceExtensionLoadingDelegate implements ExtensionLoadingDelegate {

    public static final String EXT_NAME = "Configuration Service";

    public static final String URL_PARAM = "serviceUrl";
    public static final String APP_PARAM = "application";
    public static final String VER_PARAM = "version";
    public static final String ENV_PARAM = "environment";


    @Override
    public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext extensionLoadingContext) {
        ConfigurationDeclarer configDeclarer = extensionDeclarer.named(EXT_NAME)
                .describedAs("Configuration Service Properties Provider")
                .onVersion("1.0.0")
                .withCategory(COMMUNITY)
                .fromVendor("MuleSoft")
                .withConfig("config");

        ParameterGroupDeclarer parameterGroup = configDeclarer.onDefaultParameterGroup();

        parameterGroup.withRequiredParameter(URL_PARAM)
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build());
        parameterGroup.withRequiredParameter(APP_PARAM)
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build());
        parameterGroup.withRequiredParameter(VER_PARAM)
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build());
        parameterGroup.withRequiredParameter(ENV_PARAM)
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build());

    }
}
