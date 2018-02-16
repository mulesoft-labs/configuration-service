package org.mule.modules.caas;

public abstract class ConfigurationServiceException extends Exception {
    public ConfigurationServiceException(String message) {
        super(message);
    }

    public ConfigurationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationServiceException(Throwable cause) {
        super(cause);
    }

    public ConfigurationServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
