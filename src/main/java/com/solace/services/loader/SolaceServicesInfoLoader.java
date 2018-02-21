package com.solace.services.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.services.loader.model.SolaceServiceInfo;

import java.util.HashMap;

public class SolaceServicesInfoLoader {
    private ObjectMapper objectMapper = new ObjectMapper();
    private SolaceManifestLoader manifestLoader = new SolaceManifestLoader();

    /**
     * Fetches all the Solace services from the cloud environment's manifest.
     * @return A map of {@link SolaceServiceInfo} with keys of its {@link SolaceServiceInfo#getId() service IDs}.
     */
    public HashMap<String, SolaceServiceInfo> getAllSolaceServiceInfo() {
        String raw = manifestLoader.getManifest();
        SolaceServiceInfo[] serviceManifests = objectMapper.convertValue(raw, SolaceServiceInfo[].class);
        HashMap<String, SolaceServiceInfo> serviceInfos = new HashMap<>();
        for (SolaceServiceInfo serviceInfo : serviceManifests)
            serviceInfos.put(serviceInfo.getId(), serviceInfo);
        return serviceInfos;
    }

    /**
     * Gets a single Solace service from the cloud environment's manifest.
     * @param serviceId The ID of a Solace service.
     * @return The {@link SolaceServiceInfo} associated to the given ID.
     */
    public SolaceServiceInfo getSolaceServiceInfo(String serviceId) {
        return getAllSolaceServiceInfo().get(serviceId);
    }

    /**
     * Assumes that the loader is properly configured for the cloud environment.
     * @return True if a manifest was found in the environment.
     */
    public boolean manifestExists() {
        String manifest = manifestLoader.getManifest();
        return manifest != null && !manifest.isEmpty();
    }
}
