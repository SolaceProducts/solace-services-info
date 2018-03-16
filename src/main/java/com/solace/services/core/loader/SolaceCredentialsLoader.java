package com.solace.services.core.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.solace.services.core.model.SolaceServiceCredentials;
import com.solace.services.core.model.SolaceServiceCredentialsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Reads Solace service credentials from one of the property sources defined in {@link SolaceManifestLoader}.</p>
 *
 * <p>The manifest can take on one of the following forms:</p>
 *
 * <table summary="Valid manifest structures along with ID generation logic for each format.">
 *     <tr><th>Manifest Format</th><th>Manifest Detection Handle</th><th>Default Solace-Messaging Service ID</th></tr>
 *     <tr>
 *         <td>VCAP-Formatted Map of Services</td>
 *         <td> An object-type root node with key "solace-messaging".</td>
 *         <td>The meta-name of the service, otherwise an '@'-delimited concatenation of
 *              the {@link SolaceServiceCredentials#getMsgVpnName() VPN name} and
 *              the {@link SolaceServiceCredentials#getActiveManagementHostname() active management hostname}.</td>
 *     </tr>
 *     <tr>
 *         <td>List of Service Credentials</td>
 *         <td>An array-type root node.</td>
 *         <td>An '@'-delimited concatenation of
 *              the {@link SolaceServiceCredentials#getMsgVpnName() VPN name} and
 *              the {@link SolaceServiceCredentials#getActiveManagementHostname() active management hostname}.</td>
 *     </tr>
 *     <tr>
 *         <td>Single-Service Credentials</td>
 *         <td>Default.</td>
 *         <td>An '@'-delimited concatenation of
 *              the {@link SolaceServiceCredentials#getMsgVpnName() VPN name} and
 *              the {@link SolaceServiceCredentials#getActiveManagementHostname() active management hostname}.</td>
 *     </tr>
 * </table>
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
        servicesReader = objectMapper.readerFor(VCAPServicesInfo.class);
        credsListReader = objectMapper.readerFor(new TypeReference<List<SolaceServiceCredentialsImpl>>(){});
        credReader = objectMapper.readerFor(SolaceServiceCredentialsImpl.class);
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
        List<SolaceServiceCredentialsImpl> svcsCreds = new LinkedList<>();
        JsonNode node = defaultReader.readTree(raw);

        if (node.isObject() && node.has(SOLACE_MESSAGING_SVC_NAME)) {
            VCAPServicesInfo services = servicesReader.readValue(raw);
            for (SolaceMessagingServiceInfo serviceInfo : services.getSolaceMessagingServices()) {
                SolaceServiceCredentialsImpl svcCreds = serviceInfo.getCredentials();
                svcCreds.setId(getServiceId(serviceInfo));
                svcsCreds.add(svcCreds);
            }
        } else if (node.isArray()) {
            svcsCreds = credsListReader.readValue(raw);
        } else {
            svcsCreds.add((SolaceServiceCredentialsImpl) credReader.readValue(raw));
        }

        for (SolaceServiceCredentialsImpl svcCreds : svcsCreds) svcCreds.setId(getServiceId(svcCreds));
        return new LinkedList<SolaceServiceCredentials>(svcsCreds);
    }

    private String getServiceId(SolaceMessagingServiceInfo solaceMessagingServiceInfo) {
        // Default: Service's meta-name
        String id = solaceMessagingServiceInfo.getCredentials().getId();
        return id != null && !id.isEmpty() ? id : solaceMessagingServiceInfo.getName();
    }

    private String getServiceId(SolaceServiceCredentials solaceServiceCredentials) {
        // Default: '@'-delimited concatenation of the service's VPN name and active management host name
        String id = solaceServiceCredentials.getId();
        String msgVpnName = solaceServiceCredentials.getMsgVpnName();
        String activeManagementHostname = solaceServiceCredentials.getActiveManagementHostname();
        return id != null && !id.isEmpty() ? id : msgVpnName+'@'+activeManagementHostname;
    }
}
