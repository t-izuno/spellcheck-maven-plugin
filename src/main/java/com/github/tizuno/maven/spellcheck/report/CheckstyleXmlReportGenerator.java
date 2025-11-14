package com.github.tizuno.maven.spellcheck.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Generates Checkstyle XML format reports for spell check results.
 * This format is supported by Jenkins Warnings Next Generation plugin,
 * SonarQube, and other code quality tools.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class CheckstyleXmlReportGenerator {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String CHECKSTYLE_VERSION = "10.0";

    /**
     * Generates a Checkstyle XML report from spell check results.
     *
     * @param report     the spell check report
     * @param outputFile the output XML file
     * @throws IOException if writing fails
     */
    public void generateReport(SpellCheckReport report, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(XML_HEADER);
            writer.newLine();

            // Write checkstyle opening tag
            writer.write(String.format("<checkstyle version=\"%s\">", CHECKSTYLE_VERSION));
            writer.newLine();

            // Write errors grouped by file
            Map<File, List<SpellError>> errorsByFile = report.getErrorsByFile();

            for (Map.Entry<File, List<SpellError>> entry : errorsByFile.entrySet()) {
                File file = entry.getKey();
                List<SpellError> fileErrors = entry.getValue();

                // Write file opening tag with absolute path
                writer.write(String.format("  <file name=\"%s\">", escapeXml(file.getAbsolutePath())));
                writer.newLine();

                // Write each error as an error element
                for (SpellError error : fileErrors) {
                    // Build error message
                    StringBuilder message = new StringBuilder();
                    message.append(String.format("Spelling error: '%s' - %s",
                        error.getWord(),
                        error.getMessage()
                    ));

                    if (error.getSuggestions() != null && !error.getSuggestions().isEmpty()) {
                        message.append(" [Suggestions: ");
                        int suggestionCount = Math.min(3, error.getSuggestions().size());
                        message.append(String.join(", ",
                            error.getSuggestions().subList(0, suggestionCount)));
                        message.append("]");
                    }

                    // Write error element
                    writer.write(String.format(
                        "    <error line=\"%d\" column=\"%d\" severity=\"error\" message=\"%s\" source=\"SpellCheck\"/>",
                        error.getLine(),
                        error.getColumn(),
                        escapeXml(message.toString())
                    ));
                    writer.newLine();
                }

                // Write file closing tag
                writer.write("  </file>");
                writer.newLine();
            }

            // Close checkstyle
            writer.write("</checkstyle>");
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
