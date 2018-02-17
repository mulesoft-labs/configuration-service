package org.mule.modules.caas.api;

import java.io.Serializable;

public class ReadDocumentAttributes implements Serializable {

    private final String key;
    private final String contentType;

    public ReadDocumentAttributes(String key, String contentType) {
        this.key = key;
        this.contentType = contentType;
    }

    public String getKey() {
        return key;
    }

    public String getContentType() {
        return contentType;
    }
}
