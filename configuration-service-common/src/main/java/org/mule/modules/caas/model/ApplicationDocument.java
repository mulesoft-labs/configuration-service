package org.mule.modules.caas.model;

import java.io.Serializable;

public class ApplicationDocument implements Serializable {
    private final String contentType;
    private final String name;

    public ApplicationDocument(String contentType, String name) {
        this.contentType = contentType;
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }
}
