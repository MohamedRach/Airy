# Airy

Airy is a lightweight static site generator built with Java.

## Introduction

Airy is designed to simplify the process of generating static websites. It allows you to manage content, and customize configurations to generate static HTML files for your website.

## Features

- Easy-to-use command-line interface (CLI)
- Template-based content generation
- Support for Markdown and HTML content
- Customizable configuration options
- Lightweight and efficient

## Requirements

To use Airy, you need the following:

- Java Development Kit (JDK) version 8 or higher

## Installation

You can install Airy by downloading the JAR file from the [Releases](https://github.com/yourusername/airy/releases) page.

## Usage

1. After downloading the JAR file, locate it.

2. To initialize your project, run the following command to create the necessary folders and configuration file:

```bash
java -jar path/to/your/jar/airy-1.0-SNAPSHOT.jar start
```
This command will create the input, output, and templates folders, along with a config.yaml file in the current directory.

3. Next, Add your information in config.yaml file and create your Markdown files (.md) in the input directory. These files will serve as the content for your static website.

4. Once you've created your Markdown files, run the following command to generate HTML files from your content:
```bash
java -jar path/to/your/jar/airy-1.0-SNAPSHOT.jar build
```
