package org.mule.modules.caas.client;

import org.apache.commons.codec.binary.Base64;
import org.mule.modules.caas.model.ConfigurationDataWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncryptionDataWrapper implements ConfigurationDataWrapper {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionDataWrapper.class);

    private final Cipher cipher;

    public static EncryptionDataWrapperBuilder builder() {
        return new EncryptionDataWrapperBuilder();
    }

    EncryptionDataWrapper(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public String wrapKey(String key) {

        try {
            return doDecrypt(key);
        } catch (Exception ex) {
            logger.error("Could not decrypt key {}, leaving untouched", key, ex);
            return key;
        }

    }

    @Override
    public String wrapValue(String value) {
        try {
            return doDecrypt(value);
        } catch (Exception ex) {
            logger.error("Could not decrypt value, leaving untouched", ex);
            return value;
        }
    }

    @Override
    public InputStream wrapStream(InputStream is) {
        return new CipherInputStream(is, cipher);
    }

    @Override
    public Map<String, String> wrapProperties(Map<String, String> properties) {

        if (properties == null) {
            return null;
        }

        Map<String, String> ret = new HashMap<>();

        for(Map.Entry<String, String> e : properties.entrySet()) {
            ret.put(wrapKey(e.getKey()), wrapValue(e.getValue()));
        }

        return Collections.unmodifiableMap(ret);
    }

    private String doDecrypt(String value) throws BadPaddingException, IllegalBlockSizeException {
        byte[] valueDecoded = Base64.decodeBase64(value);
        byte[] str = cipher.doFinal(valueDecoded);
        return new String(str);
    }
}
