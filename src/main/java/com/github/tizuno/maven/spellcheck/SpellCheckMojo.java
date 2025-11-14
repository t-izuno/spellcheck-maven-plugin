package com.github.tizuno.maven.spellcheck;

import com.github.tizuno.maven.spellcheck.config.SpellCheckConfiguration;
import com.github.tizuno.maven.spellcheck.report.SpellCheckReport;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maven Mojo for spell checking source files, documentation, and comments.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
@Mojo(name = "check",
      defaultPhase = LifecyclePhase.VERIFY,
      requiresProject = true,
      threadSafe = true)
public class SpellCheckMojo extends AbstractMojo {

    /**
     * The Maven project instance.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Skip the spell check execution.
     */
    @Parameter(property = "spellcheck.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Fail the build on spell check errors.
     */
    @Parameter(property = "spellcheck.failOnError", defaultValue = "true")
    private boolean failOnError;

    /**
     * Source file encoding.
     */
    @Parameter(property = "spellcheck.encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    /**
     * File includes pattern (Ant-style).
     */
    @Parameter(property = "spellcheck.includes")
    private String[] includes;

    /**
     * File excludes pattern (Ant-style).
     */
    @Parameter(property = "spellcheck.excludes")
    private String[] excludes;

    /**
     * Directories to scan for files to spell check.
     */
    @Parameter
    private File[] sourceDirectories;

    /**
     * Language for spell checking (e.g., "en-US", "en-GB").
     */
    @Parameter(property = "spellcheck.language", defaultValue = "en-US")
    private String language;

    /**
     * Custom dictionary file containing words to ignore.
     */
    @Parameter(property = "spellcheck.customDictionary")
    private File customDictionary;

    /**
     * Words to ignore during spell checking.
     */
    @Parameter
    private List<String> ignoreWords;

    /**
     * Output directory for reports.
     */
    @Parameter(defaultValue = "${project.build.directory}/spellcheck", readonly = true)
    private File outputDirectory;

    /**
     * Generate HTML report.
     */
    @Parameter(property = "spellcheck.generateReport", defaultValue = "true")
    private boolean generateReport;

    private SpellChecker spellChecker;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Spell check is skipped.");
            return;
        }

        getLog().info("Starting spell check...");
        getLog().info("Language: " + language);
        getLog().info("Encoding: " + (encoding != null ? encoding : "UTF-8"));

        try {
            // Initialize configuration
            SpellCheckConfiguration config = createConfiguration();

            // Initialize spell checker
            spellChecker = new SpellChecker(config, getLog());

            // Get files to check
            List<File> filesToCheck = getFilesToCheck();

            if (filesToCheck.isEmpty()) {
                getLog().warn("No files found to spell check.");
                return;
            }

            getLog().info("Checking " + filesToCheck.size() + " file(s)...");

            // Perform spell checking
            SpellCheckReport report = spellChecker.check(filesToCheck);

            // Generate report if requested
            if (generateReport) {
                generateReport(report);
            }

            // Log summary
            logSummary(report);

            // Fail build if errors found and failOnError is true
            if (failOnError && report.hasErrors()) {
                throw new MojoFailureException(
                    "Spell check found " + report.getErrorCount() + " error(s). " +
                    "See report at: " + new File(outputDirectory, "spellcheck-report.txt").getAbsolutePath()
                );
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error during spell check execution", e);
        }
    }

    /**
     * Creates the spell check configuration from plugin parameters.
     */
    private SpellCheckConfiguration createConfiguration() {
        SpellCheckConfiguration config = new SpellCheckConfiguration();
        config.setLanguage(language);
        config.setEncoding(encoding != null ? encoding : "UTF-8");
        config.setCustomDictionary(customDictionary);
        config.setIgnoreWords(ignoreWords != null ? ignoreWords : new ArrayList<>());
        return config;
    }

    /**
     * Gets the list of files to spell check.
     */
    private List<File> getFilesToCheck() throws IOException {
        List<File> files = new ArrayList<>();

        // Determine source directories
        List<File> dirsToScan = new ArrayList<>();
        if (sourceDirectories != null && sourceDirectories.length > 0) {
            for (File dir : sourceDirectories) {
                if (dir.exists() && dir.isDirectory()) {
                    dirsToScan.add(dir);
                }
            }
        } else {
            // Default to project source directories
            File srcMainJava = new File(project.getBasedir(), "src/main/java");
            File srcTestJava = new File(project.getBasedir(), "src/test/java");
            File srcMainResources = new File(project.getBasedir(), "src/main/resources");

            if (srcMainJava.exists()) dirsToScan.add(srcMainJava);
            if (srcTestJava.exists()) dirsToScan.add(srcTestJava);
            if (srcMainResources.exists()) dirsToScan.add(srcMainResources);

            // Check for README and markdown files in root
            File readme = new File(project.getBasedir(), "README.md");
            if (readme.exists()) files.add(readme);
        }

        // Scan directories and collect files
        for (File dir : dirsToScan) {
            try (Stream<Path> paths = Files.walk(dir.toPath())) {
                paths.filter(Files::isRegularFile)
                     .filter(this::shouldCheckFile)
                     .forEach(p -> files.add(p.toFile()));
            }
        }

        return files;
    }

    /**
     * Determines if a file should be checked based on includes/excludes patterns.
     */
    private boolean shouldCheckFile(Path path) {
        String fileName = path.getFileName().toString();

        // Default file types to check
        if (fileName.endsWith(".java") ||
            fileName.endsWith(".md") ||
            fileName.endsWith(".txt") ||
            fileName.endsWith(".properties") ||
            fileName.endsWith(".xml")) {
            return true;
        }

        // TODO: Implement proper Ant-style pattern matching for includes/excludes
        return false;
    }

    /**
     * Generates the spell check report.
     */
    private void generateReport(SpellCheckReport report) throws IOException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File reportFile = new File(outputDirectory, "spellcheck-report.txt");
        report.writeToFile(reportFile);

        getLog().info("Report generated at: " + reportFile.getAbsolutePath());
    }

    /**
     * Logs the spell check summary.
     */
    private void logSummary(SpellCheckReport report) {
        getLog().info("========================================");
        getLog().info("Spell Check Summary");
        getLog().info("========================================");
        getLog().info("Files checked: " + report.getFilesChecked());
        getLog().info("Errors found: " + report.getErrorCount());

        if (report.hasErrors()) {
            getLog().warn("Spell check completed with errors!");
        } else {
            getLog().info("Spell check completed successfully!");
        }
        getLog().info("========================================");
    }
}
