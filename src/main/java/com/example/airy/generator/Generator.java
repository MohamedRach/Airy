package com.example.airy.generator;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.commonmark.node.*;
import org.commonmark.parser.*;
import org.commonmark.renderer.html.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.*;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.yaml.snakeyaml.*;

public class Generator {
    private String inputDirectory;
    private String outputDirectory;
    private String configFile;
    private String templatesDirectory;
    private String blogName;
    private String profileImage;
    private String bio;
    private String twitterLink;
    private String linkedinLink;
    private String githubLink;
    private String stackoverflowLink;
    private String codepenLink;


    public Generator(String inputDirectory, String outputDirectory, String configFile, String templatesDirectory) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.configFile = configFile;
        this.templatesDirectory = templatesDirectory;
    }

    public void generateFiles() {
        // Read YAML config
        Map<String, Object> yamlConfig = readYamlFile(configFile);
        
        generateIndex(yamlConfig);
        // Process blog posts
        processBlogPosts();
        generateAboutMe();
    }

    private void generateAboutMe(){
        String markdownContent = readMarkdownFile(this.inputDirectory + "/about.md");
        // Extract front matter data and Markdown content
        Map<String, Object> frontMatter = extractFrontMatter(markdownContent);
        String markdownBody = extractMarkdownBody(markdownContent);

        // Convert Markdown to HTML
        String htmlContent = convertMarkdownToHtml(markdownBody);

                        // Prepare Thymeleaf context with front matter and HTML content
        Context context = new Context();
        context.setVariable("blogName", this.blogName);
        context.setVariable("profileImage", this.profileImage);
        context.setVariable("bio", this.bio);
        context.setVariable("twitterLink", this.twitterLink);
        context.setVariable("linkedinLink", this.linkedinLink);
        context.setVariable("githubLink", this.githubLink);
        context.setVariable("stackoverflowLink", this.stackoverflowLink);
        context.setVariable("codepenLink", this.codepenLink);
        context.setVariable("frontMatter", frontMatter);
        context.setVariable("markdownContent", htmlContent);

        // Process Thymeleaf template
        String outputHtml = processThymeleafTemplate(templatesDirectory +"/about.html", context);

                        // Write output HTML to a file
        writeHtmlFile(outputHtml, outputDirectory + "/" + "about.html");
    }

    private void generateIndex(Map<String, Object> config){
        @SuppressWarnings("unchecked")
        Map<String, Object> blog = (Map<String, Object>) config.get("blog");
        this.blogName = (String) blog.get("name");
        this.profileImage = (String) blog.get("profileImage");
        this.bio = (String) blog.get("bio");
        this.twitterLink = (String) blog.get("twitter_link");
        this.linkedinLink = (String) blog.get("linkedin_link");
        this.githubLink = (String) blog.get("github_link");
        this.stackoverflowLink = (String) blog.get("stackoverflow_link");
        this.codepenLink = (String) blog.get("codepen_link");
        List<BlogPost> blogPosts = new ArrayList<>();
        try {
            Files.list(Paths.get(inputDirectory)).forEach(path -> {
                if (path.toString().endsWith(".md")) {
                    String markdownContent = readMarkdownFile(path.toString());
                    Map<String, Object> frontMatter = extractFrontMatter(markdownContent);

                    BlogPost blogPost = new BlogPost();
                    blogPost.setTitle((String) frontMatter.get("title"));
                    blogPost.setDescription((String) frontMatter.get("description"));
                    blogPost.setPublishedDate((Date) frontMatter.get("date"));
                    blogPost.setImg((String) frontMatter.get("image"));
                    blogPost.setLink(path.getFileName().toString().replace(".md", ".html"));
                    blogPosts.add(blogPost);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Process index template
        Context context = new Context();
        context.setVariable("blogName", this.blogName);
        context.setVariable("profileImage", this.profileImage);
        context.setVariable("bio", this.bio);
        context.setVariable("twitterLink", this.twitterLink);
        context.setVariable("linkedinLink", this.linkedinLink);
        context.setVariable("githubLink", this.githubLink);
        context.setVariable("stackoverflowLink", this.stackoverflowLink);
        context.setVariable("codepenLink", this.codepenLink);
        context.setVariable("blogPosts", blogPosts);
        String indexHtml = processThymeleafTemplate(templatesDirectory +"/index.html", context);

        // Write index HTML file
        writeHtmlFile(indexHtml, outputDirectory + "/index.html");
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
                if (Objects.equals(path.getFileName().toString(), "about.md")){
                    return;
                } else {
                    if (path.toString().endsWith(".md")) {
                    
                        String markdownContent = readMarkdownFile(path.toString());

                        // Extract front matter data and Markdown content
                        Map<String, Object> frontMatter = extractFrontMatter(markdownContent);
                        String markdownBody = extractMarkdownBody(markdownContent);

                        // Convert Markdown to HTML
                        String htmlContent = convertMarkdownToHtml(markdownBody);

                        // Prepare Thymeleaf context with front matter and HTML content
                        Context context = new Context();
                        context.setVariable("blogName", this.blogName);
                        context.setVariable("profileImage", this.profileImage);
                        context.setVariable("bio", this.bio);
                        context.setVariable("twitterLink", this.twitterLink);
                        context.setVariable("linkedinLink", this.linkedinLink);
                        context.setVariable("githubLink", this.githubLink);
                        context.setVariable("stackoverflowLink", this.stackoverflowLink);
                        context.setVariable("codepenLink", this.codepenLink);
                        context.setVariable("frontMatter", frontMatter);
                        context.setVariable("markdownContent", htmlContent);

                        // Process Thymeleaf template
                        String outputHtml = processThymeleafTemplate(templatesDirectory +"/blog.html", context);

                        // Write output HTML to a file
                        writeHtmlFile(outputHtml, outputDirectory + "/" + path.getFileName().toString().replace(".md", ".html"));
                    }
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