package com.example.airy.generator;
import java.util.Date;

public class BlogPost {
    private String title;
    private Date publishedDate;
    private int readingTime;
    private String description;
    private String content;
    private String link;
    private String img;

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublishedDate() {
        return this.publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getReadingTime() {
        return this.readingTime;
    }

    public void setReadingTime(int readingTime) {
        this.readingTime = readingTime;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }
    
}
