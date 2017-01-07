package com.github.kevbradwick.gizmo.core.theme;


import java.nio.file.Path;

public interface Theme {

    String getIndexTemplateName();

    String getFeatureTemplateName();

    void copyStaticAssets(Path destination);
}
