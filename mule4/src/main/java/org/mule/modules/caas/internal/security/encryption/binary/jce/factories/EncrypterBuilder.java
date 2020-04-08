/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.encryption.binary.jce.factories;


import org.mule.modules.caas.internal.security.encryption.binary.Encrypter;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;

public abstract class EncrypterBuilder
{

    protected String keystorePath;
    protected String keystorePassword;
    protected String keyPassword;
    protected EncryptionMode mode;
    protected boolean usePublicKeyToEncrypt;
    protected String key;

    public EncrypterBuilder createWith(String keyFile, String keyFilePassword) {
        this.keystorePath = keyFile;
        this.keystorePassword = keyFilePassword;
        return this;
    }

    public EncrypterBuilder forKey(String key, String keyPassword) {
        this.keyPassword = keyPassword;
        this.key = key;
        return this;
    }


    public EncrypterBuilder using(EncryptionMode mode) {
        this.mode = mode;
        return this;
    }

    public abstract Encrypter build();

    public EncrypterBuilder forKey(String key){
        this.key = key;
        return this;
    }

    public EncrypterBuilder usePublicKeyToEncrypt(boolean usePublicKeyToEncrypt) {
        this.usePublicKeyToEncrypt = usePublicKeyToEncrypt;
        return this;
    }
}
