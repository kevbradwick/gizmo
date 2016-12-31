package com.github.kevbradwick.gizmo.core;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class TemplateEngines {

    /**
     * The default engine uses Gizmo templates by default.
     *
     * @return a template engine
     */
    public static TemplateEngine defaultEngine() {
        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
