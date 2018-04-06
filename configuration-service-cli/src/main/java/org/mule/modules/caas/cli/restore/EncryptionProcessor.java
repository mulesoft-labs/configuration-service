package org.mule.modules.caas.cli.restore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.modules.caas.cli.config.CliConfig;
import org.mule.modules.caas.cli.config.ServiceConfigurationAdapter;
import org.mule.modules.caas.cli.utils.CliUtils;
import org.mule.modules.caas.client.EncryptionDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class EncryptionProcessor {

    private final Cipher decCipher;
    private final Cipher encCipher;

    private static final Logger logger = LoggerFactory.getLogger(EncryptionProcessor.class);

    public EncryptionProcessor() {
        //this is effectively a noop.
        decCipher = null;
        encCipher = null;
    }

    private void testJce(Logger prompt) {
        try {
            int length = Cipher.getMaxAllowedKeyLength("AES");

            logger.debug("Max key length allowed for 'AES' is: {}", length);

            if (length <= 128) {
                prompt.warn("JCE Unlimited Strength Policy is not installed on JDK!!!");
            }

        } catch (Exception ex) {
            logger.warn("JCE does not seem to be installed!");
        }
    }

    public EncryptionProcessor(Map<String, String> encKeyData, File backupsDirectory, CliConfig config, Logger prompt) {

        testJce(prompt);

        decCipher = initDecryptionCipher(backupsDirectory, config, prompt);
        encCipher = initEncryptionCipher(encKeyData, config);
    }

    private Cipher initEncryptionCipher(Map<String, String> encKeyData, CliConfig config) {
        try {
            Key key = EncryptionDataWrapper.builder()
                    .withServiceConfiguration(ServiceConfigurationAdapter.get(config))
                    .withWrappedKeyData(encKeyData)
                    .buildWrapperKey();

            return buildCipherWithModeAndParams(Cipher.ENCRYPT_MODE, key, encKeyData);

        } catch (Exception ex) {
            logger.error("Could not retrieve encryption key!", ex);
            logger.error("Will be skipping encryption!");
        }
        return null;
    }

    private Cipher initDecryptionCipher(File backupsDirectory, CliConfig config, Logger prompt) {

        Optional<Boolean> decryptionEnabled = Optional.of(config.isDecryptionEnabled());

        if (decryptionEnabled.isPresent() && !decryptionEnabled.get()) {
            //this means the setting is not null and is false, then we don't decrypt.
            return null;
        }


        //in this case, we need to read the key from the keystore, so first we load it.
        String keystoreFile = backupsDirectory + File.separator + "enc-keys.jceks";
        String settingsJson = backupsDirectory + File.separator + "enc-props.json";

        File encKs = new File(keystoreFile);
        File encSets = new File(settingsJson);

        if (!encKs.exists() || !encSets.exists()) {
            prompt.error("Backup does not have decryption information, decryption will be skipped.");
            return null;
        }

        try {

            KeyStore ks = KeyStore.getInstance("JCEKS");
            char[] password = CliUtils.readExistingPassword("Please enter backup keystore password.", prompt, 4);

            ks.load(new FileInputStream(keystoreFile), password);

            //load the key
            char[] kpassword = CliUtils.readExistingPassword("Please enter backup decryption key password. (leave empty for same as keystore)", prompt, 4, true);

            if (kpassword != null) {
                password = kpassword;
            }

            Key key = ks.getKey("enc-key", password);

            if (key == null) {
                logger.error("Error loading backup decryption key, decryption will be skipped!");
            }

            //load the encyption properties
            ObjectMapper om = new ObjectMapper();
            Map<String, String> encSetsMap = om.readValue(encSets, Map.class);

            return buildCipherWithModeAndParams(Cipher.DECRYPT_MODE, key, encSetsMap);
        } catch (Exception ex) {
            logger.error("Could not retrieve decryption key!", ex);
            logger.error("Will be skipping decryption!");
        }

        return null;
    }

    private Cipher buildCipherWithModeAndParams(int encryptMode, Key algKey, Map<String, String> encKeyData) throws GeneralSecurityException, IOException {
        String algorithm = new String(Base64.getDecoder().decode(encKeyData.get("algorithm")));
        byte[] parameters = Base64.getDecoder().decode(encKeyData.get("parameters"));

        String[] algorithmComponents = algorithm.split("/");

        Cipher cipher = Cipher.getInstance(algorithm);

        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(algorithmComponents[0]);
        algorithmParameters.init(parameters);

        cipher.init(encryptMode, algKey, algorithmParameters);

        return cipher;
    }



    public Map<String, String> encryptProperties(Map<String, String> properties) {

        if (encCipher == null || properties == null) {
            //do nothing.
            return properties;
        }

        Map<String, String> ret = new LinkedHashMap<>();
        Base64.Encoder enc = Base64.getEncoder();

        for(Map.Entry<String, String> entry : properties.entrySet()) {
            String encrKey = entry.getKey();
            String encrValue = entry.getValue();
            try {
                encrKey = enc.encodeToString(encCipher.doFinal(encrKey.getBytes()));
                encrValue = enc.encodeToString(encCipher.doFinal(encrValue.getBytes()));
                ret.put(encrKey, encrValue);
            } catch (Exception ex) {
                logger.error("Could not encrypt property, leaving as is...", ex);
                ret.put(entry.getKey(), entry.getValue());
            }
        }

        return ret;
    }

    public Map<String, String> decryptProperties(Map<String, String> properties) {
        if (decCipher == null || properties == null) {
            //do nothing.
            return properties;
        }

        Map<String, String> ret = new LinkedHashMap<>();
        Base64.Decoder dec = Base64.getDecoder();

        for(Map.Entry<String, String> entry : properties.entrySet()) {
            String decrKey = entry.getKey();
            String decrValue = entry.getValue();
            try {
                decrKey = new String(decCipher.doFinal(dec.decode(decrKey)));
                decrValue = new String(decCipher.doFinal(dec.decode(decrValue)));
                ret.put(decrKey, decrValue);
            } catch (Exception ex) {
                logger.error("Could not decypt property, leaving as is...", ex);
                ret.put(entry.getKey(), entry.getValue());
            }
        }


        return ret;
    }

    public Map<String, String> recryptProperties(Map<String, String> original) {
        return encryptProperties(decryptProperties(original));
    }

    public InputStream encryptionStream(InputStream is) {

        if (encCipher == null) {
            return is;
        }

        return new CipherInputStream(is, encCipher);
    }

    public InputStream decryptionStream(InputStream is) {
        if (decCipher == null) {
            return is;
        }

        return new CipherInputStream(is, decCipher);
    }

    public InputStream recryptionStream(InputStream is) {
        return encryptionStream(decryptionStream(is));
    }

}
