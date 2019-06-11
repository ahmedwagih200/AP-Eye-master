package com.example.apeye.model;

public class Comment {
    private String date;
    private String user_id;
    private String comment;

    public Comment(String date, String user_id, String comment) {
        this.date = date;
        this.user_id = user_id;
        this.comment = comment;
    }

    public Comment() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
