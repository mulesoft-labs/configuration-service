package org.mule.modules.caas.client;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.SslConfigurator;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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


    public static Client buildRestClient(String keyStore, String keyPassword, String trustStore, String trustPassword, boolean disableHostnameValidation) {

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

            } finally {
                if (client != null) {
                    client.register(JacksonJsonProvider.class);
                }
                return client;
            }
    }

    public static InputStream loadFilesystemOrClasspathResource(String resourceName) {

        try {

            File tryFile = new File(resourceName);

            if (tryFile.exists()) {
                logger.debug("Found resource {} in filesystem, loading it...", resourceName);
                return new FileInputStream(tryFile);
            }



            InputStream is = null;

            if (Thread.currentThread().getContextClassLoader() != null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            }

            if (is == null) {
                ClientUtils.class.getClassLoader().getResourceAsStream(resourceName);
            }

            if (is == null) {
                logger.warn("Could not find resource: {} either on classpath or filesystem...", resourceName);
            }

            return is;

        } catch (IOException ex) {
            logger.error("Error while trying to load resource from filesystem or classpath...", ex);
            throw new RuntimeException(ex);
        }
    }


}
