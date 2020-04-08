/*
 * (c) 2003-2019 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.modules.caas.internal.security.utils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public abstract class AESFactory
{
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final String FIPS_SECURITY_MODEL = "fips140-2";
    private static final String PROPERTY_SECURITY_MODEL = "mule.security.model";

    static {
        if (!isFipsEnabled()) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static boolean isFipsEnabled() {
        return FIPS_SECURITY_MODEL.equals(System.getProperty(PROPERTY_SECURITY_MODEL));
    }

    public static Signature getSignature(String signatureAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (isFipsEnabled()) {
            return Signature.getInstance(signatureAlgorithm);
        } else {
            return Signature.getInstance(signatureAlgorithm, BOUNCY_CASTLE_PROVIDER);
        }
    }

    public static Cipher getCipher(String xform) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        if (isFipsEnabled()) {
            return Cipher.getInstance(xform);
        } else {
            return Cipher.getInstance(xform, BOUNCY_CASTLE_PROVIDER);
        }
    }

    public static boolean isJCEInstalled() {
        try {
            int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
            if (maxKeyLen <= 128) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}
