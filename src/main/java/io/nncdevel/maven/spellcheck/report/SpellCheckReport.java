package io.nncdevel.maven.spellcheck.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report containing all spell check results.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class SpellCheckReport {

    private int filesChecked = 0;
    private final List<SpellError> errors = new ArrayList<>();
    private final Map<File, List<SpellError>> errorsByFile = new HashMap<>();

    /**
     * Increments the count of files checked.
     */
    public void incrementFilesChecked() {
        filesChecked++;
    }

    /**
     * Adds a spell error to the report.
     *
     * @param error the spell error
     */
    public void addError(SpellError error) {
        errors.add(error);
        errorsByFile.computeIfAbsent(error.getFile(), k -> new ArrayList<>()).add(error);
    }

    /**
     * Gets the number of files checked.
     *
     * @return the number of files
     */
    public int getFilesChecked() {
        return filesChecked;
    }

    /**
     * Gets the total number of errors found.
     *
     * @return the error count
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Checks if any errors were found.
     *
     * @return true if errors exist
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Gets all errors.
     *
     * @return the list of errors
     */
    public List<SpellError> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Gets errors grouped by file.
     *
     * @return map of file to errors
     */
    public Map<File, List<SpellError>> getErrorsByFile() {
        return new HashMap<>(errorsByFile);
    }

    /**
     * Writes the report to a text file.
     *
     * @param outputFile the output file
     * @throws IOException if writing fails
     */
    public void writeToFile(File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Spell Check Report");
            writer.newLine();
            writer.write("==================");
            writer.newLine();
            writer.newLine();

            writer.write("Files checked: " + filesChecked);
            writer.newLine();
            writer.write("Errors found: " + errors.size());
            writer.newLine();
            writer.newLine();

            if (errors.isEmpty()) {
                writer.write("No spelling errors found!");
                writer.newLine();
            } else {
                writer.write("Errors by file:");
                writer.newLine();
                writer.write("---------------");
                writer.newLine();
                writer.newLine();

                for (Map.Entry<File, List<SpellError>> entry : errorsByFile.entrySet()) {
                    writer.write(entry.getKey().getPath());
                    writer.newLine();

                    for (SpellError error : entry.getValue()) {
                        writer.write("  Line " + error.getLine() +
                                   ", Column " + error.getColumn() +
                                   ": '" + error.getWord() + "'");
                        writer.newLine();
                        writer.write("    " + error.getMessage());
                        writer.newLine();

                        if (error.getSuggestions() != null && !error.getSuggestions().isEmpty()) {
                            writer.write("    Suggestions: " +
                                       String.join(", ",
                                           error.getSuggestions().subList(0, Math.min(5, error.getSuggestions().size()))));
                            writer.newLine();
                        }
                        writer.newLine();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SpellCheckReport{" +
               "filesChecked=" + filesChecked +
               ", errorCount=" + errors.size() +
               '}';
    }
}
