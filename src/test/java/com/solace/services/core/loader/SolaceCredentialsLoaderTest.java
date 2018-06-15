package com.solace.services.core.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.services.core.model.SolaceServiceCredentials;
import com.solace.services.core.model.SolaceServiceCredentialsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SolaceCredentialsLoaderTest {
    private static final Logger logger = LogManager.getLogger(SolaceCredentialsLoaderTest.class);
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    private static final String resourcesDir = "src/test/resources/";

    @Parameter(0) public String testManifestFormatAlias;
    @Parameter(1) public String testManifest;
    @Parameter(2) public List<SolaceServiceCredentials> testSSCs;
    @Mock private SolaceManifestLoader manifestLoader;
    @InjectMocks private SolaceCredentialsLoader sscLoader;

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameterData() throws IOException {
        // Run tests with two types of service labels: solace-messaging and solace-pubsub
        List<String> servicePaths = new ArrayList<>();
        servicePaths.add(resourcesDir.concat("test-solace-messaging-services-manifest.json.template"));
        servicePaths.add(resourcesDir.concat("test-solace-pubsub-services-manifest.json.template"));

        String credsPath = resourcesDir.concat("test-service-credentials.json.template");

        // -- Setup JUnit Parameters --
        Set<Object[]> parameters = new HashSet<>();

        for (String servicesPath : servicePaths) {

            String serviceLabel = servicesPath.contains("pubsub") ? "pubsub" : "messaging";

            // -- Setup Test Manifests --
            String testCreds = new String(Files.readAllBytes(Paths.get(credsPath)));
            String testCredsWithID = createManifestWithCredentialsID(testCreds);

            String testCredsList = String.format("[%s]", testCreds);
            String testCredsListWithID = String.format("[%s]", testCredsWithID);

            String testVCAP = String.format(new String(Files.readAllBytes(Paths.get(servicesPath))), testCreds);
            String testVCAPWithID = createManifestWithCredentialsID(testVCAP);
            String testVCAPWithoutMetaName = testVCAP.replaceAll("\"name\"\\s*?:.*?,", "");

            // -- Setup Test Objects --
            List<SolaceServiceCredentials> credsList = createTestCredsList(createTestCreds(testCreds));
            List<SolaceServiceCredentials> credsListWithIDs = createTestCredsList(createTestCreds(testCredsWithID));

            List<SolaceServiceCredentials> testVCAPCreds = createTestVCAPCreds(testVCAP);
            List<SolaceServiceCredentials> testVCAPCredsWithID = createTestVCAPCreds(testVCAPWithID);
            List<SolaceServiceCredentials> testVCAPCredsWithoutMetaName = createTestVCAPCreds(testVCAPWithoutMetaName);

            parameters.add(new Object[]{serviceLabel + "-VCAP-Manifest", testVCAP, testVCAPCreds});
            parameters.add(new Object[]{serviceLabel + "-VCAP-Manifest With Predefined ID", testVCAPWithID, testVCAPCredsWithID});
            parameters.add(new Object[]{serviceLabel + "-VCAP-Manifest Without Meta Name", testVCAPWithoutMetaName, testVCAPCredsWithoutMetaName});
            parameters.add(new Object[]{serviceLabel + "-Multi-Service Credentials List", testCredsList, credsList});
            parameters.add(new Object[]{serviceLabel + "-Multi-Service Credentials List With Predefined ID", testCredsListWithID, credsListWithIDs});
            parameters.add(new Object[]{serviceLabel + "-Single-Service Credentials", testCreds, credsList});
            parameters.add(new Object[]{serviceLabel + "-Single-Service Credentials With Predefined ID", testCredsWithID, credsListWithIDs});
        }
        return parameters;
    }

    @Before
    public void setupMockito() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(manifestLoader.getManifest()).thenReturn(testManifest);
    }

    @Test
    public void testNoManifest() {
        Mockito.when(manifestLoader.getManifest()).thenReturn(null);
        assertTrue(sscLoader.getAllSolaceServiceInfo().isEmpty());
        assertNull(sscLoader.getSolaceServiceInfo());
    }

    @Test
    public void testEmptyManifest() {
        Mockito.when(manifestLoader.getManifest()).thenReturn("");
        assertTrue(sscLoader.getAllSolaceServiceInfo().isEmpty());
        assertNull(sscLoader.getSolaceServiceInfo());
    }

    @Test
    public void testGetAllSolaceServiceInfo() {
        assertEquals(new HashSet<>(testSSCs), new HashSet<>(sscLoader.getAllSolaceServiceInfo().values()));
    }

    @Test
    public void testGetSolaceServiceInfo() {
        assertEquals(testSSCs.get(0), sscLoader.getSolaceServiceInfo());
        SolaceServiceCredentials ssc = testSSCs.get(0);
        assertEquals(ssc, sscLoader.getSolaceServiceInfo(ssc.getId()));
    }

    @Test
    public void testManifestExists() {
        assertTrue(sscLoader.manifestExists());
    }

    private static List<SolaceServiceCredentials> createTestVCAPCreds(String vcapManifest) throws IOException {
        VCAPServicesInfo services = objectMapper.readerFor(VCAPServicesInfo.class).readValue(vcapManifest);
        List<SolaceServiceCredentials> testVCAPCreds = new ArrayList<>();
        for (SolaceMessagingServiceInfo smInfo : services.getSolaceMessagingServices()) {
            SolaceServiceCredentialsImpl sCreds = smInfo.getCredentials();
            if (sCreds.getId() == null || sCreds.getId().isEmpty()) sCreds.setId(getDefaultServiceID(smInfo));
            testVCAPCreds.add(sCreds);
        }
        return testVCAPCreds;
    }

    private static SolaceServiceCredentials createTestCreds(String singleCredsManifest) throws IOException {
        SolaceServiceCredentialsImpl oneCreds = objectMapper.readerFor(SolaceServiceCredentialsImpl.class)
                .readValue(singleCredsManifest);
        if (oneCreds.getId() == null || oneCreds.getId().isEmpty()) oneCreds.setId(getDefaultServiceID(oneCreds));
        return oneCreds;
    }

    private static String getDefaultServiceID(SolaceMessagingServiceInfo smInfo) {
        if (smInfo.getName() != null && !smInfo.getName().isEmpty()) return smInfo.getName();
        else return getDefaultServiceID(smInfo.getCredentials());
    }

    private static String getDefaultServiceID(SolaceServiceCredentialsImpl solaceServiceCredentials) {
        return solaceServiceCredentials.getMsgVpnName() + '@' + solaceServiceCredentials.getActiveManagementHostname();
    }

    private static List<SolaceServiceCredentials> createTestCredsList(SolaceServiceCredentials... creds) {
        return new LinkedList<>(Arrays.asList(creds));
    }

    private static String createManifestWithCredentialsID(String manifest) {
        String testId = "test-id";
        return manifest.replaceAll("\n", " ")
                .replaceAll("\\s+", " ")
                .replaceFirst("(\"msgVpnName\"\\s*?:.*?,)", String.format("$1 \"id\": \"%s\",", testId));
    }
}
