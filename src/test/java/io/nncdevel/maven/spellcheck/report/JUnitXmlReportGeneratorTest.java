package io.nncdevel.maven.spellcheck.report;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link JUnitXmlReportGenerator}.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class JUnitXmlReportGeneratorTest {

    private JUnitXmlReportGenerator generator;
    private Path tempDir;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        generator = new JUnitXmlReportGenerator();
        tempDir = Files.createTempDirectory("junit-report-test");
        outputFile = new File(tempDir.toFile(), "junit-report.xml");
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
        report.incrementFilesChecked();

        // When
        generator.generateReport(report, outputFile);

        // Then
        assertTrue("Output file should exist", outputFile.exists());

        String content = new String(Files.readAllBytes(outputFile.toPath()));
        assertTrue("Should contain XML header", content.contains("<?xml version=\"1.0\""));
        assertTrue("Should contain testsuite element", content.contains("<testsuite"));
        assertTrue("Should show 2 tests checked", content.contains("tests=\"2\""));
        assertTrue("Should show 0 failures", content.contains("failures=\"0\""));
        assertTrue("Should contain closing testsuite", content.contains("</testsuite>"));
    }

    @Test
    public void testGenerateReportWithErrors() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();
        report.incrementFilesChecked();

        File file1 = new File("TestFile.java");
        File file2 = new File("AnotherFile.java");

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
        assertTrue("Should contain testsuite element", content.contains("<testsuite"));
        assertTrue("Should show 2 tests", content.contains("tests=\"2\""));
        assertTrue("Should show 2 failures", content.contains("failures=\"2\""));

        // Verify test cases
        assertTrue("Should contain TestFile.java test case", content.contains("TestFile.java"));
        assertTrue("Should contain AnotherFile.java test case", content.contains("AnotherFile.java"));

        // Verify error details
        assertTrue("Should contain 'teh' error", content.contains("teh"));
        assertTrue("Should contain 'recieve' error", content.contains("recieve"));
        assertTrue("Should contain 'occurence' error", content.contains("occurence"));

        // Verify suggestions
        assertTrue("Should contain suggestions", content.contains("Suggestions:"));

        // Verify failure elements
        assertTrue("Should contain failure elements", content.contains("<failure"));
        assertTrue("Should have SpellingError type", content.contains("type=\"SpellingError\""));
    }

    @Test
    public void testXmlEscaping() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file = new File("Test<File>&\"Name\".java");
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

        // Should still be valid XML (no raw special characters in text)
        assertFalse("Should not contain raw < in attribute values",
            content.matches(".*name=\"[^\"]*<[^\"]*\".*"));
    }

    @Test
    public void testGenerateReportWithMultipleErrorsInSameFile() throws IOException {
        // Given
        SpellCheckReport report = new SpellCheckReport();
        report.incrementFilesChecked();

        File file = new File("TestFile.java");

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

        // Should have all errors in one test case for the file
        assertTrue("Should show 5 errors", content.contains("Found 5 spelling error(s)"));

        // Verify all errors are present
        for (int i = 1; i <= 5; i++) {
            assertTrue("Should contain error" + i, content.contains("error" + i));
        }
    }
}
