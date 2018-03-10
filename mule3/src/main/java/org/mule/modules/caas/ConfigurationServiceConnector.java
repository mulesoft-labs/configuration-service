package org.mule.modules.caas;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.SslConfigurator;
import org.mule.api.MuleContext;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.transformer.DataType;
import org.mule.devkit.api.transformer.DefaultTranformingValue;
import org.mule.devkit.api.transformer.TransformingValue;
import org.mule.modules.caas.client.DefaultApplicationDataProvider;
import org.mule.modules.caas.config.ConnectorConfig;
import org.mule.modules.caas.model.ApplicationConfiguration;
import org.mule.modules.caas.model.ApplicationConfigurationBuilder;
import org.mule.modules.caas.model.ApplicationDocument;
import org.mule.transformer.types.SimpleDataType;
import org.mule.util.ClassUtils;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import sun.net.www.protocol.https.DefaultHostnameVerifier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Connector(name="configuration-service", friendlyName="Configuration Service")
public class ConfigurationServiceConnector extends PreferencesPlaceholderConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceConnector.class);
	
	@Inject
	private MuleContext context;
	
    @Config
    ConnectorConfig config;

    private ApplicationConfiguration appConfig;

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
    @PostConstruct
    public void setup() throws Exception {
    	
    	logger.debug("Setting up connector with properties: {}", config);

    	//standard for rest clients.
		Client client = buildClient();

    	ApplicationDataProvider provider = new DefaultApplicationDataProvider(config.getConfigServerBaseUrl(), client);

    	appConfig = loadApplicationConfiguration(provider, resolveApplicationName(), config.getVersion(), config.getEnvironment());
    }
    
    @Override
    protected String resolvePlaceholder(String placeholder, Properties p) {
    	logger.debug("Call to resolve placeholder: {}", placeholder);
    	
    	String value = this.appConfig.readProperty(placeholder);

    	if (value != null) {
    		logger.debug("Found key in config server");
    		return value;
    	}
    	
    	logger.debug("Key not found in config server, resolving in the traditional way");
    	return super.resolvePlaceholder(placeholder, p);
    }

    
    private String resolveApplicationName() {
    	
    	String app = config.getApplicationName();
    	
    	if (logger.isDebugEnabled()) logger.debug("Found app name: {}", app);
    	
    	if (StringUtils.isEmpty(app)) {
    		app = context.getConfiguration().getId();
    		
    		if (logger.isDebugEnabled()) logger.debug("Detected app name: {}", app);
    	}
    	
    	if (StringUtils.isEmpty(app)) {
    		logger.error("App name could not be detected");
    		throw new IllegalArgumentException("Could not detect application name from context or configuration.");
    	}
    	
    	
    	if (logger.isDebugEnabled()) logger.debug("Detected app name: {} ", app);
    	
    	return app;
    	
    }

	public void setContext(MuleContext context) {
		this.context = context;
	}


	@Processor
	public TransformingValue<InputStream, DataType<InputStream>> readDocument(String key) throws ConfigurationNotFoundException {

        ApplicationDocument doc = appConfig.findDocument(key);

        if (doc == null) throw new ConfigurationNotFoundException("Could not find document " + key + " in application " + appConfig.getName());

		Client client = buildClient();

        ApplicationDataProvider provider = new DefaultApplicationDataProvider(config.getConfigServerBaseUrl(), client);

        return new DefaultTranformingValue<>(provider.loadDocument(doc, appConfig), new SimpleDataType<InputStream>(InputStream.class, doc.getContentType()));
    }

	private Client buildClient() {
		ClientBuilder cb = ClientBuilder.newBuilder();
        Client client = null;

		try {

            if (config.getTrustStore() == null && config.getKeyStore() == null) {
                client = cb.build();
                return client;
            }

            if (config.isDisableHostNameVerification()) {
                cb.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
            }

            try {
                //configure SSL if needed.
                SslConfigurator sslConfig = SslConfigurator.newInstance();

                if (config.getTrustStore() != null) {
                    logger.debug("Loading trust store from {}", config.getTrustStore());
                    sslConfig.trustStoreBytes(IOUtils.toByteArray(ClassUtils.getResource(config.getTrustStore(), getClass())));
                }

                if (config.getTrustStorePassword() != null) {
                    sslConfig.trustStorePassword(config.getTrustStorePassword());
                }

                if (config.getKeyStore() != null) {
                    logger.debug("Loading keystore from {}", config.getKeyStore());
                    sslConfig.keyStoreBytes(IOUtils.toByteArray(ClassUtils.getResource(config.getKeyStore(), getClass())));
                }

                if (config.getKeyStorePassword() != null) {
                    sslConfig.keyStorePassword(config.getKeyStorePassword());
                }

                cb.sslContext(sslConfig.createSSLContext());

            } catch (IOException ex) {
                logger.error("Error while configuring SSL on client, leaving client unfonfigured...", ex);
            }

            client = cb.build();

        } finally {
            if (client != null) {
                client.register(JacksonJsonProvider.class);
            }
            return client;
        }

	}

	/**
     * Recursive method to read from the configuration service an app and its parents.
     * @param name the application name to read.
     * @param version the version to read.
     * @param environment the environment.
     * @return an application configuration.
     */
    protected ApplicationConfiguration loadApplicationConfiguration(ApplicationDataProvider provider, String name, String version, String environment) throws ConfigurationServiceException{
        return provider.loadApplicationConfiguration(name, version, environment);
    }

}