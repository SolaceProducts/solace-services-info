package com.solace.services.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.solace.services.loader.model.SolCapServicesInfo;
import com.solace.services.loader.model.SolaceMessagingServiceInfo;
import com.solace.services.loader.model.SolaceServiceCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Reads solace credentials from one of the property sources defined in the {@link SolaceManifestLoader}.</p>
 *
 * <p>The manifest can take on one of the following forms:</p>
 * <ul>
 *     <li><i>VCAP-formatted map of services:</i> An object-type root node with key "solace-messaging".</li>
 *     <li><i>List of credentials:</i> An array-type root node.</li>
 *     <li><i>Single credential:</i> Default.</li>
 * <ul/>
 */
public class SolaceCredentialsLoader {
    private SolaceManifestLoader manifestLoader = new SolaceManifestLoader();

    private static final Logger logger = LogManager.getLogger(SolaceCredentialsLoader.class);
    private static final ObjectReader defaultReader;
    private static final ObjectReader servicesReader;
    private static final ObjectReader credsListReader;
    private static final ObjectReader credReader;
    private static final String SOLACE_MESSAGING_SVC_NAME = "solace-messaging";

    static {
        ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
        defaultReader = objectMapper.reader();
        servicesReader = objectMapper.readerFor(SolCapServicesInfo.class);
        credsListReader = objectMapper.readerFor(new TypeReference<List<SolaceServiceCredentials>>(){});
        credReader = objectMapper.readerFor(SolaceServiceCredentials.class);
    }

    /**
     * Fetches all the Solace services from the cloud environment's manifest.
     * @return A map of {@link SolaceServiceCredentials#getId() service IDs} to {@link SolaceServiceCredentials}.
     */
    public Map<String, SolaceServiceCredentials> getAllSolaceServiceInfo() {
        String raw = manifestLoader.getManifest();
        if (raw == null || raw.isEmpty()) return new HashMap<>();

        Map<String, SolaceServiceCredentials> svcsCreds = new HashMap<>();
        try {
            for (SolaceServiceCredentials creds : getServicesCredentials(raw)) svcsCreds.put(creds.getId(), creds);
        } catch (IOException e) {
            String msg = String.format("The provided services manifest does not have the expected format:\n%s", raw);
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return svcsCreds;
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

    private List<SolaceServiceCredentials> getServicesCredentials(String raw) throws IOException {
        List<SolaceServiceCredentials> creds = new LinkedList<>();
        JsonNode node = defaultReader.readTree(raw);

        if (node.isObject() && node.has(SOLACE_MESSAGING_SVC_NAME)) {
            SolCapServicesInfo services = servicesReader.readValue(raw);
            for (SolaceMessagingServiceInfo serviceInfo : services.getSolaceMessagingServices())
                creds.add(serviceInfo.getCredentials());
        } else if (node.isArray()) {
            creds = credsListReader.readValue(raw);
        } else {
            SolaceServiceCredentials cred = credReader.readValue(raw);
            creds.add(cred);
        }

        return creds;
    }
}
