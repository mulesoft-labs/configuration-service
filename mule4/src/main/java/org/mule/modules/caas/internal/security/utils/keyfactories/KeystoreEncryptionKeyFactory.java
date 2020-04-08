/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.utils.keyfactories;

import org.mule.runtime.core.api.util.IOUtils;

import java.security.*;

import static java.lang.String.format;


public class KeystoreEncryptionKeyFactory implements EncryptionKeyFactory {

    public static final String PASSWORD_NOT_DEFINED_ERROR = "It is not possible to get the private key because the password key wasn't defined.";
    private KeyStore ks;
    private String password;
    private boolean symmetric;
    private String keyPassword;
    private String key;
    private boolean usePublicKeyToEncrypt;

    public KeystoreEncryptionKeyFactory(String keystore, String password, String keyPassword,
                                        String key, boolean symmetric) {
        this(keystore, password, keyPassword, key, symmetric, false);
    }

    public KeystoreEncryptionKeyFactory(String keystore, String password, String keyPassword,
                                        String key, boolean symmetric, boolean usePublicKeyToEncrypt) {


        try {
            this.key = key;
            this.password = password;
            this.symmetric = symmetric;
            this.keyPassword = keyPassword;
            this.usePublicKeyToEncrypt = usePublicKeyToEncrypt;

            validateKeystoreParameters();

            ks = KeyStore.getInstance(symmetric ? "JCEKS" : "JKS");
            ks.load(IOUtils.getResourceAsStream(keystore, getClass()), password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Could not read the Keystore", e);
        }
    }

    private void validateKeystoreParameters() {
        if (password == null) {
            throw new IllegalArgumentException("If keystore is defined then the keystore password must be defined");
        }

        if (key == null) {
            throw new IllegalArgumentException("If keystore is defined then the key must be defined");
        }
    }


    @Override
    public Key buildEncryptionKey() {
        if (symmetric || !usePublicKeyToEncrypt) {
            return buildKeyFromKeyStore();
        }

        return buildKeyFromCertificate();
    }

    @Override
    public Key buildDecryptionKey() {
        if (!symmetric && !usePublicKeyToEncrypt) {
            return buildKeyFromCertificate();
        }
        return buildKeyFromKeyStore();
    }

    public Key buildKeyFromCertificate() {
        java.security.cert.Certificate cert;
        try {
            cert = ks.getCertificate(key);
            if (cert != null) {
                return cert.getPublicKey();
            }
            throw new IllegalStateException(format("A key with alias %s wasn't found", key));
        } catch (KeyStoreException e) {
            throw new RuntimeException("There was a problem trying to get the public key", e);
        }
    }

    public Key buildKeyFromKeyStore() {
        if (keyPassword == null) {
            throw new IllegalArgumentException(PASSWORD_NOT_DEFINED_ERROR);
        }
        try {
            return ks.getKey(key, keyPassword.toCharArray());
        } catch (UnrecoverableKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        }
        return null;
    }

    @Override
    public String getPlainKey() {
        return key;
    }
}
