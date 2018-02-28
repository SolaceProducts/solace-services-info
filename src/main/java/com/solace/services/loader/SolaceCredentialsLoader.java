package com.solace.services.loader;

import com.fasterxml.jackson.databind.ObjectReader;
import com.solace.services.loader.model.SolCapServicesInfo;
import com.solace.services.loader.model.SolaceMessagingServiceInfo;
import com.solace.services.loader.model.SolaceServiceCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SolaceCredentialsLoader {
    private SolaceManifestLoader manifestLoader = new SolaceManifestLoader();

    private static final Logger logger = LogManager.getLogger(SolaceCredentialsLoader.class);
    private static final ObjectReader servicesInfoObjectReader = ObjectMapperUtil.getReader(SolCapServicesInfo.class);

    /**
     * Fetches all the Solace services from the cloud environment's manifest.
     * @return A map of {@link SolaceServiceCredentials#getId() service IDs} to {@link SolaceServiceCredentials}.
     */
    public Map<String, SolaceServiceCredentials> getAllSolaceServiceInfo() {
        String raw = manifestLoader.getManifest();
        if (raw == null || raw.isEmpty()) return new HashMap<>();

        SolCapServicesInfo solCapServices;
        try {
            solCapServices = servicesInfoObjectReader.readValue(raw);
        } catch (IOException e) {
            String msg = String.format("The provided services manifest does not have the expected format:\n%s", raw);
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        HashMap<String, SolaceServiceCredentials> solaceServiceInfos = new HashMap<>();
        if (solCapServices != null)
            for (SolaceMessagingServiceInfo smInfo : solCapServices.getSolaceMessagingServices())
                solaceServiceInfos.put(smInfo.getCredentials().getId(), smInfo.getCredentials());

        return solaceServiceInfos;
    }

    /**
     * Gets a single Solace service from the application environment's services manifest
     * @return A single {@link SolaceServiceCredentials}, null if none found.
     */
    public SolaceServiceCredentials getSolaceServiceInfo() {
        Iterator<Map.Entry<String, SolaceServiceCredentials>> iterator = getAllSolaceServiceInfo().entrySet().iterator();
        return iterator.hasNext() ? iterator.next().getValue() : null;
    }

    /**
     * Gets the specified Solace service from the application environment's services manifest.
     * @param serviceId The ID of a Solace service.
     * @return The {@link SolaceServiceCredentials} associated to the given ID, null if none found.
     */
    public SolaceServiceCredentials getSolaceServiceInfo(String serviceId) {
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
