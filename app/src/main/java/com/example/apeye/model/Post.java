package com.example.apeye.model;

public class Post extends PostID{

    private String image_url;
    private String user_id;
    private String desc;
    private String date;

    public Post(){

    }

    public Post(String user_id, String image_url, String desc, String date) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.date = date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

