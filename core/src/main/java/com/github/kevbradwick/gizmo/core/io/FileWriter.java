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
    private Path destinationPath;

    public FileWriter setDestinationPath(Path destinationPath) {
        this.destinationPath = destinationPath;
        return this;
    }

    @Override
    public void write(String content, String fileName) {
        if (destinationPath == null) {
            throw new RuntimeException("The FileWriter has not been set a destination path");
        }
        try {
            Path to = Paths.get(destinationPath.toString(), fileName);
            Files.createDirectories(to.getParent());
            Files.write(to, content.getBytes());
        } catch (IOException e) {
            logger.error("Unable to write to {}", destinationPath);
            throw new RuntimeException(e);
        }
    }
}
