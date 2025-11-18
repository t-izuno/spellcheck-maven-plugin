package io.nncdevel.maven.spellcheck.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration model for CSpell-compatible configuration files.
 * This class represents the structure of cspell.json or .cspell.json files.
 *
 * @author T. Izuno
 * @since 1.0.0
 * @see <a href="https://cspell.org/configuration/">CSpell Configuration</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSpellConfig {

    /**
     * Configuration format version (e.g., "0.2").
     */
    @JsonProperty("version")
    private String version;

    /**
     * Language locale(s) to use for spell checking (e.g., "en", "en-US", "en,nl").
     */
    @JsonProperty("language")
    private String language;

    /**
     * List of words to be considered correct.
     */
    @JsonProperty("words")
    private List<String> words;

    /**
     * List of words to be ignored. An ignored word will not show up as an error.
     */
    @JsonProperty("ignoreWords")
    private List<String> ignoreWords;

    /**
     * Glob patterns of files to be ignored.
     */
    @JsonProperty("ignorePaths")
    private List<String> ignorePaths;

    /**
     * Glob patterns of files to be checked.
     */
    @JsonProperty("files")
    private List<String> files;

    /**
     * Optional list of dictionaries to use.
     */
    @JsonProperty("dictionaries")
    private List<String> dictionaries;

    /**
     * Defines custom available dictionaries.
     */
    @JsonProperty("dictionaryDefinitions")
    private List<DictionaryDefinition> dictionaryDefinitions;

    /**
     * List of patterns that can be used with ignoreRegExpList and includeRegExpList.
     */
    @JsonProperty("patterns")
    private List<Pattern> patterns;

    /**
     * Regular expression patterns to exclude from spell checking.
     */
    @JsonProperty("ignoreRegExpList")
    private List<String> ignoreRegExpList;

    /**
     * Patterns to match for spell checking.
     */
    @JsonProperty("includeRegExpList")
    private List<String> includeRegExpList;

    /**
     * Allows this configuration to inherit configuration from other files.
     */
    @JsonProperty("import")
    private List<String> importPaths;

    /**
     * Enables or disables the spell checker.
     */
    @JsonProperty("enabled")
    private Boolean enabled;

    /**
     * Enable scanning of hidden files and directories (starting with ".").
     */
    @JsonProperty("enableGlobDot")
    private Boolean enableGlobDot;

    /**
     * Determines if words must match case and accent rules.
     */
    @JsonProperty("caseSensitive")
    private Boolean caseSensitive;

    /**
     * Enables compound word checking.
     */
    @JsonProperty("allowCompoundWords")
    private Boolean allowCompoundWords;

    /**
     * Words that are always considered incorrect.
     */
    @JsonProperty("flagWords")
    private List<String> flagWords;

    /**
     * Root directory for glob patterns.
     */
    @JsonProperty("globRoot")
    private String globRoot;

    /**
     * Apply settings to specific files in the project.
     */
    @JsonProperty("overrides")
    private List<Override> overrides;

    // Constructors

    public CSpellConfig() {
        this.words = new ArrayList<>();
        this.ignoreWords = new ArrayList<>();
        this.ignorePaths = new ArrayList<>();
        this.files = new ArrayList<>();
        this.dictionaries = new ArrayList<>();
        this.dictionaryDefinitions = new ArrayList<>();
        this.patterns = new ArrayList<>();
        this.ignoreRegExpList = new ArrayList<>();
        this.includeRegExpList = new ArrayList<>();
        this.importPaths = new ArrayList<>();
        this.flagWords = new ArrayList<>();
        this.overrides = new ArrayList<>();
    }

    // Getters and Setters

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getIgnoreWords() {
        return ignoreWords;
    }

    public void setIgnoreWords(List<String> ignoreWords) {
        this.ignoreWords = ignoreWords;
    }

    public List<String> getIgnorePaths() {
        return ignorePaths;
    }

    public void setIgnorePaths(List<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<String> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public List<DictionaryDefinition> getDictionaryDefinitions() {
        return dictionaryDefinitions;
    }

    public void setDictionaryDefinitions(List<DictionaryDefinition> dictionaryDefinitions) {
        this.dictionaryDefinitions = dictionaryDefinitions;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public List<String> getIgnoreRegExpList() {
        return ignoreRegExpList;
    }

    public void setIgnoreRegExpList(List<String> ignoreRegExpList) {
        this.ignoreRegExpList = ignoreRegExpList;
    }

    public List<String> getIncludeRegExpList() {
        return includeRegExpList;
    }

    public void setIncludeRegExpList(List<String> includeRegExpList) {
        this.includeRegExpList = includeRegExpList;
    }

    public List<String> getImportPaths() {
        return importPaths;
    }

    public void setImportPaths(List<String> importPaths) {
        this.importPaths = importPaths;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnableGlobDot() {
        return enableGlobDot;
    }

    public void setEnableGlobDot(Boolean enableGlobDot) {
        this.enableGlobDot = enableGlobDot;
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Boolean getAllowCompoundWords() {
        return allowCompoundWords;
    }

    public void setAllowCompoundWords(Boolean allowCompoundWords) {
        this.allowCompoundWords = allowCompoundWords;
    }

    public List<String> getFlagWords() {
        return flagWords;
    }

    public void setFlagWords(List<String> flagWords) {
        this.flagWords = flagWords;
    }

    public String getGlobRoot() {
        return globRoot;
    }

    public void setGlobRoot(String globRoot) {
        this.globRoot = globRoot;
    }

    public List<Override> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<Override> overrides) {
        this.overrides = overrides;
    }

    // Nested Classes

    /**
     * Defines a custom dictionary.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DictionaryDefinition {
        @JsonProperty("name")
        private String name;

        @JsonProperty("path")
        private String path;

        @JsonProperty("description")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Defines a pattern for matching text.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pattern {
        @JsonProperty("name")
        private String name;

        @JsonProperty("pattern")
        private String pattern;

        @JsonProperty("description")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Override settings for specific files.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Override {
        @JsonProperty("filename")
        private String filename;

        @JsonProperty("language")
        private String language;

        @JsonProperty("words")
        private List<String> words;

        @JsonProperty("ignoreWords")
        private List<String> ignoreWords;

        @JsonProperty("enabled")
        private Boolean enabled;

        public Override() {
            this.words = new ArrayList<>();
            this.ignoreWords = new ArrayList<>();
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<String> getWords() {
            return words;
        }

        public void setWords(List<String> words) {
            this.words = words;
        }

        public List<String> getIgnoreWords() {
            return ignoreWords;
        }

        public void setIgnoreWords(List<String> ignoreWords) {
            this.ignoreWords = ignoreWords;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
