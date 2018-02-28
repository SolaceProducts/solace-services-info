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
        if (this.name != null && !this.name.isEmpty()) this.credentials.setId(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.credentials != null) this.credentials.setId(name);
    }
}
