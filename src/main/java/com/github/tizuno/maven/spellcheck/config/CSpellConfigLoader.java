package com.github.tizuno.maven.spellcheck.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loader for CSpell configuration files.
 * Supports loading cspell.json or .cspell.json files.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class CSpellConfigLoader {

    private static final String[] CONFIG_FILE_NAMES = {
        "cspell.json",
        ".cspell.json",
        "cSpell.json",
        ".cSpell.json"
    };

    private final ObjectMapper objectMapper;
    private final Log log;
    private final Set<String> loadedFiles;

    /**
     * Creates a new CSpell configuration loader.
     *
     * @param log Maven logger
     */
    public CSpellConfigLoader(Log log) {
        this.objectMapper = new ObjectMapper();
        this.log = log;
        this.loadedFiles = new HashSet<>();
    }

    /**
     * Finds and loads a CSpell configuration file from the given directory.
     *
     * @param baseDirectory the directory to search for configuration files
     * @return the loaded configuration, or null if no configuration file found
     * @throws IOException if an error occurs reading the configuration file
     */
    public CSpellConfig loadConfig(File baseDirectory) throws IOException {
        File configFile = findConfigFile(baseDirectory);

        if (configFile == null) {
            log.debug("No CSpell configuration file found in: " + baseDirectory.getAbsolutePath());
            return null;
        }

        log.info("Loading CSpell configuration from: " + configFile.getAbsolutePath());
        return loadConfigFile(configFile, baseDirectory);
    }

    /**
     * Loads a specific CSpell configuration file.
     *
     * @param configFile the configuration file to load
     * @return the loaded configuration
     * @throws IOException if an error occurs reading the configuration file
     */
    public CSpellConfig loadConfig(File configFile) throws IOException {
        if (configFile == null || !configFile.exists()) {
            throw new IOException("Configuration file does not exist: " + configFile);
        }

        File baseDirectory = configFile.getParentFile();
        return loadConfigFile(configFile, baseDirectory);
    }

    /**
     * Finds a CSpell configuration file in the given directory.
     *
     * @param directory the directory to search
     * @return the configuration file, or null if not found
     */
    private File findConfigFile(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return null;
        }

        for (String fileName : CONFIG_FILE_NAMES) {
            File configFile = new File(directory, fileName);
            if (configFile.exists() && configFile.isFile()) {
                return configFile;
            }
        }

        return null;
    }

    /**
     * Loads and parses a CSpell configuration file.
     *
     * @param configFile the file to load
     * @param baseDirectory the base directory for resolving relative paths
     * @return the loaded configuration
     * @throws IOException if an error occurs reading or parsing the file
     */
    private CSpellConfig loadConfigFile(File configFile, File baseDirectory) throws IOException {
        String absolutePath = configFile.getAbsolutePath();

        // Prevent circular imports
        if (loadedFiles.contains(absolutePath)) {
            log.warn("Circular import detected, skipping: " + absolutePath);
            return new CSpellConfig();
        }

        loadedFiles.add(absolutePath);

        // Parse the JSON file
        CSpellConfig config = objectMapper.readValue(configFile, CSpellConfig.class);

        // Process imports
        if (config.getImportPaths() != null && !config.getImportPaths().isEmpty()) {
            config = processImports(config, baseDirectory);
        }

        return config;
    }

    /**
     * Processes import statements in the configuration.
     *
     * @param config the configuration with imports
     * @param baseDirectory the base directory for resolving import paths
     * @return the merged configuration
     * @throws IOException if an error occurs loading imported files
     */
    private CSpellConfig processImports(CSpellConfig config, File baseDirectory) throws IOException {
        CSpellConfig mergedConfig = new CSpellConfig();

        // Load and merge imported configurations
        for (String importPath : config.getImportPaths()) {
            File importFile = resolveImportPath(importPath, baseDirectory);

            if (importFile != null && importFile.exists()) {
                log.debug("Importing configuration from: " + importFile.getAbsolutePath());
                CSpellConfig importedConfig = loadConfigFile(importFile, importFile.getParentFile());
                mergedConfig = mergeConfigs(mergedConfig, importedConfig);
            } else {
                log.warn("Import file not found: " + importPath);
            }
        }

        // Merge current configuration on top of imported configurations
        mergedConfig = mergeConfigs(mergedConfig, config);

        return mergedConfig;
    }

    /**
     * Resolves an import path to a file.
     *
     * @param importPath the import path (can be relative or absolute)
     * @param baseDirectory the base directory for relative paths
     * @return the resolved file, or null if it cannot be resolved
     */
    private File resolveImportPath(String importPath, File baseDirectory) {
        File importFile = new File(importPath);

        // If absolute path and exists
        if (importFile.isAbsolute()) {
            return importFile;
        }

        // Try relative to base directory
        importFile = new File(baseDirectory, importPath);
        if (importFile.exists()) {
            return importFile;
        }

        return null;
    }

    /**
     * Merges two configurations, with the override configuration taking precedence.
     *
     * @param base the base configuration
     * @param override the overriding configuration
     * @return the merged configuration
     */
    private CSpellConfig mergeConfigs(CSpellConfig base, CSpellConfig override) {
        CSpellConfig merged = new CSpellConfig();

        // Simple properties (override takes precedence if not null)
        merged.setVersion(override.getVersion() != null ? override.getVersion() : base.getVersion());
        merged.setLanguage(override.getLanguage() != null ? override.getLanguage() : base.getLanguage());
        merged.setEnabled(override.getEnabled() != null ? override.getEnabled() : base.getEnabled());
        merged.setEnableGlobDot(override.getEnableGlobDot() != null ? override.getEnableGlobDot() : base.getEnableGlobDot());
        merged.setCaseSensitive(override.getCaseSensitive() != null ? override.getCaseSensitive() : base.getCaseSensitive());
        merged.setAllowCompoundWords(override.getAllowCompoundWords() != null ? override.getAllowCompoundWords() : base.getAllowCompoundWords());
        merged.setGlobRoot(override.getGlobRoot() != null ? override.getGlobRoot() : base.getGlobRoot());

        // List properties (merge both lists)
        merged.setWords(mergeLists(base.getWords(), override.getWords()));
        merged.setIgnoreWords(mergeLists(base.getIgnoreWords(), override.getIgnoreWords()));
        merged.setIgnorePaths(mergeLists(base.getIgnorePaths(), override.getIgnorePaths()));
        merged.setFiles(mergeLists(base.getFiles(), override.getFiles()));
        merged.setDictionaries(mergeLists(base.getDictionaries(), override.getDictionaries()));
        merged.setIgnoreRegExpList(mergeLists(base.getIgnoreRegExpList(), override.getIgnoreRegExpList()));
        merged.setIncludeRegExpList(mergeLists(base.getIncludeRegExpList(), override.getIncludeRegExpList()));
        merged.setFlagWords(mergeLists(base.getFlagWords(), override.getFlagWords()));

        // Don't merge import paths (already processed)
        merged.setImportPaths(new ArrayList<>());

        // Merge complex objects
        merged.setDictionaryDefinitions(mergeLists(base.getDictionaryDefinitions(), override.getDictionaryDefinitions()));
        merged.setPatterns(mergeLists(base.getPatterns(), override.getPatterns()));
        merged.setOverrides(mergeLists(base.getOverrides(), override.getOverrides()));

        return merged;
    }

    /**
     * Merges two lists, combining their elements.
     *
     * @param list1 the first list
     * @param list2 the second list
     * @param <T> the type of list elements
     * @return a new list containing all elements from both lists
     */
    private <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>();

        if (list1 != null) {
            merged.addAll(list1);
        }

        if (list2 != null) {
            merged.addAll(list2);
        }

        return merged;
    }

    /**
     * Converts a CSpell configuration to a SpellCheckConfiguration.
     *
     * @param cspellConfig the CSpell configuration
     * @return the SpellCheckConfiguration
     */
    public SpellCheckConfiguration toSpellCheckConfiguration(CSpellConfig cspellConfig) {
        SpellCheckConfiguration config = new SpellCheckConfiguration();

        if (cspellConfig == null) {
            return config;
        }

        // Set language
        if (cspellConfig.getLanguage() != null) {
            // CSpell can have multiple languages separated by comma (e.g., "en,nl")
            // For now, use the first one
            String language = cspellConfig.getLanguage().split(",")[0].trim();
            config.setLanguage(language);
        }

        // Merge words and ignoreWords
        List<String> allIgnoreWords = new ArrayList<>();
        if (cspellConfig.getWords() != null) {
            allIgnoreWords.addAll(cspellConfig.getWords());
        }
        if (cspellConfig.getIgnoreWords() != null) {
            allIgnoreWords.addAll(cspellConfig.getIgnoreWords());
        }
        config.setIgnoreWords(allIgnoreWords);

        return config;
    }
}
