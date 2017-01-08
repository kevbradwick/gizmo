package com.github.kevbradwick.gizmo.core.theme;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultTheme implements Theme {

    @Override
    public List<Path> getStaticAssets() {
        return Arrays.stream(new String[] {"style.css", "script.js"}).map(n -> {
            try {
                return Paths.get(getClass().getResource("/gizmo/default-theme/static/" + n).toURI());
            } catch (URISyntaxException e) {
                // todo logging
                return null;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String getIndexTemplateName() {
        return "index";
    }

    @Override
    public String getFeatureTemplateName() {
        return "feature";
    }
}
