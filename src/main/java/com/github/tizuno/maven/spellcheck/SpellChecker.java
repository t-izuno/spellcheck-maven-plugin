package com.github.tizuno.maven.spellcheck;

import com.github.tizuno.maven.spellcheck.config.SpellCheckConfiguration;
import com.github.tizuno.maven.spellcheck.report.SpellCheckReport;
import com.github.tizuno.maven.spellcheck.report.SpellError;
import org.apache.maven.plugin.logging.Log;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Core spell checker implementation using LanguageTool.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class SpellChecker {

    private final SpellCheckConfiguration config;
    private final Log log;
    private final JLanguageTool languageTool;

    /**
     * Creates a new spell checker with the given configuration.
     *
     * @param config the spell check configuration
     * @param log    the Maven logger
     * @throws IOException if initialization fails
     */
    public SpellChecker(SpellCheckConfiguration config, Log log) throws IOException {
        this.config = config;
        this.log = log;
        this.languageTool = createLanguageTool(config.getLanguage());

        // Add custom words to ignore
        if (config.getCustomDictionary() != null && config.getCustomDictionary().exists()) {
            loadCustomDictionary(config.getCustomDictionary());
        }

        // Add ignore words
        for (String word : config.getIgnoreWords()) {
            languageTool.addIgnoreTokens(java.util.Collections.singletonList(word));
        }
    }

    /**
     * Creates a JLanguageTool instance for the specified language.
     *
     * @param language the language code
     * @return the JLanguageTool instance
     */
    private JLanguageTool createLanguageTool(String language) {
        if (language == null || language.startsWith("en-US")) {
            return new JLanguageTool(new AmericanEnglish());
        } else if (language.startsWith("en-GB")) {
            return new JLanguageTool(new BritishEnglish());
        } else {
            // Default to American English
            log.warn("Unsupported language: " + language + ". Defaulting to en-US.");
            return new JLanguageTool(new AmericanEnglish());
        }
    }

    /**
     * Loads custom dictionary words.
     *
     * @param dictionaryFile the dictionary file
     * @throws IOException if reading fails
     */
    private void loadCustomDictionary(File dictionaryFile) throws IOException {
        log.debug("Loading custom dictionary from: " + dictionaryFile.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    languageTool.addIgnoreTokens(java.util.Collections.singletonList(line));
                }
            }
        }

        log.debug("Custom dictionary loaded successfully");
    }

    /**
     * Checks the given files for spelling errors.
     *
     * @param files the files to check
     * @return the spell check report
     * @throws IOException if file reading fails
     */
    public SpellCheckReport check(List<File> files) throws IOException {
        SpellCheckReport report = new SpellCheckReport();

        for (File file : files) {
            log.debug("Checking file: " + file.getAbsolutePath());
            checkFile(file, report);
        }

        return report;
    }

    /**
     * Checks a single file for spelling errors.
     *
     * @param file   the file to check
     * @param report the report to update
     * @throws IOException if file reading fails
     */
    private void checkFile(File file, SpellCheckReport report) throws IOException {
        report.incrementFilesChecked();

        String content = readFile(file);

        if (content.trim().isEmpty()) {
            log.debug("Skipping empty file: " + file.getName());
            return;
        }

        try {
            List<RuleMatch> matches = languageTool.check(content);

            for (RuleMatch match : matches) {
                // Only report spelling errors, not grammar errors
                if (isSpellingError(match)) {
                    SpellError error = new SpellError(
                        file,
                        match.getLine() + 1,
                        match.getColumn() + 1,
                        content.substring(match.getFromPos(), match.getToPos()),
                        match.getMessage(),
                        match.getSuggestedReplacements()
                    );

                    report.addError(error);

                    if (log.isDebugEnabled()) {
                        log.debug(String.format(
                            "Error in %s at line %d: %s",
                            file.getName(),
                            error.getLine(),
                            error.getWord()
                        ));
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Error checking file " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Reads the content of a file.
     *
     * @param file the file to read
     * @return the file content
     * @throws IOException if reading fails
     */
    private String readFile(File file) throws IOException {
        Charset charset = Charset.forName(config.getEncoding());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, charset);
    }

    /**
     * Determines if a rule match is a spelling error.
     *
     * @param match the rule match
     * @return true if it's a spelling error
     */
    private boolean isSpellingError(RuleMatch match) {
        String ruleId = match.getRule().getId();
        // Check if it's a spelling-related rule
        return ruleId.contains("SPELL") ||
               ruleId.contains("MORFOLOGIK") ||
               ruleId.contains("HUNSPELL") ||
               ruleId.contains("TYPO");
    }
}
