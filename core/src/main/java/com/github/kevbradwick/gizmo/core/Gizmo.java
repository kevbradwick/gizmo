package com.github.kevbradwick.gizmo.core;

import java.io.File;

public class Gizmo {

    final private File sourceDirectory;

    final private File destinationDirectory;

    /**
     * Create a new instance of Gizmo
     *
     * @param sourceDirectory the directory containing gherkin files
     * @param destinationDirectory destination output directory
     */
    public Gizmo(File sourceDirectory, File destinationDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
    }

    /**
     * @param sourceDirectory absolute path to directory containing gherkin files
     * @param destinationDirectory absolute path to the directory to output site to
     */
    public Gizmo(String sourceDirectory, String destinationDirectory) {
        this(new File(sourceDirectory), new File(destinationDirectory));
    }

    /**
     * Generate the html site from the source directory
     */
    public void process() {

    }
}
