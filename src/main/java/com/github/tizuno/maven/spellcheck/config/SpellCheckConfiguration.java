package com.github.tizuno.maven.spellcheck.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for spell checking.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class SpellCheckConfiguration {

    private String language = "en-US";
    private String encoding = "UTF-8";
    private File customDictionary;
    private List<String> ignoreWords = new ArrayList<>();

    /**
     * Gets the language for spell checking.
     *
     * @return the language code (e.g., "en-US")
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language for spell checking.
     *
     * @param language the language code
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the file encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the file encoding.
     *
     * @param encoding the encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Gets the custom dictionary file.
     *
     * @return the custom dictionary file
     */
    public File getCustomDictionary() {
        return customDictionary;
    }

    /**
     * Sets the custom dictionary file.
     *
     * @param customDictionary the custom dictionary file
     */
    public void setCustomDictionary(File customDictionary) {
        this.customDictionary = customDictionary;
    }

    /**
     * Gets the list of words to ignore.
     *
     * @return the list of ignored words
     */
    public List<String> getIgnoreWords() {
        return ignoreWords;
    }

    /**
     * Sets the list of words to ignore.
     *
     * @param ignoreWords the list of ignored words
     */
    public void setIgnoreWords(List<String> ignoreWords) {
        this.ignoreWords = ignoreWords;
    }
}
