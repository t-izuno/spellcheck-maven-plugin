package com.github.tizuno.maven.spellcheck.report;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for {@link CheckstyleXmlReportGenerator}.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class CheckstyleXmlReportGeneratorTest {

    private CheckstyleXmlReportGenerator generator;
    private Path tempDir;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        generator = new CheckstyleXmlReportGenerator();
        tempDir = Files.createTempDirectory("checkstyle-report-test");
        outputFile = new File(tempDir.toFile(), "checkstyle-report.xml");
    }

    @After
    public void tearDown() throws IOException {
        // Clean up temp directory
        if (outputFile.exists()) {
            outputFile.delete();
        }
        Files.deleteIfExists(tempDir);
    }

    @Test
    public void testGenerateEmptyReport() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        // When
        generator.generateReport(report, outputFile);

        // Then
        assertTrue("Output file should exist", outputFile.exists());

        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue("Should contain XML header", content.contains("<?xml version=\"1.0\""));
        assertTrue("Should contain checkstyle element", content.contains("<checkstyle"));
        assertTrue("Should contain version", content.contains("version="));
        assertTrue("Should contain closing checkstyle", content.contains("</checkstyle>"));
    }

    @Test
    public void testGenerateReportWithErrors() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file1 = new File("/path/to/TestFile.java");
        File file2 = new File("/path/to/AnotherFile.java");

        SpellError error1 = new SpellError(
            file1, 10, 5, "teh",
            "Possible spelling mistake found",
            Arrays.asList("the", "tea", "ten")
        );

        SpellError error2 = new SpellError(
            file1, 15, 10, "recieve",
            "Possible spelling mistake found",
            Arrays.asList("receive")
        );

        SpellError error3 = new SpellError(
            file2, 20, 3, "occurence",
            "Possible spelling mistake found",
            Arrays.asList("occurrence")
        );

        report.addError(error1);
        report.addError(error2);
        report.addError(error3);

        // When
        generator.generateReport(report, outputFile);

        // Then
        assertTrue("Output file should exist", outputFile.exists());

        String content = new String(Files.readAllBytes(outputFile.toPath()));

        // Verify XML structure
        assertTrue("Should contain XML header", content.contains("<?xml version=\"1.0\""));
        assertTrue("Should contain checkstyle element", content.contains("<checkstyle"));

        // Verify file elements
        assertTrue("Should contain file element for TestFile.java",
            content.contains("<file name=\"" + file1.getAbsolutePath()));
        assertTrue("Should contain file element for AnotherFile.java",
            content.contains("<file name=\"" + file2.getAbsolutePath()));

        // Verify error elements
        assertTrue("Should contain error element", content.contains("<error"));
        assertTrue("Should have line attribute", content.contains("line="));
        assertTrue("Should have column attribute", content.contains("column="));
        assertTrue("Should have severity attribute", content.contains("severity=\"error\""));
        assertTrue("Should have message attribute", content.contains("message="));
        assertTrue("Should have source attribute", content.contains("source=\"SpellCheck\""));

        // Verify error details
        assertTrue("Should contain 'teh' error", content.contains("teh"));
        assertTrue("Should contain 'recieve' error", content.contains("recieve"));
        assertTrue("Should contain 'occurence' error", content.contains("occurence"));

        // Verify suggestions
        assertTrue("Should contain suggestions", content.contains("Suggestions:"));
    }

    @Test
    public void testXmlEscaping() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file = new File("/path/with/special<chars>&\"name\"/Test.java");
        SpellError error = new SpellError(
            file, 1, 1, "test",
            "Error with special chars: <>&\"'",
            Arrays.asList("suggestion")
        );
        report.addError(error);

        // When
        generator.generateReport(report, outputFile);

        // Then
        assertTrue("Output file should exist", outputFile.exists());

        String content = new String(Files.readAllBytes(outputFile.toPath()));

        // Verify special characters are escaped
        assertTrue("Should escape <", content.contains("&lt;"));
        assertTrue("Should escape >", content.contains("&gt;"));
        assertTrue("Should escape &", content.contains("&amp;"));
        assertTrue("Should escape \"", content.contains("&quot;"));
    }

    @Test
    public void testMultipleErrorsInSameFile() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file = new File("/path/to/TestFile.java");

        for (int i = 1; i <= 5; i++) {
            SpellError error = new SpellError(
                file, i * 10, i, "error" + i,
                "Error message " + i,
                Arrays.asList("correction" + i)
            );
            report.addError(error);
        }

        // When
        generator.generateReport(report, outputFile);

        // Then
        assertTrue("Output file should exist", outputFile.exists());

        String content = new String(Files.readAllBytes(outputFile.toPath()));

        // Should have one file element with multiple error elements
        int fileTagCount = countOccurrences(content, "<file");
        assertEquals("Should have exactly one file element", 1, fileTagCount);

        int errorTagCount = countOccurrences(content, "<error");
        assertEquals("Should have 5 error elements", 5, errorTagCount);

        // Verify all errors are present
        for (int i = 1; i <= 5; i++) {
            assertTrue("Should contain error" + i, content.contains("error" + i));
            assertTrue("Should contain line " + (i * 10), content.contains("line=\"" + (i * 10) + "\""));
        }
    }

    @Test
    public void testErrorAttributes() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file = new File("/path/to/TestFile.java");
        SpellError error = new SpellError(
            file, 42, 15, "misspeled",
            "Possible spelling mistake",
            Arrays.asList("misspelled", "mis-spelled")
        );
        report.addError(error);

        // When
        generator.generateReport(report, outputFile);

        // Then
        String content = new String(Files.readAllBytes(outputFile.toPath()));

        // Verify error attributes
        assertTrue("Should have correct line number", content.contains("line=\"42\""));
        assertTrue("Should have correct column number", content.contains("column=\"15\""));
        assertTrue("Should have severity='error'", content.contains("severity=\"error\""));
        assertTrue("Should have source='SpellCheck'", content.contains("source=\"SpellCheck\""));
        assertTrue("Should contain word in message", content.contains("misspeled"));
    }

    /**
     * Helper method to count occurrences of a substring.
     */
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
