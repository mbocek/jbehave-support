package org.jbehavesupport.core.file;

import static org.jbehavesupport.core.internal.MetadataUtil.userDefined;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.jbehavesupport.core.TestContext;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class FileSteps {

    @Autowired
    private TestContext testContext;

    @When("a file with the [$extension] extension is created and the file path is stored as [$alias]: $content")
    @Given("a file with the [$extension] extension is created and the file path is stored as [$alias]: $content")
    public void createFileToContext(String extension, String alias, String content) {
        try {
            File tempFile = File.createTempFile("test", extension);
            tempFile.deleteOnExit();
            FileUtils.writeStringToFile(tempFile, content);
            testContext.put(alias, tempFile.getCanonicalPath(), userDefined());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
