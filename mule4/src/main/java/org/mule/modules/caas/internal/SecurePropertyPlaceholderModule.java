package org.mule.modules.caas.internal;

import org.mule.modules.caas.internal.security.encryption.MuleEncryptionException;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm;
import org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode;
import org.mule.runtime.core.api.util.Base64;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionAlgorithm.Blowfish;
import static org.mule.modules.caas.internal.security.encryption.binary.jce.algorithms.EncryptionMode.CBC;

public class SecurePropertyPlaceholderModule {
    private static final String FIPS_SECURITY_MODEL = "fips140-2";
    private static final String PROPERTY_SECURITY_MODEL = "mule.security.model";

    private static final String FIPS_MODE_MESSAGE = "You're running in FIPS mode " +
            " so please verify that the algorithm is compliant with FIPS.";

    /**
     * <p>The encryption algorithm used </p>
     * <p/>
     * <p>Allowed algorithms Blowfish(Default), AES, DES, DESede, RC2, RSA, PBEWithMD5AndDES</p>
     */
    private EncryptionAlgorithm encryptionAlgorithm = Blowfish;

    /**
     * <p>The encryption mode used </p>
     * <p/>
     * <p>Allowed modes CBC, CFB, ECB, OFB, PCBC</p>
     */
    private EncryptionMode encryptionMode = CBC;


    /**
     * <p>The decryption key </p>
     */
    private String key;

    private static boolean isFipsEnabled() {
        return FIPS_SECURITY_MODEL.equals(System.getProperty(PROPERTY_SECURITY_MODEL));
    }

    public byte[] decrypt(byte[] payload) throws MuleEncryptionException {
        try {

            return encryptionAlgorithm.getBuilder().using(encryptionMode).forKey(key).build().decrypt(payload);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    public String convertPropertyValue(String originalValue) {
        if (originalValue == null) {
            return null;
        }

        if (!(originalValue.startsWith("![") && originalValue.endsWith("]"))) {
            return originalValue;
        }
        String propertyKey = originalValue.substring(2, originalValue.length() - 1);

        try {
            return new String(decrypt(Base64.decode(propertyKey)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public EncryptionMode getEncryptionMode() {
        return encryptionMode;
    }

    public void setEncryptionMode(EncryptionMode encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String readEnvironmentalProperties(String text) {

        if (text == null) {
            return text;
        }

        Pattern propertyPatter = Pattern.compile("\\$\\{([^}]+)}");
        Matcher propertyMatcher = propertyPatter.matcher(text);
        String modifiedText = text;
        while (propertyMatcher.find()) {
            String property = propertyMatcher.group(1);
            modifiedText = replaceProperty(modifiedText, property);

        }
        return modifiedText;
    }

    private String replaceProperty(String modifiedText, String property) {
        String propertyValue = System.getProperty(property);
        checkForPropertyExistence(property, propertyValue);
        propertyValue = convertPropertyValue(propertyValue);
        String pattern = "\\$\\{(" + property + ")}";
        Pattern replacement = Pattern.compile(pattern);
        Matcher replacementMatcher = replacement.matcher(modifiedText);
        replacementMatcher.find();
        return replacementMatcher.replaceAll(Matcher.quoteReplacement(propertyValue));
    }


    public String resolveEnvProperties(String txt) {
        if (txt == null) {
            return txt;
        }
        if (txt.startsWith("${")) {
            return readEnvironmentalProperties(txt);
        } else {
            return System.getProperty(txt);
        }
    }

    private void checkForPropertyExistence(String property, String propertyValue) {
        if (propertyValue == null) {
            throw new RuntimeException("Property " + property + " could not be found");
        }
    }

}
