# Spellcheck Maven Plugin

Spellcheck Maven Plugin is a Java-based Maven plugin for performing spellcheck validation on project files during the Maven build process. This plugin provides spell checking capabilities for documentation, source code comments, and other text files in Maven projects.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Current Repository State

**IMPORTANT**: This repository is in early development and currently contains only a README.md file. The Maven plugin project structure does not exist yet but can be bootstrapped using the commands below.

- Repository currently has: README.md only
- Expected after bootstrapping: Complete Maven plugin project with src/, pom.xml, and package structure
- The archetype generation command will create the full project structure when needed

## Working Effectively

### Environment Setup
The repository currently contains only a README.md file. To bootstrap the Maven plugin project structure:

- Verify Java and Maven are available:
  - `java -version` -- should show OpenJDK 17.0.16+
  - `mvn -version` -- should show Apache Maven 3.9.11
- Bootstrap the Maven plugin project structure:
  - `mvn archetype:generate -DgroupId=com.github.tizuno -DartifactId=spellcheck-maven-plugin -DarchetypeArtifactId=maven-archetype-plugin -DinteractiveMode=false`
  - Takes ~1 second. NEVER CANCEL.
  - This creates the standard Maven plugin directory structure with src/main/java and pom.xml

### Build and Test the Project
- Clean build: `mvn clean compile` -- takes ~1 second. NEVER CANCEL. Set timeout to 60+ seconds.
- Full build: `mvn clean install` -- takes ~2 seconds. NEVER CANCEL. Set timeout to 120+ seconds.
- Run tests: `mvn test` -- takes ~1 second. NEVER CANCEL. Set timeout to 60+ seconds.
- Package plugin: `mvn package` -- takes ~2 seconds. NEVER CANCEL. Set timeout to 60+ seconds.

### Run the Plugin
After building:
- Execute plugin goal: `mvn com.github.tizuno:spellcheck-maven-plugin:1.0-SNAPSHOT:[goal-name]`
- List available goals: `mvn help:describe -Dplugin=com.github.tizuno:spellcheck-maven-plugin`

### Development Workflow
- Maven plugins are developed using the standard Maven directory structure
- Main plugin code goes in `src/main/java/com/github/tizuno/`
- Plugin configuration and Mojo classes extend `org.apache.maven.plugin.AbstractMojo`
- Use `@Mojo(name = "goal-name")` annotations for goal definitions
- Plugin descriptor is auto-generated during build

## Validation

### Comprehensive End-to-End Validation Scenarios
ALWAYS perform these complete validation workflows after making any changes to ensure the plugin works correctly:

#### Scenario 1: Plugin Development and Execution
1. Create plugin project: `mvn archetype:generate -DgroupId=com.github.tizuno -DartifactId=spellcheck-maven-plugin -DarchetypeArtifactId=maven-archetype-plugin -DinteractiveMode=false`
2. Navigate to project: `cd spellcheck-maven-plugin`
3. Modify `src/main/java/com/github/tizuno/MyMojo.java` to implement spellcheck functionality
4. Build plugin: `mvn clean install`
5. Test plugin execution: `mvn com.github.tizuno:spellcheck-maven-plugin:1.0-SNAPSHOT:spellcheck`
6. Verify expected output messages appear in logs

#### Scenario 2: External Project Integration
1. Create test project: `mvn archetype:generate -DgroupId=com.test -DartifactId=sample-project -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`
2. Navigate to test project: `cd sample-project`
3. Execute spellcheck plugin: `mvn com.github.tizuno:spellcheck-maven-plugin:1.0-SNAPSHOT:spellcheck`
4. Test with skip parameter: `mvn com.github.tizuno:spellcheck-maven-plugin:1.0-SNAPSHOT:spellcheck -Dspellcheck.skip=true`
5. Verify both normal execution and skip functionality work correctly

### Manual Testing Requirements
- ALWAYS test the plugin by installing it locally with `mvn install`
- ALWAYS test plugin execution with a sample Maven project
- Create a test project in `/tmp` and configure the plugin in its pom.xml
- Verify the plugin executes without errors and produces expected output
- Test different configuration scenarios for comprehensive validation

### Build Validation
- ALWAYS run `mvn clean compile` first to check for compilation errors
- ALWAYS run `mvn test` to execute unit tests (when they exist)
- ALWAYS run `mvn install` before testing plugin functionality
- Validate that the generated JAR contains the plugin.xml descriptor

### Critical Timing Information
- **NEVER CANCEL builds or tests** - they typically complete within 5 seconds
- Maven archetype generation: Set timeout to **60+ seconds**
- Maven compile: Set timeout to **60+ seconds**  
- Maven full build (clean install): Set timeout to **120+ seconds**
- Maven test execution: Set timeout to **60+ seconds**
- Plugin execution: Set timeout to **30+ seconds**

## Common Tasks

### Repository Structure
```
ls -la [repo-root]
.
..
.git/
.github/
README.md
pom.xml (after bootstrapping)
src/ (after bootstrapping)
  main/
    java/
      com/
        github/
          tizuno/
            SpellcheckMojo.java (plugin implementation)
target/ (after building)
```

### Standard Maven Plugin pom.xml Structure
```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.github.tizuno</groupId>
  <artifactId>spellcheck-maven-plugin</artifactId>
  <packaging>maven-plugin</packaging>
  <version>1.0-SNAPSHOT</version>
  
  <dependencies>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-plugin-api</artifactId>
      <version>3.9.0</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
</project>
```

### Essential Plugin Development Dependencies
```xml
<!-- Always include in Maven plugin projects -->
<dependency>
  <groupId>org.apache.maven</groupId>
  <artifactId>maven-plugin-api</artifactId>
  <version>3.9.0</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>org.apache.maven.plugin-tools</groupId>
  <artifactId>maven-plugin-annotations</artifactId>
  <version>3.9.0</version>
  <scope>provided</scope>
</dependency>
```

### Modern Mojo Implementation Template (Recommended)
```java
package com.github.tizuno;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "spellcheck")
public class SpellcheckMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    @Parameter(property = "spellcheck.skip", defaultValue = "false")
    private boolean skip;
    
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Spellcheck is skipped.");
            return;
        }
        
        getLog().info("Running spellcheck validation...");
        // Plugin logic here
        getLog().info("Spellcheck completed successfully.");
    }
}
```

### Legacy Mojo Implementation (Generated by Archetype)
```java
/**
 * Goal which performs spellcheck validation.
 *
 * @goal spellcheck
 * @phase validate
 */
public class SpellcheckMojo extends AbstractMojo {
    
    /**
     * Skip spellcheck execution.
     * @parameter property="spellcheck.skip" default-value="false"
     */
    private boolean skip;
    
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Spellcheck is skipped.");
            return;
        }
        
        getLog().info("Running spellcheck validation...");
        getLog().info("Spellcheck completed successfully.");
    }
}
```

## Development Environment Information

### Available Tools
- **Java**: OpenJDK 17.0.16 (Temurin) at `/usr/bin/java`
- **Maven**: Apache Maven 3.9.11 at `/usr/bin/mvn`
- **Git**: Git 2.51.0 at `/usr/bin/git`

### Maven Configuration
- Maven home: `/usr/share/apache-maven-3.9.11`
- Local repository: `~/.m2/repository`
- Default locale: en, platform encoding: UTF-8
- OS: Linux (Ubuntu), arch: amd64

### Project State
- **Current State**: Repository contains only README.md
- **Expected Evolution**: Will become a full Maven plugin project with standard structure
- **Target Functionality**: Spellcheck validation for Maven projects
- **Build System**: Maven with maven-plugin packaging

### Key Spellcheck Plugin Requirements
- Support for multiple file types (Java, MD, TXT, etc.)
- Configurable word dictionaries and exclusion lists
- Integration with Maven build lifecycle
- Detailed reporting of spelling errors
- Skip functionality for CI/CD scenarios
- Proper error handling and logging

### Critical Development Notes
- ALWAYS use `<scope>provided</scope>` for Maven API dependencies
- ALWAYS use `@Parameter` annotations for plugin configuration
- ALWAYS extend `AbstractMojo` for plugin implementation
- ALWAYS test plugin execution after any changes
- NEVER commit without validating the plugin builds and runs successfully