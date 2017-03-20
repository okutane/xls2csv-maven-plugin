package ru.urururu;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
@Mojo(name = "xls2csv")
public class Xls2Csv extends AbstractMojo {
    @Parameter(required = true)
    private File targetDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!this.targetDirectory.isDirectory()) {
                throw new MojoFailureException("No such directory: " + targetDirectory);
            } else {
                Xls2CsvConverter.convert(getLog(), targetDirectory);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}