package com.odd.zhihudailypaper.Bean;

public class CommentsListBean {
    private String author;
    private String content;
    private String avatarId;
    private String time;
    private String likes;
    private String apply_to;

    public CommentsListBean(){}
    public CommentsListBean(String author, String content, String avatarId, String time, String likes, String apply_to){
        this.author = author;
        this.content = content;
        this.avatarId = avatarId;
        this.time = time;
        this.likes = likes;
        this.apply_to = apply_to;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatar) {
        this.avatarId = avatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getApply_to() {
        return apply_to;
    }

    public void setApply_to(String apply_to) {
        this.apply_to = apply_to;
    }
}
