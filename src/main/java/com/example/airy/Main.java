package com.example.airy;
import com.example.airy.generator.Generator;



public class Main {

    public static void main(String[] args) {
        Generator gen = new Generator("D:/web_dev/javaProject/airy/src/main/resources/input", "D:/web_dev/javaProject/airy/src/main/resources/output", "D:/web_dev/javaProject/airy/src/main/resources/config.yaml"); 
        gen.generateFiles();
        
    }

    
}
