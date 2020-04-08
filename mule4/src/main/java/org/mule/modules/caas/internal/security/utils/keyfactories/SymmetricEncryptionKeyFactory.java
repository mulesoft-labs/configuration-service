/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.utils.keyfactories;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;



/**
 * <p> Creates a key for decryption</p>
 *
 * @author MuleSoft, Inc.
 */
public class SymmetricEncryptionKeyFactory implements EncryptionKeyFactory {
    private String algorithm;
    private String key;

    public SymmetricEncryptionKeyFactory(String algorithm, String key) {
        this.algorithm = algorithm;
        this.key = key;
        validateKey();
    }

    @Override
    public Key buildEncryptionKey() {
        byte[] bytes = key.getBytes();
        return new SecretKeySpec(bytes, algorithm);
    }

    @Override
    public Key buildDecryptionKey() {
        return buildEncryptionKey();
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
