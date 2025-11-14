package com.github.tizuno.maven.spellcheck.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Generates JUnit XML format reports for spell check results.
 * This format is widely supported by CI/CD tools like Jenkins, GitLab CI, GitHub Actions, etc.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class JUnitXmlReportGenerator {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * Generates a JUnit XML report from spell check results.
     *
     * @param report     the spell check report
     * @param outputFile the output XML file
     * @throws IOException if writing fails
     */
    public void generateReport(SpellCheckReport report, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(XML_HEADER);
            writer.newLine();

            // Calculate test suite metrics
            int totalTests = report.getFilesChecked();
            int failures = report.getErrorsByFile().size(); // Files with errors
            int errors = 0; // We don't distinguish between errors and failures for spell check
            double totalTime = 0.0; // Time is not tracked in our implementation

            // Write testsuite opening tag
            writer.write(String.format(
                "<testsuite name=\"SpellCheck\" tests=\"%d\" failures=\"%d\" errors=\"%d\" time=\"%.3f\" timestamp=\"%s\">",
                totalTests, failures, errors, totalTime, Instant.now().toString()
            ));
            writer.newLine();

            // Write testcases for each file checked
            Map<File, List<SpellError>> errorsByFile = report.getErrorsByFile();

            // First, write test cases for files with errors
            for (Map.Entry<File, List<SpellError>> entry : errorsByFile.entrySet()) {
                File file = entry.getKey();
                List<SpellError> fileErrors = entry.getValue();

                writer.write(String.format(
                    "  <testcase name=\"%s\" classname=\"SpellCheck\" time=\"0.0\">",
                    escapeXml(file.getPath())
                ));
                writer.newLine();

                // Create failure message with all errors for this file
                StringBuilder failureMessage = new StringBuilder();
                failureMessage.append(String.format("Found %d spelling error(s) in %s",
                    fileErrors.size(), file.getPath()));

                StringBuilder failureDetail = new StringBuilder();
                for (SpellError error : fileErrors) {
                    failureDetail.append(String.format("%s:%d:%d: '%s' - %s",
                        file.getPath(),
                        error.getLine(),
                        error.getColumn(),
                        error.getWord(),
                        error.getMessage()
                    ));

                    if (error.getSuggestions() != null && !error.getSuggestions().isEmpty()) {
                        failureDetail.append(" [Suggestions: ");
                        int suggestionCount = Math.min(3, error.getSuggestions().size());
                        failureDetail.append(String.join(", ",
                            error.getSuggestions().subList(0, suggestionCount)));
                        failureDetail.append("]");
                    }
                    failureDetail.append("\n");
                }

                writer.write(String.format(
                    "    <failure message=\"%s\" type=\"SpellingError\">%s</failure>",
                    escapeXml(failureMessage.toString()),
                    escapeXml(failureDetail.toString())
                ));
                writer.newLine();

                writer.write("  </testcase>");
                writer.newLine();
            }

            // Then, write test cases for files without errors (passed tests)
            // Note: We don't track individual files in the current implementation,
            // so we'll just add a summary if there are files that passed
            int filesWithoutErrors = totalTests - failures;
            if (filesWithoutErrors > 0) {
                writer.write(String.format(
                    "  <testcase name=\"%d files passed spell check\" classname=\"SpellCheck\" time=\"0.0\"/>",
                    filesWithoutErrors
                ));
                writer.newLine();
            }

            // Close testsuite
            writer.write("</testsuite>");
            writer.newLine();
        }
    }

    /**
     * Escapes special XML characters.
     *
     * @param text the text to escape
     * @return the escaped text
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
