package com.github.kevbradwick.gizmo.core.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter implements Writer {

    private static final Logger logger = LogManager.getLogger(FileWriter.class);

    /**
     * Files will be written to this directory.
     */
    private final Path destinationDirectory;

    /**
     * Creates an instance of a FileWriter.
     *
     * @param destinationDirectory the output file
     */
    public FileWriter(Path destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    @Override
    public void write(String content, String fileName) {
        try {
            Path to = Paths.get(destinationDirectory.toString(), fileName);
            Files.createDirectories(to.getParent());
            Files.write(to, content.getBytes());
        } catch (IOException e) {
            logger.error("Unable to write to {}", destinationDirectory);
            throw new RuntimeException(e);
        }
    }
}
