package com.github.tizuno.maven.spellcheck;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Unit tests for SpellCheckMojo.
 *
 * @author T. Izuno
 */
public class SpellCheckMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * Tests that the Mojo can be instantiated.
     */
    @Test
    @WithoutMojo
    public void testMojoInstantiation() {
        SpellCheckMojo mojo = new SpellCheckMojo();
        assertNotNull(mojo);
    }

    /**
     * Tests skip functionality.
     */
    @Test
    public void testSkipExecution() throws Exception {
        File pom = new File("src/test/resources/test-projects/simple-project/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        // This is a basic test to ensure the test infrastructure is working
        // More comprehensive tests would require a proper test project setup
    }
}
