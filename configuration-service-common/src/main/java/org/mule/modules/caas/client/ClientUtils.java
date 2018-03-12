package org.mule.modules.caas.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.SslConfigurator;
import org.mule.modules.caas.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;

import static org.mule.modules.caas.util.ConfigurationServiceUtil.loadFilesystemOrClasspathResource;

public class ClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClientUtils.class);

    /**
     * Static instance of the allow all hostname verifier
     */
    private static final HostnameVerifier ALLOW_ALL_VERIFIER = new HostnameVerifier() {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return false;
        }
    };


    public static Client buildRestClient(ServiceConfiguration config) {

        String trustStore = config.getTrustStore();
        String keyStore = config.getKeyStore();
        boolean disableHostnameValidation = config.isDisableHostNameVerification();
        String trustPassword = config.getTrustStorePassword();
        String keyPassword = config.getKeyStorePassword();

        ClientBuilder cb = ClientBuilder.newBuilder();
        Client client = null;

        try {

            if (trustStore == null && keyStore == null) {
                client = cb.build();
                return client;
            }

            if (disableHostnameValidation) {
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

                if (trustStore != null) {
                    logger.debug("Loading trust store from {}", trustStore);
                    sslConfig.trustStoreBytes(IOUtils.toByteArray(loadFilesystemOrClasspathResource(trustStore)));
                }

                if (trustPassword != null) {
                    sslConfig.trustStorePassword(trustPassword);
                }

                if (keyStore != null) {
                    logger.debug("Loading keystore from {}", keyStore);
                    sslConfig.keyStoreBytes(IOUtils.toByteArray(loadFilesystemOrClasspathResource(keyStore)));
                }

                if (keyPassword != null) {
                    sslConfig.keyStorePassword(keyPassword);
                }

                cb.sslContext(sslConfig.createSSLContext());

            } catch (IOException ex) {
                logger.error("Error while configuring SSL on client, leaving client unfonfigured...", ex);
            }

            client = cb.build();
        } catch (Throwable ex) {
            logger.error("Unknown error while building client...", ex);
        } finally {
            if (client != null) {
                client.register(JacksonJsonProvider.class);
            }
            return client;
        }
    }


}
