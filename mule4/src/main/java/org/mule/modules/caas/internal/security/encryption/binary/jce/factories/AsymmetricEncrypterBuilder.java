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
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionPadding;
import org.mule.modules.caas.internal.security.utils.keyfactories.AsymmetricEncryptionKeyFactory;
import org.mule.modules.caas.internal.security.utils.keyfactories.KeystoreEncryptionKeyFactory;

public class AsymmetricEncrypterBuilder extends EncrypterBuilder {

    @Override
    public Encrypter build() {
        if ( keystorePath != null ){

            return new JCEEncrypter(EncryptionAlgorithm.RSA, EncryptionMode.ECB, EncryptionPadding.PKCS1PADDING,
                    new KeystoreEncryptionKeyFactory(keystorePath, keystorePassword,
                            keyPassword, key, false, usePublicKeyToEncrypt));
        }


        return new JCEEncrypter(EncryptionAlgorithm.RSA, EncryptionMode.ECB, EncryptionPadding.PKCS1PADDING,
                new AsymmetricEncryptionKeyFactory(EncryptionAlgorithm.RSA.name(),key, usePublicKeyToEncrypt));
    }
}
