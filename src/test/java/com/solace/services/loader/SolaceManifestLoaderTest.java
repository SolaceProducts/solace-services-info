package com.solace.services.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.solace.services.loader.SolaceManifestLoader.MANIFEST_FILE_NAME;
import static com.solace.services.loader.SolaceManifestLoader.SOLACE_SERVICES_HOME;
import static com.solace.services.loader.SolaceManifestLoader.SOLCAP_SERVICES;
import static com.solace.services.loader.SolaceManifestLoader.ManifestSource;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SolaceManifestLoaderTest {
    @Rule public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();
    @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Parameter(0) public String sourceName;
    @Parameter(1) public Set<ManifestSource> manifestSource;

    private SolaceManifestLoader manifestLoader;

    private static final String resourcesDir = "src/test/resources/";
    private static final Logger logger = LogManager.getLogger(SolaceManifestLoaderTest.class);
    private static String testServiceManifest;
    private static List<SimpleEntry<SolaceManifestLoader.ManifestSource, String>> searchesQueries;

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameterData() {
        searchesQueries = new LinkedList<>();
        searchesQueries.add(new SimpleEntry<>(ManifestSource.JVM, SOLCAP_SERVICES));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.ENV, SOLCAP_SERVICES));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.FILE, SOLACE_SERVICES_HOME));

        HashMap<String, Set<ManifestSource>> envPropertySources = new HashMap<>();
        for (Map.Entry<ManifestSource, String> entry : searchesQueries) {
            if (!envPropertySources.containsKey(entry.getValue()))
                envPropertySources.put(entry.getValue(), new HashSet<ManifestSource>());
            envPropertySources.get(entry.getValue()).add(entry.getKey());
        }

        Set<Object[]> parameters = new HashSet<>();
        for (Map.Entry<String, Set<ManifestSource>> entry : envPropertySources.entrySet())
            parameters.add(new Object[]{entry.getKey(), entry.getValue()});

        return parameters;
    }

    @BeforeClass
    public static void setupTestServiceManifest() throws IOException {
        String path = resourcesDir.concat("test-service-manifest.json");
        testServiceManifest = new String(Files.readAllBytes(Paths.get(path)));
    }

    @Before
    public void setupLoader() {
        manifestLoader = new SolaceManifestLoader(searchesQueries);
    }

    @Test
    public void testBlankSources() {
        assertNull(manifestLoader.getManifest());
    }

    @Test
    public void testJvmSolo() {
        assumeTrue(manifestSource.contains(ManifestSource.JVM));
        logger.info(String.format("Testing JVM Property %s ", sourceName));

        System.setProperty(sourceName, testServiceManifest);
        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), testServiceManifest);
    }

    @Test
    public void testEnvSolo() {
        assumeTrue(manifestSource.contains(ManifestSource.ENV));
        logger.info(String.format("Testing OS Environment %s ", sourceName));

        environmentVariables.set(sourceName, testServiceManifest);
        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), testServiceManifest);
    }

    @Test
    public void testFileSolo() throws IOException {
        assumeTrue(manifestSource.contains(ManifestSource.FILE));
        String dirPath = tmpFolder.getRoot().getAbsolutePath();

        File manifestFile = tmpFolder.newFile(MANIFEST_FILE_NAME);
        Files.write(manifestFile.toPath(), testServiceManifest.getBytes());

        logger.info(String.format("Testing JVM Property %s ", sourceName));
        System.setProperty(sourceName, dirPath);
        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), testServiceManifest);
        logger.info(String.format("Cleared JVM Property %s ", sourceName));
        System.clearProperty(sourceName);

        logger.info(String.format("Testing OS Environment %s ", sourceName));
        environmentVariables.set(sourceName, dirPath);
        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), testServiceManifest);
        logger.info(String.format("Cleared OS Environment %s ", sourceName));
        environmentVariables.clear(sourceName);

        String manifestMod = "abc";
        logger.info(String.format("Appending %s to the manifest.", manifestMod));
        String newTestManifest = testServiceManifest.concat(manifestMod);
        assertNotEquals("TEST ERROR: The manifest string wasn't modified...",
                newTestManifest, testServiceManifest);
        Files.write(manifestFile.toPath(), newTestManifest.getBytes());

        logger.info(String.format("Testing JVM Property %s with modified manifest", sourceName));
        System.setProperty(sourceName, dirPath);
        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), newTestManifest);
        logger.info(String.format("Cleared JVM Property %s ", sourceName));
        System.clearProperty(sourceName);

        logger.info(String.format("Testing OS Environment %s with modified manifest", sourceName));
        environmentVariables.set(sourceName, dirPath);
        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), newTestManifest);
        logger.info(String.format("Cleared OS Environment %s ", sourceName));
        environmentVariables.clear(sourceName);
    }

    @Test
    public void testFileNotExist() {
        assumeTrue(manifestSource.contains(ManifestSource.FILE));
        String dirPath = tmpFolder.getRoot().getAbsolutePath();

        logger.info(String.format("Testing JVM Property %s ", sourceName));
        System.setProperty(sourceName, dirPath);
        assertNotNull(System.getProperty(sourceName));
        assertNull(manifestLoader.getManifest());
        logger.info(String.format("Cleared JVM Property %s ", sourceName));
        System.clearProperty(sourceName);

        logger.info(String.format("Testing OS Environment %s ", sourceName));
        environmentVariables.set(sourceName, dirPath);
        assertNotNull(System.getenv(sourceName));
        assertNull(manifestLoader.getManifest());
        logger.info(String.format("Cleared OS Environment %s ", sourceName));
        environmentVariables.clear(sourceName);
    }

    @Test
    @Ignore
    public void testPropertySourceHierarchy() { //TODO
    }
}
