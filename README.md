# spellcheck-maven-plugin

A Maven plugin for spell-checking source files, documentation, and comments in your Maven projects.

## Features

- Spell-checking for Java source files, documentation, and other text files
- Support for CSpell configuration files (`cspell.json`, `.cspell.json`)
- Customizable dictionaries and ignore words
- Integration with Maven build lifecycle
- Configurable file patterns for inclusion and exclusion
- Detailed reporting of spelling errors

## Usage

### Basic Configuration

Add the plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.tizuno</groupId>
            <artifactId>spellcheck-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### CSpell Configuration File Support

The plugin automatically detects and uses CSpell configuration files in your project root. Supported filenames:
- `cspell.json`
- `.cspell.json`
- `cSpell.json`
- `.cSpell.json`

#### Example CSpell Configuration

Create a `cspell.json` file in your project root:

```json
{
  "$schema": "https://raw.githubusercontent.com/streetsidesoftware/cspell/main/cspell.schema.json",
  "version": "0.2",
  "language": "en-US",
  "words": [
    "spellcheck",
    "customword"
  ],
  "ignoreWords": [
    "TODO",
    "FIXME"
  ],
  "ignorePaths": [
    "node_modules/**",
    "target/**",
    "*.min.js"
  ],
  "files": [
    "**/*.java",
    "**/*.md",
    "**/*.txt"
  ]
}
```

See [`cspell.json.example`](cspell.json.example) for a complete example.

### Plugin Configuration Options

You can configure the plugin in your `pom.xml`:

```xml
<plugin>
    <groupId>com.github.tizuno</groupId>
    <artifactId>spellcheck-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <configuration>
        <!-- Enable/disable CSpell config file detection -->
        <useCSpellConfig>true</useCSpellConfig>

        <!-- Specify custom CSpell config file path -->
        <cspellConfigFile>${project.basedir}/custom-cspell.json</cspellConfigFile>

        <!-- Language for spell checking (overrides CSpell config) -->
        <language>en-US</language>

        <!-- File encoding -->
        <encoding>UTF-8</encoding>

        <!-- Custom dictionary file -->
        <customDictionary>${project.basedir}/custom-dictionary.txt</customDictionary>

        <!-- Words to ignore (merged with CSpell config) -->
        <ignoreWords>
            <ignoreWord>customword1</ignoreWord>
            <ignoreWord>customword2</ignoreWord>
        </ignoreWords>

        <!-- Fail build on errors -->
        <failOnError>true</failOnError>

        <!-- Skip spell check -->
        <skip>false</skip>

        <!-- Generate report -->
        <generateReport>true</generateReport>
    </configuration>
</plugin>
```

### Command Line Usage

Run spell check manually:

```bash
mvn spellcheck:check
```

Skip spell check:

```bash
mvn verify -Dspellcheck.skip=true
```

Don't fail build on errors:

```bash
mvn verify -Dspellcheck.failOnError=false
```

Specify custom CSpell config:

```bash
mvn spellcheck:check -Dspellcheck.cspellConfig=path/to/cspell.json
```

Disable CSpell config file detection:

```bash
mvn spellcheck:check -Dspellcheck.useCSpellConfig=false
```

## CSpell Integration

### Supported CSpell Features

The plugin supports the following CSpell configuration properties:

- `version`: Configuration format version
- `language`: Language locale(s) for spell checking
- `words`: List of words to be considered correct
- `ignoreWords`: List of words to be ignored
- `ignorePaths`: Glob patterns of files to be ignored
- `files`: Glob patterns of files to be checked
- `dictionaries`: List of dictionaries to use
- `dictionaryDefinitions`: Custom dictionary definitions
- `patterns`: Named patterns for regex matching
- `ignoreRegExpList`: Regular expressions to exclude from checking
- `includeRegExpList`: Regular expressions to include in checking
- `import`: Import other CSpell configuration files
- `enabled`: Enable/disable spell checking
- `caseSensitive`: Case-sensitive matching
- `allowCompoundWords`: Allow compound words
- `flagWords`: Words that are always considered incorrect
- `overrides`: File-specific configuration overrides

### Configuration Priority

When both CSpell configuration file and Maven plugin configuration are present:

1. CSpell configuration file is loaded first (if `useCSpellConfig=true`)
2. Maven plugin parameters override CSpell settings
3. For list properties (like `ignoreWords`), both are merged

### Import Support

The plugin supports CSpell's `import` feature to load configuration from other files:

```json
{
  "version": "0.2",
  "import": [
    "./base-cspell.json",
    "../shared-config.json"
  ],
  "words": [
    "projectspecific"
  ]
}
```

## Reports

The plugin generates a spell check report in the `target/spellcheck` directory:
- `spellcheck-report.txt`: Text report with all spelling errors

## Requirements

- Java 8 or higher
- Maven 3.6.0 or higher

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Links

- [CSpell Documentation](https://cspell.org/)
- [CSpell Configuration](https://cspell.org/configuration/)
- [Maven Plugin Development](https://maven.apache.org/plugin-developers/)
