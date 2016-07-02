package com.example.lenovo.notebook;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Article {
    private String title;
    private String content;

    public Article(String title, String content){
        this.content = content;
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }
}
