package com.solace.services.core.loader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class VCAPServicesInfo {
    @JsonProperty("solace-pubsub") private List<SolaceMessagingServiceInfo> solaceMessagingServices;

    public List<SolaceMessagingServiceInfo> getSolaceMessagingServices() {
        return solaceMessagingServices;
    }

    public void setSolaceMessagingServices(List<SolaceMessagingServiceInfo> solaceMessagingServices) {
        this.solaceMessagingServices = solaceMessagingServices;
    }
}
