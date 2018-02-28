package com.solace.services.loader;

import com.solace.services.loader.model.SolCapServicesInfo;
import com.solace.services.loader.model.SolaceMessagingServiceInfo;
import com.solace.services.loader.model.SolaceServiceCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SolaceCredentialsLoaderTest {
    private static final Logger logger = LogManager.getLogger(SolaceCredentialsLoaderTest.class);
    private static final String resourcesDir = "src/test/resources/";
    private static List<SolaceServiceCredentials> testSSIs = new ArrayList<>();
    private static String testServiceManifest;

    @Mock private SolaceManifestLoader manifestLoader;
    @InjectMocks private SolaceCredentialsLoader ssiLoader;

    @BeforeClass
    public static void setupTestServiceManifest() throws IOException {
        String path = resourcesDir.concat("test-service-manifest.json");
        testServiceManifest = new String(Files.readAllBytes(Paths.get(path)));
        SolCapServicesInfo svcs = ObjectMapperUtil.getReader(SolCapServicesInfo.class).readValue(testServiceManifest);

        for (SolaceMessagingServiceInfo smInfo : svcs.getSolaceMessagingServices())
            testSSIs.add(smInfo.getCredentials());
    }

    @Before
    public void setupMockito() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(manifestLoader.getManifest()).thenReturn(testServiceManifest);
    }

    @Test
    public void testNoManifest() {
        Mockito.when(manifestLoader.getManifest()).thenReturn(null);
        assertTrue(ssiLoader.getAllSolaceServiceInfo().isEmpty());
        assertNull(ssiLoader.getSolaceServiceInfo());
    }

    @Test
    public void testGetAllSolaceServiceInfo() {
        assertEquals(new HashSet<>(testSSIs), new HashSet<>(ssiLoader.getAllSolaceServiceInfo().values()));
    }

    @Test
    public void testGetSolaceServiceInfo() {
        assertEquals(testSSIs.get(0), ssiLoader.getSolaceServiceInfo());
        SolaceServiceCredentials ssi = testSSIs.get(0);
        assertEquals(ssi, ssiLoader.getSolaceServiceInfo(ssi.getId()));
    }

    @Test
    public void testManifestExists() {
        assertTrue(ssiLoader.manifestExists());
    }
}
