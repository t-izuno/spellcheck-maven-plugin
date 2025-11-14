package com.github.tizuno.maven.spellcheck.report;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for SpellCheckReport.
 *
 * @author T. Izuno
 */
public class SpellCheckReportTest {

    private SpellCheckReport report;
    private File testFile;

    @Before
    public void setUp() {
        report = new SpellCheckReport();
        testFile = new File("TestFile.java");
    }

    @Test
    public void testInitialState() {
        assertEquals(0, report.getFilesChecked());
        assertEquals(0, report.getErrorCount());
        assertFalse(report.hasErrors());
    }

    @Test
    public void testIncrementFilesChecked() {
        report.incrementFilesChecked();
        assertEquals(1, report.getFilesChecked());

        report.incrementFilesChecked();
        assertEquals(2, report.getFilesChecked());
    }

    @Test
    public void testAddError() {
        List<String> suggestions = Arrays.asList("correct", "correkt");
        SpellError error = new SpellError(testFile, 10, 5, "incorect", "Possible spelling mistake", suggestions);

        report.addError(error);

        assertEquals(1, report.getErrorCount());
        assertTrue(report.hasErrors());
        assertEquals(1, report.getErrors().size());
        assertEquals(error, report.getErrors().get(0));
    }

    @Test
    public void testMultipleErrors() {
        SpellError error1 = new SpellError(testFile, 10, 5, "incorect", "Spelling mistake", Arrays.asList("correct"));
        SpellError error2 = new SpellError(testFile, 15, 8, "teh", "Spelling mistake", Arrays.asList("the"));

        report.addError(error1);
        report.addError(error2);

        assertEquals(2, report.getErrorCount());
        assertTrue(report.hasErrors());
    }

    @Test
    public void testErrorsByFile() {
        File file1 = new File("File1.java");
        File file2 = new File("File2.java");

        SpellError error1 = new SpellError(file1, 10, 5, "incorect", "Spelling mistake", Arrays.asList("correct"));
        SpellError error2 = new SpellError(file1, 15, 8, "teh", "Spelling mistake", Arrays.asList("the"));
        SpellError error3 = new SpellError(file2, 5, 1, "speling", "Spelling mistake", Arrays.asList("spelling"));

        report.addError(error1);
        report.addError(error2);
        report.addError(error3);

        assertEquals(2, report.getErrorsByFile().get(file1).size());
        assertEquals(1, report.getErrorsByFile().get(file2).size());
    }

    @Test
    public void testWriteToFile() throws IOException {
        File outputFile = Files.createTempFile("spellcheck-test", ".txt").toFile();
        outputFile.deleteOnExit();

        report.incrementFilesChecked();
        report.incrementFilesChecked();

        SpellError error = new SpellError(testFile, 10, 5, "incorect", "Possible spelling mistake",
                                         Arrays.asList("correct", "incorrect"));
        report.addError(error);

        report.writeToFile(outputFile);

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);

        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue(content.contains("Files checked: 2"));
        assertTrue(content.contains("Errors found: 1"));
        assertTrue(content.contains("incorect"));
    }

    @Test
    public void testWriteEmptyReport() throws IOException {
        File outputFile = Files.createTempFile("spellcheck-empty", ".txt").toFile();
        outputFile.deleteOnExit();

        report.incrementFilesChecked();
        report.writeToFile(outputFile);

        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue(content.contains("No spelling errors found!"));
    }
}
