package com.solace.services.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolaceMessagingServiceInfo {
    private SolaceServiceCredentials credentials;
    private String name;

    public SolaceServiceCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(SolaceServiceCredentials credentials) {
        this.credentials = credentials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
