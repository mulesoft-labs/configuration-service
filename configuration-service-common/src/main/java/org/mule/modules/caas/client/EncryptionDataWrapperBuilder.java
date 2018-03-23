package org.mule.modules.caas.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.mule.modules.caas.ServiceConfiguration;
import org.mule.modules.caas.util.ConfigurationServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.util.Map;

public class EncryptionDataWrapperBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionDataWrapperBuilder.class);

    public static final String MAC_KEY_ALG = "HmacSHA256";
    public static final String WRAPPING_KEY_ALG = "Blowfish";

    private ServiceConfiguration config;
    private Map<String, String> wrappedKeyData;


    public EncryptionDataWrapperBuilder withServiceConfiguration(ServiceConfiguration config) {
        this.config = config;
        return this;
    }

    public EncryptionDataWrapperBuilder withWrappedKeyData(Map<String, String> wrappedKeyData) {
        this.wrappedKeyData = wrappedKeyData;
        return this;
    }


    public EncryptionDataWrapper build() {

        try {
            return doBuild();
        } catch (Exception ex) {
            logger.error("Could not unwrap encryption key", ex);
            throw new RuntimeException(ex);
        }

    }

    private EncryptionDataWrapper doBuild() throws Exception {

        String algorithm = new String(Base64.decodeBase64(wrappedKeyData.get("algorithm")));
        byte[] parameters = Base64.decodeBase64(wrappedKeyData.get("parameters"));
        byte[] wrappedKey = Base64.decodeBase64(wrappedKeyData.get("encodedKey"));
        String signature = wrappedKeyData.get("macSignature");

        KeyStore ks = loadKeyStore();

        //unwrap the key.
        Key unwrapKey = ks.getKey(config.getWrapKeyAlias(), getChars(config.getWrapKeyPassword()));
        Key macKey = ks.getKey(config.getMacKeyAlias(), getChars(config.getMacKeyPassword()));

        if (unwrapKey == null) {
            logger.error("Unwrapping key not found in keystore! Decryption will not be possible");
            throw new RuntimeException("Key not found with alias: " + config.getWrapKeyAlias());
        }

        if (macKey == null) {
            logger.error("MAC Signature key not found in keystore! Verification of unwrapped key will not be possible");
            throw new RuntimeException("Key not found with alias: " + config.getMacKeyAlias());
        }

        Cipher cipher = Cipher.getInstance(WRAPPING_KEY_ALG);
        //init for unwrapping
        cipher.init(Cipher.UNWRAP_MODE, unwrapKey);

        String[] algorithmComponents = algorithm.split("/");

        Key encKey = cipher.unwrap(wrappedKey, algorithmComponents[0], Cipher.SECRET_KEY);

        //verify the signature
        Mac mac = Mac.getInstance(MAC_KEY_ALG);
        mac.init(macKey);

        String verSignature = Base64.encodeBase64String(mac.doFinal(wrappedKey));

        if (StringUtils.isNotEmpty(verSignature) && StringUtils.equals(signature, verSignature)) {
            logger.debug("Verification of key signature passed.");
        } else {
            logger.error("Signature not passed!! orig {}, new {}", signature, verSignature);
            throw new RuntimeException("Signature could not be verified, encryption key may be counterfeit.");
        }

        Cipher decCipher = Cipher.getInstance(algorithm);

        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(algorithmComponents[0]);
        algorithmParameters.init(parameters);

        decCipher.init(Cipher.DECRYPT_MODE, encKey, algorithmParameters);

        return new EncryptionDataWrapper(decCipher);
    }

    private KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
        InputStream is = ConfigurationServiceUtil.loadFilesystemOrClasspathResource(config.getClientDecryptionKeyStore());
        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(is, getChars(config.getClientDecryptionKeyStorePassword()));

        return ks;
    }

    private char[] getChars(String s) {
        if (s == null) {
            return null;
        }

        return s.toCharArray();
    }
}
