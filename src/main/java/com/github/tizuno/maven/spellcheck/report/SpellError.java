package com.github.tizuno.maven.spellcheck.report;

import java.io.File;
import java.util.List;

/**
 * Represents a single spelling error.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class SpellError {

    private final File file;
    private final int line;
    private final int column;
    private final String word;
    private final String message;
    private final List<String> suggestions;

    /**
     * Creates a new spell error.
     *
     * @param file        the file containing the error
     * @param line        the line number (1-based)
     * @param column      the column number (1-based)
     * @param word        the misspelled word
     * @param message     the error message
     * @param suggestions suggested corrections
     */
    public SpellError(File file, int line, int column, String word, String message, List<String> suggestions) {
        this.file = file;
        this.line = line;
        this.column = column;
        this.word = word;
        this.message = message;
        this.suggestions = suggestions;
    }

    /**
     * Gets the file containing the error.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the line number.
     *
     * @return the line number (1-based)
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number.
     *
     * @return the column number (1-based)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the misspelled word.
     *
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * Gets the error message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the suggested corrections.
     *
     * @return the list of suggestions
     */
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getPath()).append(":")
          .append(line).append(":")
          .append(column).append(": ")
          .append("'").append(word).append("' - ")
          .append(message);

        if (suggestions != null && !suggestions.isEmpty()) {
            sb.append(" [Suggestions: ");
            sb.append(String.join(", ", suggestions.subList(0, Math.min(3, suggestions.size()))));
            sb.append("]");
        }

        return sb.toString();
    }
}
