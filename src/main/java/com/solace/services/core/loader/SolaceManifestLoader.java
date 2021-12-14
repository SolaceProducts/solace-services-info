package com.solace.services.core.loader;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Only deals with manifest retrieval. No manifest marshalling or validation is done at this level.</p>
 *
 * <p>The manifest load order is as follows:</p>
 * <ol>
 *     <li>{@link SolaceManifestLoader.SolaceEnv#SOLCAP_SERVICES SOLCAP_SERVICES} in the JVM properties.</li>
 *     <li>{@link SolaceManifestLoader.SolaceEnv#SOLCAP_SERVICES SOLCAP_SERVICES} as an OS environment.</li>
 *     <li>{@link SolaceManifestLoader.SolaceEnv#SOLACE_SERVICES_HOME SOLACE_SERVICES_HOME} in the JVM properties.</li>
 *     <li>{@link SolaceManifestLoader.SolaceEnv#SOLACE_SERVICES_HOME SOLACE_SERVICES_HOME} as an OS environment.</li>
 *     <li>Fallback check for file {@value #MANIFEST_FILE_NAME} in the user's home directory.</li>
 * </ol>
 *
 * <p>Of the mentioned environments, some may be associated to a content
 *      {@link SolaceManifestLoader.PostProcessor post-processor}.</p>
 * <p>Specific associations can be found by looking at the entries given to {@link #searchQueries}.</p>
 */
class SolaceManifestLoader {
    enum SolaceEnv {SOLACE_CREDENTIALS, SOLCAP_SERVICES, SOLACE_SERVICES_HOME}
    enum SolaceEnvSource {JVM, ENV}
    enum PostProcessor {NONE, FILE, REST}

    static final String MANIFEST_FILE_NAME = ".solaceservices";
    private static final Logger logger = LoggerFactory.getLogger(SolaceManifestLoader.class);

    private List<Triple<SolaceEnv, SolaceEnvSource, PostProcessor>> searchQueries;

    public SolaceManifestLoader() {
        searchQueries = new LinkedList<>();
//        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_CREDENTIALS, SolaceEnvSource.JVM, PostProcessor.REST));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLCAP_SERVICES, SolaceEnvSource.JVM, PostProcessor.NONE));
//        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_CREDENTIALS, SolaceEnvSource.ENV, PostProcessor.REST));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLCAP_SERVICES, SolaceEnvSource.ENV, PostProcessor.NONE));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_SERVICES_HOME, SolaceEnvSource.JVM, PostProcessor.FILE));
        searchQueries.add(new ImmutableTriple<>(SolaceEnv.SOLACE_SERVICES_HOME, SolaceEnvSource.ENV, PostProcessor.FILE));
    }

    // For Testing
    SolaceManifestLoader(List<Triple<SolaceEnv, SolaceEnvSource, PostProcessor>> searchQueries) {
        this.searchQueries = searchQueries;
    }

    /**
     * Finds and loads a manifest from the application's environment as per the precedence defined in the search queries.
     * The manifest contents are retrieved <b>as is</b> and is not checked for validity.
     * @return A JSON string representing a service manifest, null if not found.
     */
    public String getManifest() {
        String content = null;
        for (Triple<SolaceEnv, SolaceEnvSource, PostProcessor> searchQuery : searchQueries) {
            String sourceName = searchQuery.getLeft().name();
            switch (searchQuery.getMiddle()) {
                case JVM: content = System.getProperty(sourceName, null); break;
                case ENV: content = System.getenv(sourceName); break;
            }

            // Post Processing
            if (content!= null && !content.isEmpty()) {
                switch (searchQuery.getRight()) {
                    case FILE: content = readFile(content, MANIFEST_FILE_NAME); break;
                    case REST: content = getManifestFromCredentials(content); break;
                }
            }

            if (content != null && !content.isEmpty()) return content;
        }

        // Fallback
        content = readFile(System.getProperty("user.home"), MANIFEST_FILE_NAME);
        if (content != null && !content.isEmpty()) return content;
        else return null;
    }

    private String readFile(String dir, String fileName) {
        Path filePath = Paths.get(dir.concat(File.separator).concat(fileName));
        if (Files.notExists(filePath)) {
            if (!dir.equals(System.getProperty("user.home")))
                logger.warn("File {} does not exist", filePath);
            return "";
        } else if (!Files.isReadable(filePath)) {
            logger.warn(String.format("%s cannot be opened for reading. Ignoring file parameter...", filePath));
            return "";
        }

        String fileContents;
        try {
            fileContents = new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            logger.error("Error reading {}", filePath);
            return "";
        }
        return fileContents;
    }

    private String getManifestFromCredentials(String credentials) { //TODO
        String manifest = "";
        return manifest;
    }

    // For Testing
    void setSearchQueries(List<Triple<SolaceEnv, SolaceEnvSource, PostProcessor>> searchQueries) {
        this.searchQueries = searchQueries;
    }
}
