package ru.urururu.xls2csv;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class Xls2CsvTest {
    @Test
    public void testBadDirectory() throws IOException, MojoExecutionException {
        Xls2Csv xls2Csv = new Xls2Csv();
        xls2Csv.targetDirectory = File.createTempFile("prefix", "suffix");
        try {
            xls2Csv.execute();

            fail("unreachable");
        } catch (MojoFailureException e) {
            // expected
        }
    }
}