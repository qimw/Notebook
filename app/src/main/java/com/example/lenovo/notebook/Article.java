package com.example.lenovo.notebook;

/**
 * Created by lenovo on 2016/5/7.
 */
public class Article {
    private String title;
    private String content;
    private int status;
    private long id;

    public Article(String title, String content,int status,long id){
        this.content = content;
        this.title = title;
        this.status = status;
        this.id = id;
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
    public long getId(){
        return id;
    }
}
