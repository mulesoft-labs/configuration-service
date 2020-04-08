/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.utils.keyfactories;

import org.bouncycastle.util.encoders.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p> Creates a key for encryption</p>
 *
 * @author MuleSoft, Inc.
 */
public class AsymmetricEncryptionKeyFactory implements EncryptionKeyFactory {

    private String algorithm;
    private String key;
    private boolean usePublicKeyToEncrypt;

    public AsymmetricEncryptionKeyFactory(String algorithm, String key, boolean usePublicKeyToEncrypt) {
        this.algorithm = algorithm;
        this.key = key;
        this.usePublicKeyToEncrypt = usePublicKeyToEncrypt;
        validateKey();
    }



    @Override
    public Key buildEncryptionKey() {
        if (usePublicKeyToEncrypt) {
            return buildX509EncodedKey();
        } else {
            return buildPKCS8Key();
        }
    }



    private Key buildPKCS8Key()
    {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(key));
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Could not build the Encryption key", e);
        }
    }

    @Override
    public Key buildDecryptionKey() {
        if (usePublicKeyToEncrypt) {
            return buildPKCS8Key();
        } else {
            return buildX509EncodedKey();
        }
    }



    private Key buildX509EncodedKey()
    {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(key));
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Could not build the descryption key", e);
        }
    }

    @Override
    public String getPlainKey() {
        return key;
    }

    private void validateKey() {
        if ( key == null ){
            throw new IllegalArgumentException("If keystore is not defined then the key is considered to be " +
                    "an encryption key in Base64 encoding");
        }
    }

}
