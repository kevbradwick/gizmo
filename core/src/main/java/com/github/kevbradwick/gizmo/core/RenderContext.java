package com.github.kevbradwick.gizmo.core;

import gherkin.ast.GherkinDocument;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RenderContext {

    /**
     *
     */
    private final Locale locale;

    /**
     * The parsed gherkin document
     */
    private final GherkinDocument document;

    /**
     * Page title
     */
    private String pageTitle;

    /**
     * Create a new render context.
     *
     * @param locale the locale
     * @param document the gherkin document
     */
    private RenderContext(final Locale locale, final GherkinDocument document) {
        this.locale = locale;
        this.document = document;
    }

    /**
     * Create a new render context.
     *
     * @param document the gherkin document
     * @return a new RenderContext with default locale
     */
    public static RenderContext from(GherkinDocument document) {
        return new RenderContext(Locale.getDefault(), document);
    }

    /**
     * Create a new render context.
     *
     * @param locale the locale
     * @param document the gherkin document
     * @return a new RenderContext
     */
    public static RenderContext from(Locale locale, GherkinDocument document) {
        return new RenderContext(locale, document);
    }

    /**
     * @param title the page title
     * @return chainable render context
     */
    public RenderContext withPageTitle(String title) {
        pageTitle = title;
        return this;
    }

    public Context build() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("pageTitle", pageTitle);
        vars.put("document", document);

        return new Context(locale, vars);
    }
}
