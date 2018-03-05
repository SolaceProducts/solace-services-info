package com.solace.services.loader;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.solace.services.loader.SolaceManifestLoader.MANIFEST_FILE_NAME;
import static com.solace.services.loader.SolaceManifestLoader.SolaceEnv;
import static com.solace.services.loader.SolaceManifestLoader.ManifestSource;
import static com.solace.services.loader.SolaceManifestLoader.PostProcessor;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SolaceManifestLoaderTest {
    @Rule public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();
    @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Parameter(0) public String sourceName;
    @Parameter(1) public Set<Entry<ManifestSource, PostProcessor>> srcProperties;

    private SolaceManifestLoader manifestLoader;

    private static final String resourcesDir = "src/test/resources/";
    private static final Logger logger = LogManager.getLogger(SolaceManifestLoaderTest.class);
    private static String testManifest;
    private static List<Triple<SolaceEnv, ManifestSource, PostProcessor>> searchQueries;

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameterData() {
        searchQueries = new LinkedList<>();
//        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_CREDENTIALS, ManifestSource.JVM, PostProcessor.REST));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLCAP_SERVICES, ManifestSource.JVM, PostProcessor.NONE));
//        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_CREDENTIALS, ManifestSource.ENV, PostProcessor.REST));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLCAP_SERVICES, ManifestSource.ENV, PostProcessor.NONE));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_SERVICES_HOME, ManifestSource.JVM, PostProcessor.FILE));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_SERVICES_HOME, ManifestSource.ENV, PostProcessor.FILE));

        // -- Collect Properties Per Source Name --
        HashMap<String, Set<Entry<ManifestSource, PostProcessor>>> invertedQueries = new HashMap<>();
        for (Triple<SolaceEnv, ManifestSource, PostProcessor> entry : searchQueries) {
            String solaceEnv = entry.getLeft().name();
            if (!invertedQueries.containsKey(solaceEnv))
                invertedQueries.put(solaceEnv, new HashSet<Entry<ManifestSource, PostProcessor>>());
            invertedQueries.get(solaceEnv).add(new SimpleEntry<>(entry.getMiddle(), entry.getRight()));
        }

        // -- Setup JUnit Parameters --
        Set<Object[]> parameters = new HashSet<>();
        for (Entry<String, Set<Entry<ManifestSource, PostProcessor>>> entry : invertedQueries.entrySet())
            parameters.add(new Object[]{entry.getKey(), entry.getValue()});

        return parameters;
    }

    @BeforeClass
    public static void setupTestServiceManifest() throws IOException {
        String credentialsPath = resourcesDir.concat("test-service-credentials.json.template");
        String servicesPath = resourcesDir.concat("test-services-manifest.json.template");
        testManifest = String.format(
                new String(Files.readAllBytes(Paths.get(servicesPath))),
                new String(Files.readAllBytes(Paths.get(credentialsPath))));
    }

    @Before
    public void setup() {
        manifestLoader = new SolaceManifestLoader(searchQueries);

        // Ensures that any REAL property/environment variables are cleared before executing the test
        for (SolaceEnv env : SolaceEnv.values()) {
            System.clearProperty(env.name());
            environmentVariables.clear(env.name());
        }
    }

    @Test
    public void testBlankSources() {
        assertNull(manifestLoader.getManifest());
    }

    @Test
    public void testJvm() {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.JVM, PostProcessor.NONE);
        assumeTrue("Not a JVM query", srcProperties.contains(validTestProps));

        logger.info(String.format("Testing JVM Property %s ", sourceName));

        System.setProperty(sourceName, testManifest);
        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), testManifest);
    }

    @Test
    public void testEnv() {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.ENV, PostProcessor.NONE);
        assumeTrue("Not an ENV query", srcProperties.contains(validTestProps));
        logger.info(String.format("Testing OS Environment %s ", sourceName));

        environmentVariables.set(sourceName, testManifest);
        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), testManifest);
    }

    @Test
    public void testUserHomeFallback() throws IOException {
        assumeTrue("Not a FILE query", srcProperties.containsAll(Arrays.asList(
                new SimpleEntry<>(ManifestSource.JVM, PostProcessor.FILE),
                new SimpleEntry<>(ManifestSource.ENV, PostProcessor.FILE))));

        logger.info(String.format("Testing user home fallback for %s", sourceName));
        System.setProperty("user.home", tmpFolder.getRoot().getAbsolutePath());
        generateTestFile(MANIFEST_FILE_NAME, testManifest);

        assertEquals(manifestLoader.getManifest(), testManifest);

        String manifestMod = "abc";
        logger.info(String.format("Appending %s to the manifest.", manifestMod));
        String newTestManifest = testManifest.concat(manifestMod);
        assertNotEquals("TEST ERROR: The manifest string wasn't modified...", newTestManifest, testManifest);
        generateTestFile(MANIFEST_FILE_NAME, newTestManifest);

        logger.info(String.format("Testing user home fallback for %s with modified manifest", sourceName));
        assertEquals(manifestLoader.getManifest(), newTestManifest);
    }

    @Test
    public void testJvmFile() throws IOException {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.JVM, PostProcessor.FILE);
        assumeTrue("Not a JVM-FILE query", srcProperties.contains(validTestProps));

        logger.info(String.format("Testing JVM Property %s ", sourceName));
        System.setProperty(sourceName, generateTestFile(MANIFEST_FILE_NAME, testManifest));

        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), testManifest);

        String manifestMod = "abc";
        logger.info(String.format("Appending %s to the manifest.", manifestMod));
        String newTestManifest = testManifest.concat(manifestMod);
        assertNotEquals("TEST ERROR: The manifest string wasn't modified...", newTestManifest, testManifest);
        generateTestFile(MANIFEST_FILE_NAME, newTestManifest);

        logger.info(String.format("Testing JVM Property %s with modified manifest", sourceName));
        assertNotNull(System.getProperty(sourceName));
        assertEquals(manifestLoader.getManifest(), newTestManifest);
    }

    @Test
    public void testEnvFile() throws IOException {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.ENV, PostProcessor.FILE);
        assumeTrue("Not an ENV-FILE query", srcProperties.contains(validTestProps));

        logger.info(String.format("Testing OS Environment %s ", sourceName));
        environmentVariables.set(sourceName, generateTestFile(MANIFEST_FILE_NAME, testManifest));

        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), testManifest);

        String manifestMod = "abc";
        logger.info(String.format("Appending %s to the manifest.", manifestMod));
        String newTestManifest = testManifest.concat(manifestMod);
        assertNotEquals("TEST ERROR: The manifest string wasn't modified...",
                newTestManifest, testManifest);
        generateTestFile(MANIFEST_FILE_NAME, newTestManifest);

        logger.info(String.format("Testing OS Environment %s with modified manifest", sourceName));
        assertNotNull(System.getenv(sourceName));
        assertEquals(manifestLoader.getManifest(), newTestManifest);
    }

    @Test
    public void testJvmFileNotExist() {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.JVM, PostProcessor.FILE);
        assumeTrue("Not a JVM-FILE query", srcProperties.contains(validTestProps));

        logger.info(String.format("Testing JVM Property %s ", sourceName));
        System.setProperty(sourceName, tmpFolder.getRoot().getAbsolutePath());
        assertNotNull(System.getProperty(sourceName));
        assertNull(manifestLoader.getManifest());
    }

    @Test
    public void testEnvFileNotExist() {
        Entry<ManifestSource, PostProcessor> validTestProps = new SimpleEntry<>(ManifestSource.ENV, PostProcessor.FILE);
        assumeTrue("Not an ENV-FILE query", srcProperties.contains(validTestProps));

        logger.info(String.format("Testing OS Environment %s ", sourceName));
        environmentVariables.set(sourceName, tmpFolder.getRoot().getAbsolutePath());
        assertNotNull(System.getenv(sourceName));
        assertNull(manifestLoader.getManifest());
    }

    @Test
    public void testPropertySourceHierarchy() throws IOException {
        for (Entry<ManifestSource, PostProcessor> props : srcProperties) {
            Triple<SolaceEnv, ManifestSource, PostProcessor> query =
                    new ImmutableTriple<>(SolaceEnv.valueOf(sourceName), props.getKey(), props.getValue());

            logger.info(String.format("Testing hierarchy of %s", query));

            String fakeVal = setupFakeJVMPropsAndOSEnvs();
            String valToWrite = testManifest;

            switch (props.getValue()) {
                case FILE: valToWrite = generateTestFile(MANIFEST_FILE_NAME, testManifest); break;
                case REST: break; //TODO
            }

            switch (props.getKey()) {
                case JVM: System.setProperty(sourceName, valToWrite); break;
                case ENV: environmentVariables.set(sourceName, valToWrite); break;
            }

            List<Triple<SolaceEnv, ManifestSource, PostProcessor>> testQueries = new LinkedList<>(searchQueries);
            boolean testedQuery = false;

            while (!testQueries.isEmpty()) {
                manifestLoader.setSearchQueries(testQueries);
                Triple<SolaceEnv, ManifestSource, PostProcessor> firstQuery = testQueries.get(0);
                logger.info(String.format("Top of loader's search stack: %s", firstQuery));

                String output = manifestLoader.getManifest();
                if (firstQuery.equals(query)) {
                    assertNotNull(output);
                    assertEquals(testManifest, output);
                    testedQuery = true;
                } else if (!firstQuery.getRight().equals(PostProcessor.NONE)) {
                    // Capture post-processor failure scenarios since they would safely fall through to the next query
                    if (testedQuery) assertTrue("A non-null and non-fake value was returned",
                            output == null || output.equals(fakeVal));
                    else assertTrue("A value that is neither the test manifest nor the fake value was returned",
                            output.equals(testManifest) || output.equals(fakeVal));
                } else {
                    assertNotNull(output);
                    assertEquals("A non-fake value was returned", fakeVal, output);
                }
                testQueries.remove(0);
            }
        }
    }

    private String generateTestFile(String name, String contents) throws IOException {
        String dirPath = tmpFolder.getRoot().getAbsolutePath();
        String filePath = dirPath.concat(File.separator).concat(name);
        File manifestFile = Files.exists(Paths.get(filePath)) ? new File(filePath) : tmpFolder.newFile(name);
        Files.write(manifestFile.toPath(), contents.getBytes());
        return dirPath;
    }

    private String setupFakeJVMPropsAndOSEnvs() {
        // No guarantee that any of the set props and env variables will have valid values
        // No guarantee that all of the set props and env variables will be valid queries
        logger.info("Will setup fake JVM properties and OS environments:");
        String test = "TESTING_1_2_3!";
        for (SolaceEnv solaceEnv : SolaceEnv.values()) {
            logger.info(String.format("\t%s: \"%s\"", solaceEnv.name(), test));
            System.setProperty(solaceEnv.name(), test);
            environmentVariables.set(solaceEnv.name(), test);
        }
        return test;
    }
}
