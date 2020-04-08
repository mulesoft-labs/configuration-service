package org.mule.modules.caas.internal.security.encryption;

public class MuleEncryptionException extends Exception{

    public MuleEncryptionException(String s) {
        super(s);
    }

    public MuleEncryptionException(String s, Exception e) {
        super(s,e);
    }
}