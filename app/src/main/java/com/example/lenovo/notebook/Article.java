package com.example.lenovo.notebook;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Article {
    private String title;
    private String content;
    private int status;

    public Article(String title, String content,int status){
        this.content = content;
        this.title = title;
        this.status = status;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }
    public int getStatus(){
        return status;
    }
}
