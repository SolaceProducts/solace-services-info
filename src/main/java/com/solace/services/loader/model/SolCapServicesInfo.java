package com.solace.services.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolCapServicesInfo {
    @JsonProperty("solace-messaging") private List<SolaceMessagingServiceInfo> solaceMessagingServices;

    public List<SolaceMessagingServiceInfo> getSolaceMessagingServices() {
        return solaceMessagingServices;
    }

    public void setSolaceMessagingServices(List<SolaceMessagingServiceInfo> solaceMessagingServices) {
        this.solaceMessagingServices = solaceMessagingServices;
    }
}
