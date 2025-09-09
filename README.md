# Spellcheck Maven Plugin

A Maven plugin for spell checking project files.

## Usage

Add the plugin to your `pom.xml`:

```xml
<plugin>
    <groupId>com.github.tizuno</groupId>
    <artifactId>spellcheck-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Goals

- `spellcheck:check` - Performs spell checking on project files

## Configuration

- `sourceDirectory` - Directory to check for spelling errors (default: `${project.basedir}/src`)
- `includes` - File extensions to include in spell checking (default: `java,md,txt`)
- `skip` - Skip spell checking (default: `false`)

Example configuration:

```xml
<plugin>
    <groupId>com.github.tizuno</groupId>
    <artifactId>spellcheck-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <sourceDirectory>${project.basedir}/src/main</sourceDirectory>
        <includes>java,md,txt,properties</includes>
        <skip>false</skip>
    </configuration>
</plugin>
```

## Building the Plugin

```bash
mvn clean install
```

## License

Licensed under the Apache License, Version 2.0.
