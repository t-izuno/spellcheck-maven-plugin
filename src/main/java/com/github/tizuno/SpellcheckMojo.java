package com.github.tizuno;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Goal which performs spell checking on project files.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
public class SpellcheckMojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Source directory to check for spelling errors.
     */
    @Parameter(defaultValue = "${project.basedir}/src", property = "spellcheck.sourceDirectory")
    private File sourceDirectory;

    /**
     * File extensions to include in spell checking.
     */
    @Parameter(defaultValue = "java,md,txt", property = "spellcheck.includes")
    private String includes;

    /**
     * Skip spell checking.
     */
    @Parameter(defaultValue = "false", property = "spellcheck.skip")
    private boolean skip;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Spellcheck is skipped.");
            return;
        }

        getLog().info("Starting spellcheck for project: " + project.getName());
        getLog().info("Checking directory: " + sourceDirectory.getAbsolutePath());

        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
            return;
        }

        try {
            List<String> extensions = Stream.of(includes.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<Path> filesToCheck = findFilesToCheck(sourceDirectory.toPath(), extensions);
            
            getLog().info("Found " + filesToCheck.size() + " files to check");
            
            int issuesFound = 0;
            for (Path file : filesToCheck) {
                getLog().debug("Checking file: " + file.toString());
                issuesFound += checkFile(file);
            }

            if (issuesFound > 0) {
                getLog().info("Spellcheck completed. Found " + issuesFound + " potential issues.");
            } else {
                getLog().info("Spellcheck completed successfully with no issues found.");
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error during spellcheck", e);
        }
    }

    private List<Path> findFilesToCheck(Path directory, List<String> extensions) throws IOException {
        return Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    return extensions.stream().anyMatch(ext -> fileName.endsWith("." + ext));
                })
                .collect(Collectors.toList());
    }

    private int checkFile(Path file) {
        // Basic implementation - just log that we're checking the file
        // In a real implementation, this would use a spell checking library
        getLog().debug("Spell checking file: " + file.toString());
        
        try {
            List<String> lines = Files.readAllLines(file);
            getLog().debug("File " + file.getFileName() + " has " + lines.size() + " lines");
        } catch (IOException e) {
            getLog().warn("Could not read file: " + file.toString() + " - " + e.getMessage());
        }
        
        // Return 0 issues for now (placeholder implementation)
        return 0;
    }
}
