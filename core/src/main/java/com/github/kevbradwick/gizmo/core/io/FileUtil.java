package com.github.kevbradwick.gizmo.core.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    public boolean copyFile(Path from, Path to) {
        try {
            Files.copy(Files.newInputStream(from), to, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            // todo logging
            return false;
        }
    }
}
