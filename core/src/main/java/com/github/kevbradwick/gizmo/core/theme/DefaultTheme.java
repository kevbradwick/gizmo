package com.github.kevbradwick.gizmo.core.theme;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class DefaultTheme implements Theme {

    @Override
    public void copyStaticAssets(Path destination) {
        Path staticDir = Paths.get(destination.toString(), "static");
        ensureDirectories(staticDir);

        Arrays.stream(new String[]{"style.css", "script.js"}).forEach(fileName -> {
            try {
                Files.copy(
                    getClass().getResourceAsStream("/gizmo/default-theme/static/" + fileName),
                    Paths.get(staticDir.toString(), fileName),
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (FileAlreadyExistsException e) {
                // todo logging...
            } catch (Exception e) {
                // todo logging...
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String getIndexTemplateName() {
        return "index";
    }

    @Override
    public String getFeatureTemplateName() {
        return "feature";
    }

    private void ensureDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // todo logging...
            throw new RuntimeException(e);
        }
    }
}
