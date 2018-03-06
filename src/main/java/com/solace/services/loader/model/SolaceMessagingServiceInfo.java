package com.solace.services.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolaceMessagingServiceInfo {
    private SolaceServiceCredentialsImpl credentials;
    private String name;

    public SolaceServiceCredentialsImpl getCredentials() {
        return credentials;
    }

    public void setCredentials(SolaceServiceCredentialsImpl credentials) {
        this.credentials = credentials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
