package com.example.airy;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.example.airy.generator.Generator;



public class Main {

    public static void main(String[] args) {

        if (args.length > 0 && args[0].equals("start")) {
            createTemplateFolderStructure();
        }else if (args.length > 0 && args[0].equals("build")) {
            String currentDir = System.getProperty("user.dir");
            String inputFolder = currentDir + "/input";
            String outputFolder = currentDir + "/output";
            String templatesFolder = currentDir + "/templates";
            String configFile = currentDir + "/config.yaml";
            Generator gen = new Generator(inputFolder, outputFolder, configFile, templatesFolder); 
            gen.generateFiles();
            HttpServer server = null;
            try{
                server = HttpServer.create(new InetSocketAddress(8080), 0);
            } catch(IOException e){
                throw new RuntimeException(e);
            }
            

            // Set up context for handling requests
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    // Get the requested file path
                    String requestedFile = exchange.getRequestURI().getPath().substring(1); // Remove leading slash

                    // If the requested file is empty, default to index.html
                    if (requestedFile.isEmpty()) {
                        requestedFile = "index.html";
                    }

                    // Load the requested file
                    Path filePath = Paths.get(outputFolder, requestedFile);
                    byte[] fileBytes;
                    try {
                        fileBytes = Files.readAllBytes(filePath);
                    } catch (IOException e) {
                        // If the file does not exist, return a 404 Not Found response
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }

                    // Set response headers
                    String contentType = getContentType(requestedFile);
                    exchange.getResponseHeaders().add("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, fileBytes.length);

                    // Write the file content to the response body
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(fileBytes);
                    responseBody.close();
                }
            });

            // Start the server
            server.start();

            // Print a message to indicate that the server has started
            System.out.println("Server is listening on port " + "8080");
        }
        
    }
    // Utility method to determine the content type based on file extension
    private static String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
    private static void createTemplateFolderStructure() {
        // Create resources directory
        File inputDir = new File("input");
        inputDir.mkdirs();
        File outputDir = new File("output");
        outputDir.mkdirs();
        File assetsFolder = new File(outputDir, "assets");
        assetsFolder.mkdirs();
        // Create template folder
        File templateDir = new File("templates");
        templateDir.mkdirs();
        
        // Copy index.html
        File indexHtmlSrc = new File("D:/web_dev/javaProject/airy/src/main/resources/templates/index.html");
        File indexHtmlDest = new File(templateDir, "index.html");
        copyFile(indexHtmlSrc, indexHtmlDest);
        // Copy about.html
         File aboutHtmlSrc = new File("D:/web_dev/javaProject/airy/src/main/resources/templates/about.html");
        File aboutHtmlDest = new File(templateDir, "about.html");
        copyFile(aboutHtmlSrc, aboutHtmlDest);
        // Copy blog.html
        File blogHtmlSrc = new File("D:/web_dev/javaProject/airy/src/main/resources/templates/blog.html");
        File blogHtmlDest = new File(templateDir, "blog.html");
        copyFile(blogHtmlSrc, blogHtmlDest);
        File sourceDir = new File("D:/web_dev/javaProject/airy/src/main/resources/output/assets");
        try {
            // Copy all files from source directory to destination directory
            Files.walk(sourceDir.toPath())
                 .forEach(source -> {
                    Path destination = assetsFolder.toPath().resolve(sourceDir.toPath().relativize(source));
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            
            System.out.println("Assets copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create config.yaml file
        File configFile = new File("config.yaml");
        try {
            FileWriter writer = new FileWriter(configFile);
            // Write your YAML configuration here
            writer.write("blog:\n  name: 'your blogs name'\n  author: 'author here'\n  bio: 'Bio here'\n  twitter_link: '#'\n  linkedin_link: '#'\n  github_link: '#'\n  stackoverflow_link: '#'\n  codepen_link: '#'");
            writer.close();
            System.out.println("config.yaml file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Template folder structure created successfully.");
    }
    private static void copyFile(File src, File dest) {
        try (FileReader reader = new FileReader(src);
             FileWriter writer = new FileWriter(dest)) {
            int c;
            while ((c = reader.read()) != -1) {
                writer.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
}
