package com.github.kevbradwick.gizmo.core.theme;


import java.nio.file.Path;
import java.util.List;

public interface Theme {

    String getIndexTemplateName();

    String getFeatureTemplateName();

    List<Path> getStaticAssets();
}
