package com.solace.services.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

class SolaceManifestLoader {
    enum ManifestSource {JVM, ENV, FILE}
    enum SolaceEnv {SOLACE_CREDENTIALS, SOLCAP_SERVICES, SOLACE_SERVICES_HOME}

    static final String MANIFEST_FILE_NAME = ".solaceservices";
    private static final Logger logger = LogManager.getLogger(SolaceManifestLoader.class);

    private List<SimpleEntry<ManifestSource, SolaceEnv>> searchesQueries;

    public SolaceManifestLoader() {
        searchesQueries = new LinkedList<>();
        searchesQueries.add(new SimpleEntry<>(ManifestSource.JVM, SolaceEnv.SOLACE_CREDENTIALS));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.JVM, SolaceEnv.SOLCAP_SERVICES));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.ENV, SolaceEnv.SOLACE_CREDENTIALS));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.ENV, SolaceEnv.SOLCAP_SERVICES));
        searchesQueries.add(new SimpleEntry<>(ManifestSource.FILE, SolaceEnv.SOLACE_SERVICES_HOME));
    }

    // For Testing
    SolaceManifestLoader(List<SimpleEntry<ManifestSource, SolaceEnv>> searchesQueries) {
        this.searchesQueries = searchesQueries;
    }

    /**
     * Finds and loads a manifest from the application's environment as per the precedence defined in the search queries.
     * The manifest contents are retrieved <emphasis>as is</emphasis> and is not checked for validity.
     * @return A JSON string representing a service manifest, null if not found.
     */
    public String getManifest() {
        String content = null;
        for (SimpleEntry<ManifestSource, SolaceEnv> searchQuery : searchesQueries) {
            String sourceName = searchQuery.getValue().name();
            switch (searchQuery.getKey()) {
                case JVM: content = System.getProperty(sourceName, null); break;
                case ENV: content = System.getenv(sourceName); break;
                case FILE: content = readFile(getPathFromJvmOrEnv(sourceName), MANIFEST_FILE_NAME); break;
            }

            if (content!= null && !content.isEmpty()) { // Content mutation
                switch (searchQuery.getValue()) {
                    case SOLACE_CREDENTIALS: content = getManifestFromCredentials(content); break;
                }
            }

            if (content != null && !content.isEmpty()) return content;
        }

        return null;
    }

    // Defaults to the user home directory
    private String getPathFromJvmOrEnv(String envName) {
        String path = System.getProperty(envName, System.getenv(envName));
        if (path == null || path.isEmpty()) path = System.getProperty("user.home");
        return path;
    }

    private String readFile(String dir, String fileName) {
        Path filePath = Paths.get(dir.concat(File.separator).concat(fileName));
        if (Files.notExists(filePath)) return "";
        else if (!Files.isReadable(filePath)) { //TODO Add some waiting mechanism before failing
            logger.warn(String.format("%s cannot be opened for reading. Ignoring file parameter...", filePath));
            return "";
        }

        String fileContents;
        try {
            fileContents = new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", filePath));
            return "";
        }

        return fileContents;
    }

    private String getManifestFromCredentials(String credentials) {
        String manifest = "";
        return manifest;
    }
}
