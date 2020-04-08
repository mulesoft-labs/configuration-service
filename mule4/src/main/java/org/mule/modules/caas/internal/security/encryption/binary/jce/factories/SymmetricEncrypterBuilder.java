/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.encryption.binary.jce.factories;

import org.mule.modules.caas.internal.security.encryption.binary.Encrypter;
import org.mule.modules.caas.internal.security.encryption.binary.jce.JCEEncrypter;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionPadding;
import org.mule.modules.caas.internal.security.utils.keyfactories.KeystoreEncryptionKeyFactory;
import org.mule.modules.caas.internal.security.utils.keyfactories.SymmetricEncryptionKeyFactory;

public class SymmetricEncrypterBuilder extends EncrypterBuilder {
    private EncryptionAlgorithm encryptionAlgorithm;

    public SymmetricEncrypterBuilder(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    @Override
    public Encrypter build() {
        if ( keystorePath != null ){

            return new JCEEncrypter(encryptionAlgorithm,mode, EncryptionPadding.PKCS5Padding,
                    new KeystoreEncryptionKeyFactory(keystorePath,
                            keystorePassword, keyPassword, key, true));
        }

        return  new JCEEncrypter(encryptionAlgorithm,mode, EncryptionPadding.PKCS5Padding,
                new SymmetricEncryptionKeyFactory(encryptionAlgorithm.name(), key));
    }
    
    @Override
    public EncrypterBuilder usePublicKeyToEncrypt(boolean usePublicKeyToEncrypt)
    {
        if (usePublicKeyToEncrypt) {
            new RuntimeException("A symmetric algorithm is used. usePublicKeyToEncrypt should be false.");
        }
        return this;
    }
}
