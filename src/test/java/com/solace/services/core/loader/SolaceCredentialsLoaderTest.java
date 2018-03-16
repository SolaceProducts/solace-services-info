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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        String credsPath = resourcesDir.concat("test-service-credentials.json.template");
        String servicesPath = resourcesDir.concat("test-services-manifest.json.template");

        String testCreds = new String(Files.readAllBytes(Paths.get(credsPath)));
        String testCredsList = String.format("[%s]", testCreds);
        String testCAP = String.format(new String(Files.readAllBytes(Paths.get(servicesPath))), testCreds);

        // -- Setup Test Objects --
        VCAPServicesInfo services = objectMapper.readerFor(VCAPServicesInfo.class).readValue(testCAP);
        SolaceServiceCredentialsImpl oneCreds = objectMapper.readerFor(SolaceServiceCredentialsImpl.class).readValue(testCreds);
        List<SolaceServiceCredentialsImpl> credsList = new LinkedList<>();
        credsList.add(oneCreds);

        List<SolaceServiceCredentials> testCAPCreds = new ArrayList<>();
        for (SolaceMessagingServiceInfo smInfo : services.getSolaceMessagingServices()) {
            SolaceServiceCredentialsImpl sCreds = smInfo.getCredentials();
            sCreds.setId(smInfo.getName());
            testCAPCreds.add(sCreds);
        }

        for (SolaceServiceCredentialsImpl creds : credsList)
            creds.setId(creds.getMsgVpnName() + '@' + creds.getActiveManagementHostname());

        // -- Setup JUnit Parameters --
        Set<Object[]> parameters = new HashSet<>();
        parameters.add(new Object[] {"CAP-Manifest", testCAP, testCAPCreds});
        parameters.add(new Object[] {"Multi-Service Credentials List", testCredsList, credsList});
        parameters.add(new Object[] {"Single-Service Credentials", testCreds, credsList});
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

    @Test
    public void testPredefinedServiceId() {
        String testId = "test-id";
        String testManifestWithId = testManifest
                .replaceAll("\n", " ")
                .replaceAll("\\s+", " ")
                .replaceFirst("(\"msgVpnName\"\\s*?:.*?,)", String.format("$1 \"id\": \"%s\",", testId));

        Mockito.when(manifestLoader.getManifest()).thenReturn(testManifestWithId);

        assertTrue(String.format("No service found with predefined test ID of %s", testId),
                sscLoader.getAllSolaceServiceInfo().containsKey(testId));

        assertNotNull(String.format("No service found with predefined test ID of %s", testId),
                sscLoader.getSolaceServiceInfo(testId));
    }
}
