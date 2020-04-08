/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.encryption.binary.jce;


import org.mule.modules.caas.internal.security.encryption.MuleEncryptionException;
import org.mule.modules.caas.internal.security.encryption.NotSupportedInFipsModeException;
import org.mule.modules.caas.internal.security.encryption.binary.Encrypter;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionPadding;
import org.mule.modules.caas.internal.security.utils.keyfactories.EncryptionKeyFactory;
import org.mule.runtime.core.api.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;

import static org.mule.modules.caas.internal.security.utils.AESFactory.*;

public class JCEEncrypter implements Encrypter {


    protected EncryptionAlgorithm encryptionAlgorithm;

    protected EncryptionMode encryptionMode;

    protected EncryptionPadding encryptionPadding;
    private EncryptionKeyFactory keyFactory;

    private static final String INSTALL_JCE_MESSAGE = "You need to install the Java Cryptography Extension (JCE) " +
            "Unlimited Strength Jurisdiction Policy Files";

    private static final String FIPS_MODE_MESSAGE = "You're running in FIPS mode " +
            " so please verify that the algorithm is compliant with FIPS.";

    private static final String SHORT_KEY_MESSAGE = "You need to increment your key size " +
            " The minimum allowed key size is: %d "  +
            " But your key size is: %d";

    private static final String LONG_KEY_MESSAGE = "Your key size exceeds the maximum allowed key size in your JVM. " +
            " The maximum allowed key size is: %d" +
            " But your key size is: %d.";

    public JCEEncrypter(EncryptionAlgorithm encryptionAlgorithm,
                        EncryptionMode encryptionMode,
                        EncryptionPadding encryptionPadding,
                        EncryptionKeyFactory keyFactory) {

        this.encryptionAlgorithm = encryptionAlgorithm;
        this.encryptionMode = encryptionMode;
        this.encryptionPadding = encryptionPadding;
        this.keyFactory = keyFactory;
    }

    @Override
    public byte[] encrypt(byte[] data) throws MuleEncryptionException {

        try {
            Cipher cipher = getCipher(createXForm());
            Key cipherKey = keyFactory.buildEncryptionKey();
            runCipher(cipher, cipherKey, Cipher.ENCRYPT_MODE, keyFactory.getPlainKey());
            return cipher.doFinal(data);

        } catch (Exception e) {
            throw buildEncryptionException(e, keyFactory.getPlainKey());
        }
    }

    @Override
    public void encrypt(InputStream in, OutputStream out) throws MuleEncryptionException {
        try {
            Cipher cipher = getCipher(createXForm());
            Key cipherKey =keyFactory.buildEncryptionKey();
            runCipher(cipher, cipherKey, Cipher.ENCRYPT_MODE, keyFactory.getPlainKey());
            byte[] buf = new byte[1024];
            out = new CipherOutputStream(out, cipher);

            int numRead;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }

        } catch (Exception e) {
            buildEncryptionException(e, keyFactory.getPlainKey());
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public byte[] decrypt(byte[] data) throws MuleEncryptionException {

        try {
            Cipher cipher = getCipher(createXForm());
            Key cipherKey = keyFactory.buildDecryptionKey();
            runCipher(cipher, cipherKey, Cipher.DECRYPT_MODE, keyFactory.getPlainKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw buildEncryptionException(e, keyFactory.getPlainKey());
        }
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws MuleEncryptionException {
        try {
            Cipher cipher = getCipher(createXForm());
            Key cipherKey = keyFactory.buildDecryptionKey();
            runCipher(cipher, cipherKey, Cipher.DECRYPT_MODE, keyFactory.getPlainKey());
            in = new CipherInputStream(in, cipher);

            byte[] buf = new byte[1024];
            int numRead;
            while ((numRead = in.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
        } catch (Exception e) {
            throw buildEncryptionException(e, keyFactory.getPlainKey());
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private String createXForm() {
        return encryptionAlgorithm.name() + "/" + encryptionMode.name() + "/" +
                encryptionPadding.name();
    }

    private void runCipher(Cipher cipher, Key cipherKey, int mode, String key) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (encryptionMode.equals(EncryptionMode.ECB))
        {
            cipher.init(mode, cipherKey);
        }
        else{
            IvParameterSpec ips = new IvParameterSpec(Arrays.copyOfRange(key.getBytes(), 0, ivSize()));
            cipher.init(mode, cipherKey, ips);
        }
    }

    private int ivSize() {
        return encryptionAlgorithm.getIvSize() == 0 ? 8 : encryptionAlgorithm.getIvSize();
    }

    private MuleEncryptionException buildEncryptionException(Exception e, String key)  {

        if (e instanceof InvalidAlgorithmParameterException) {
            return handleInvalidAlgorithmParameterException((InvalidAlgorithmParameterException) e);
        } else if (e instanceof InvalidKeyException) {
            return handleInvalidKeyException((InvalidKeyException) e, key);
        } else if (e instanceof NoSuchAlgorithmException) {
            return wrapNoSuchAlgorithmException((NoSuchAlgorithmException) e);
        } else {
            return new MuleEncryptionException("Could not encrypt the data.", e);
        }
    }

    private MuleEncryptionException handleInvalidAlgorithmParameterException(InvalidAlgorithmParameterException e) {
        String message = String.format("Wrong configuration for algorithm: %s.", encryptionAlgorithm.name());
        if (!isJCEInstalled()) {
            message += INSTALL_JCE_MESSAGE;
        }
        return new MuleEncryptionException(message, e);
    }

    private MuleEncryptionException wrapNoSuchAlgorithmException(NoSuchAlgorithmException e) {
        String message = String.format("Invalid algorithm: %s.", encryptionAlgorithm.name());

        if (!isJCEInstalled()) {
            message += INSTALL_JCE_MESSAGE;
            return new MuleEncryptionException(message, e);
        }
        else if (isFipsEnabled()) {
            return new MuleEncryptionException(message, new NotSupportedInFipsModeException(FIPS_MODE_MESSAGE, e));
        }
        else {
            return new MuleEncryptionException(message, e);
        }
    }

    private MuleEncryptionException handleInvalidKeyException(InvalidKeyException e, String key) {

        String message = String.format("Invalid key: %s.", key);

        if (key.getBytes().length > encryptionAlgorithm.getMaxKeySize()) {
            message += String.format(LONG_KEY_MESSAGE, encryptionAlgorithm.getMaxKeySize(), key.getBytes().length);
            if (!isJCEInstalled()) {
                message += INSTALL_JCE_MESSAGE;
            }
        }
        else if (key.getBytes().length < encryptionAlgorithm.getMinKeySize()) {
            message += String.format(SHORT_KEY_MESSAGE, encryptionAlgorithm.getMinKeySize(), key.getBytes().length);
        }

        return new MuleEncryptionException(message, e);
    }

}
