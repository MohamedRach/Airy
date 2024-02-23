package com.example.airy.generator;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.commonmark.node.*;
import org.commonmark.parser.*;
import org.commonmark.renderer.html.*;
import org.thymeleaf.*;
import org.thymeleaf.context.*;
import org.thymeleaf.templateresolver.*;
import org.yaml.snakeyaml.*;

public class Generator {
    private String inputDirectory;
    private String outputDirectory;
    private String configFile;

    public Generator(String inputDirectory, String outputDirectory, String configFile) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.configFile = configFile;
    }

    public void generateFiles() {
        // Read YAML config
        Map<String, Object> yamlConfig = readYamlFile(configFile);
        
        generateIndex(yamlConfig);
        // Process blog posts
        //processBlogPosts();
    }

    private void generateIndex(Map<String, Object> config){
        Map<String, Object> blog = (Map<String, Object>) config.get("blog");
        String blogName = (String) blog.get("name");
        String profileImage = (String) blog.get("profileImage");
        String bio = (String) blog.get("bio");
        String twitterLink = (String) blog.get("twitter_link");
        String linkedinLink = (String) blog.get("linkedin_link");
        String githubLink = (String) blog.get("github_link");
        String stackoverflowLink = (String) blog.get("stackoverflow_link");
        String codepenLink = (String) blog.get("codepen_link");
        // Process index template
        Context indexContext = new Context();
        indexContext.setVariable("blogName", blogName);
        indexContext.setVariable("profileImage", profileImage);
        indexContext.setVariable("bio", bio);
        indexContext.setVariable("twitterLink", twitterLink);
        indexContext.setVariable("linkedinLink", linkedinLink);
        indexContext.setVariable("githubLink", githubLink);
        indexContext.setVariable("stackoverflowLink", stackoverflowLink);
        indexContext.setVariable("codepenLink", codepenLink);

        String indexHtml = processThymeleafTemplate("D:/web_dev/javaProject/airy/src/main/resources/templates/index.html", indexContext);

        // Write index HTML file
        writeHtmlFile(indexHtml, outputDirectory + "/index.html");
    }
    

    private Map<String, Object> readYamlConfig(String configFile) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(configFile))) {
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
    private static Map<String, Object> readYamlFile(String filePath) {
        try {
            
            return new Yaml().load(Files.newBufferedReader(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processBlogPosts() {
        try {
            Files.list(Paths.get(inputDirectory)).forEach(path -> {
                if (path.toString().endsWith(".md")) {
                    String markdownContent = readMarkdownFile(path.toString());

                    // Extract front matter data and Markdown content
                    Map<String, Object> frontMatter = extractFrontMatter(markdownContent);
                    String markdownBody = extractMarkdownBody(markdownContent);

                    // Convert Markdown to HTML
                    String htmlContent = convertMarkdownToHtml(markdownBody);

                    // Prepare Thymeleaf context with front matter and HTML content
                    Context context = new Context();
                    context.setVariable("frontMatter", frontMatter);
                    context.setVariable("markdownContent", htmlContent);

                    // Process Thymeleaf template
                    String outputHtml = processThymeleafTemplate("D:/web_dev/javaProject/airy/src/main/resources/templates/blog.html", context);

                    // Write output HTML to a file
                    writeHtmlFile(outputHtml, outputDirectory + "/" + path.getFileName().toString().replace(".md", ".html"));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readMarkdownFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Map<String, Object> extractFrontMatter(String markdownContent) {
        String frontMatterString = markdownContent.split("---")[1];
        return new Yaml().load(frontMatterString);
    }

    private String extractMarkdownBody(String markdownContent) {
        return markdownContent.split("---")[2].trim();
    }

    private String convertMarkdownToHtml(String markdownContent) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private String processThymeleafTemplate(String templatePath, Context context) {
        TemplateEngine templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine.process(templatePath, context);
    }

    private void writeHtmlFile(String content, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}