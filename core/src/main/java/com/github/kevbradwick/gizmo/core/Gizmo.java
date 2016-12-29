package com.github.kevbradwick.gizmo.core;

import java.io.File;

public class Gizmo {

    final private File sourceDirectory;

    final private File destinationDirectory;

    public Gizmo(File sourceDirectory, File destinationDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
    }
}
