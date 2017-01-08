package com.github.kevbradwick.gizmo.core;

import com.github.kevbradwick.gizmo.core.io.FileUtil;
import com.github.kevbradwick.gizmo.core.io.FileWriter;
import com.github.kevbradwick.gizmo.core.io.Writer;
import com.github.kevbradwick.gizmo.core.theme.DefaultTheme;
import com.github.kevbradwick.gizmo.core.theme.Theme;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gizmo {

    private static final Logger logger = LogManager.getLogger(Gizmo.class);

    /**
     * Source destination directory that contains the gherkin files
     */
    private final File sourceDirectory;

    /**
     * The directory all html files will be written to
     */
    private final File destinationDirectory;

    /**
     * Runtime configuration
     */
    private static final Config config = ConfigFactory.load();

    /**
     * Thymeleaf template engine
     */
    private TemplateEngine templateEngine;

    /**
     * The output writer interface
     */
    private Writer writer;

    /**
     * Project theme
     */
    private Theme theme = new DefaultTheme();

    /**
     * File helper utility class
     */
    private FileUtil fileUtil = new FileUtil();

    /**
     * Create a new instance of Gizmo
     *
     * @param sourceDirectory the directory containing gherkin files
     * @param destinationDirectory destination output directory
     * @param templateEngine the thymeleaf template engine to use
     */
    public Gizmo(File sourceDirectory, File destinationDirectory, TemplateEngine templateEngine) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        this.templateEngine = templateEngine;

        // default output writer
        FileWriter writer = new FileWriter();
        writer.setDestinationPath(destinationDirectory.toPath());
        this.writer = writer;
    }

    public Gizmo setTheme(Theme theme) {
        this.theme = theme;
        return this;
    }

    public Gizmo setWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    public Gizmo setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
        return this;
    }

    /**
     * Generate the html site from the source directory
     */
    public void process() {
        Util.assertNotNull(destinationDirectory, "No destination directory has been set");
        Util.assertNotNull(sourceDirectory, "No source directory has been set");

        final Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        final Map<String, String> index = new HashMap<>();

        // 2. loop through each file and parse it
        getFilePaths().forEach(p -> {
            try {
                GherkinDocument document = parser.parse(Files.newBufferedReader(p));

                // construct the relative output path and change file extension to .html
                String relativeOutputPath = sourceDirectory
                        .toURI()
                        .relativize(p.toUri())
                        .getPath()
                        .replaceAll("\\.feature$", ".html");

                // add file to index
                index.put(document.getFeature().getName(), relativeOutputPath);

                // calculate the directory depth
                StringBuilder sb = new StringBuilder();
                relativeOutputPath.chars().forEach(c -> {
                    if (c == File.separatorChar) {
                        sb.append("../");
                    }
                });

                // ensure directories exist to write the file to
                Path outputPath = Paths.get(destinationDirectory.getCanonicalPath(), relativeOutputPath);
                if (Files.exists(outputPath)) {
                    Files.delete(outputPath);
                }

                Context context = RenderContext.from(document)
                        .withPageTitle(document.getFeature().getName())
                        .withRelativeRootPath(sb.toString())
                        .build();

                String out = templateEngine.process(theme.getFeatureTemplateName(), context);
                logger.info("Generated output for {}", p);

                writer.write(out, relativeOutputPath);
            } catch (IOException | ParserException e) {
                logger.error("Unable to parse file {}", p);
                throw new RuntimeException(e);
            }
        });

        // create index.html
        Context context = new Context();
        context.setVariable("index", index);
        String indexSource = templateEngine.process(theme.getIndexTemplateName(), context);
        writer.write(indexSource, "index.html");

        // copy static assets
        theme.getStaticAssets().forEach(p -> {
            fileUtil.copyFile(p, Paths.get(destinationDirectory.toString(), "static", p.getFileName().toString()));
        });
    }

    /**
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
