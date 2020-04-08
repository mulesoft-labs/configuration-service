/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms;


import org.mule.modules.caas.internal.security.encryption.binary.jce.factories.AsymmetricEncrypterBuilder;
import org.mule.modules.caas.internal.security.encryption.binary.jce.factories.EncrypterBuilder;
import org.mule.modules.caas.internal.security.encryption.binary.jce.factories.SymmetricEncrypterBuilder;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

/**
 * <p>The algorithms for encryption</p>
 *
 * @author MuleSoft, Inc.
 */
public enum EncryptionAlgorithm {
    AES(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Blowfish(0, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    DES(0, 8, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    DESede(0, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Camellia(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    CAST5(8, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    CAST6(16, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Noekeon(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Rijndael(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    SEED(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Serpent(16, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Skipjack(8, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    TEA(0, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    Twofish(16, 8, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    XTEA(8, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    RC2(0, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    RC5(0, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    RC6(16, 1, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new SymmetricEncrypterBuilder(algorithm);
        }
    }),
    RSA(0, 16, new EncrypterBuilderFactory() {

        @Override
        public EncrypterBuilder createFor(EncryptionAlgorithm algorithm) {
            return new AsymmetricEncrypterBuilder();
        }
    });


    public int getMinKeySize() {
        return minSize;
    }

    private int minSize;
    private EncrypterBuilderFactory factory;
    private int ivSize;


    EncryptionAlgorithm(int ivSize, int minSize, EncrypterBuilderFactory factory) {
        this.minSize = minSize;
        this.factory = factory;
        this.ivSize = ivSize;
    }

    public EncrypterBuilder getBuilder() {
        return factory.createFor(this);
    }

    public int getIvSize() {
        return ivSize;
    }

    public int getMaxKeySize() {
        try {
            return Cipher.getMaxAllowedKeyLength(this.name()) / 8;
        } catch (NoSuchAlgorithmException e) {
            return 0;
        }
    }

}
