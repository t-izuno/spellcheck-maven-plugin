package io.nncdevel.maven.spellcheck.config;

import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for CSpellConfigLoader.
 *
 * @author T. Izuno
 * @since 1.0.0
 */
public class CSpellConfigLoaderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Log log;
    private CSpellConfigLoader loader;

    @Before
    public void setUp() {
        log = mock(Log.class);
        loader = new CSpellConfigLoader(log);
    }

    @Test
    public void testLoadBasicConfig() throws IOException {
        // Create a basic cspell.json file
        String configJson = "{\n" +
                "  \"version\": \"0.2\",\n" +
                "  \"language\": \"en-US\",\n" +
                "  \"words\": [\"testword\", \"anotherword\"],\n" +
                "  \"ignoreWords\": [\"ignoreword\"]\n" +
                "}";

        File configFile = tempFolder.newFile("cspell.json");
        Files.write(configFile.toPath(), configJson.getBytes(StandardCharsets.UTF_8));

        CSpellConfig config = loader.loadConfig(configFile);

        assertNotNull(config);
        assertEquals("0.2", config.getVersion());
        assertEquals("en-US", config.getLanguage());
        assertEquals(2, config.getWords().size());
        assertTrue(config.getWords().contains("testword"));
        assertTrue(config.getWords().contains("anotherword"));
        assertEquals(1, config.getIgnoreWords().size());
        assertTrue(config.getIgnoreWords().contains("ignoreword"));
    }

    @Test
    public void testLoadConfigWithIgnorePaths() throws IOException {
        String configJson = "{\n" +
                "  \"version\": \"0.2\",\n" +
                "  \"ignorePaths\": [\"node_modules/**\", \"*.min.js\"]\n" +
                "}";

        File configFile = tempFolder.newFile("cspell.json");
        Files.write(configFile.toPath(), configJson.getBytes(StandardCharsets.UTF_8));

        CSpellConfig config = loader.loadConfig(configFile);

        assertNotNull(config);
        assertEquals(2, config.getIgnorePaths().size());
        assertTrue(config.getIgnorePaths().contains("node_modules/**"));
        assertTrue(config.getIgnorePaths().contains("*.min.js"));
    }

    @Test
    public void testLoadConfigWithDictionaries() throws IOException {
        String configJson = "{\n" +
                "  \"version\": \"0.2\",\n" +
                "  \"dictionaries\": [\"custom\", \"companies\"],\n" +
                "  \"dictionaryDefinitions\": [\n" +
                "    {\n" +
                "      \"name\": \"custom\",\n" +
                "      \"path\": \"./custom-dictionary.txt\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        File configFile = tempFolder.newFile("cspell.json");
        Files.write(configFile.toPath(), configJson.getBytes(StandardCharsets.UTF_8));

        CSpellConfig config = loader.loadConfig(configFile);

        assertNotNull(config);
        assertEquals(2, config.getDictionaries().size());
        assertTrue(config.getDictionaries().contains("custom"));
        assertTrue(config.getDictionaries().contains("companies"));
        assertEquals(1, config.getDictionaryDefinitions().size());
        assertEquals("custom", config.getDictionaryDefinitions().get(0).getName());
        assertEquals("./custom-dictionary.txt", config.getDictionaryDefinitions().get(0).getPath());
    }

    @Test
    public void testFindConfigInDirectory() throws IOException {
        String configJson = "{\n" +
                "  \"version\": \"0.2\",\n" +
                "  \"language\": \"en\"\n" +
                "}";

        File dir = tempFolder.newFolder();
        File configFile = new File(dir, "cspell.json");
        Files.write(configFile.toPath(), configJson.getBytes(StandardCharsets.UTF_8));

        CSpellConfig config = loader.loadConfig(dir);

        assertNotNull(config);
        assertEquals("0.2", config.getVersion());
        assertEquals("en", config.getLanguage());
    }

    @Test
    public void testFindConfigWithDotPrefix() throws IOException {
        String configJson = "{\n" +
                "  \"version\": \"0.2\",\n" +
                "  \"language\": \"en\"\n" +
                "}";

        File dir = tempFolder.newFolder();
        File configFile = new File(dir, ".cspell.json");
        Files.write(configFile.toPath(), configJson.getBytes(StandardCharsets.UTF_8));

        CSpellConfig config = loader.loadConfig(dir);

        assertNotNull(config);
        assertEquals("en", config.getLanguage());
    }

    @Test
    public void testNoConfigFound() throws IOException {
        File dir = tempFolder.newFolder();

        CSpellConfig config = loader.loadConfig(dir);

        assertNull(config);
    }

    @Test
    public void testToSpellCheckConfiguration() {
        CSpellConfig cspellConfig = new CSpellConfig();
        cspellConfig.setLanguage("en-US");
        cspellConfig.getWords().add("customword");
        cspellConfig.getIgnoreWords().add("ignoreword");

        SpellCheckConfiguration config = loader.toSpellCheckConfiguration(cspellConfig);

        assertNotNull(config);
        assertEquals("en-US", config.getLanguage());
        assertEquals(2, config.getIgnoreWords().size());
        assertTrue(config.getIgnoreWords().contains("customword"));
        assertTrue(config.getIgnoreWords().contains("ignoreword"));
    }

    @Test
    public void testToSpellCheckConfigurationWithMultipleLanguages() {
        CSpellConfig cspellConfig = new CSpellConfig();
        cspellConfig.setLanguage("en,nl,de");

        SpellCheckConfiguration config = loader.toSpellCheckConfiguration(cspellConfig);

        assertNotNull(config);
        // Should use the first language
        assertEquals("en", config.getLanguage());
    }

    @Test
    public void testToSpellCheckConfigurationWithNull() {
        SpellCheckConfiguration config = loader.toSpellCheckConfiguration(null);

        assertNotNull(config);
        // Should return default configuration
    }
}
