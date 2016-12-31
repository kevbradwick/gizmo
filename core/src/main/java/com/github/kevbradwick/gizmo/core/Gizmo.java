package com.github.kevbradwick.gizmo.core;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.ast.GherkinDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Gizmo {

    private static final Logger logger = LogManager.getLogger(Gizmo.class);

    private final File sourceDirectory;

    private final File destinationDirectory;

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

        final Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TemplateEngine engine = TemplateEngines.defaultEngine();

        // 1. get a list of all feature files
        List<Path> paths = getFilePaths();

        // 2. loop through each file and parse it
        paths.forEach(p -> {
            try {
                GherkinDocument document = parser.parse(Files.newBufferedReader(p));
                Context context = RenderContext.from(document)
                        .withPageTitle(document.getFeature().getName())
                        .build();
                String out = engine.process("feature", context);
                logger.info("Generated output for {}", p);

                // construct the relative output path and change file extension to .html
                String relativeOutputPath = sourceDirectory
                        .toURI()
                        .relativize(p.toUri())
                        .getPath()
                        .replaceAll("\\.feature$", ".html");

                Path outputPath = Paths.get(destinationDirectory.getCanonicalPath(), relativeOutputPath);
                if (Files.exists(outputPath)) {
                    Files.delete(outputPath);
                }
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, out.getBytes());
            } catch (IOException | ParserException e) {
                logger.error("Unable to parse file {}", p);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     *
     * @return a list of paths containing feature files.
     */
    private List<Path> getFilePaths() {
        final List<Path> files = new ArrayList<>();
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.feature");
        try {
            Files.walkFileTree(sourceDirectory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (matcher.matches(file)) {
                        logger.debug("found gherkin file {}", file);
                        files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error reading file from source directory. {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return files;
    }
}
