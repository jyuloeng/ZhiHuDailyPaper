package com.odd.zhihudailypaper.Bean;

import java.io.Serializable;

public class NewsListBean implements Serializable {

    private String newId;
    private String title;
    private String imgId;
    private String hint;
    private String url;

    public NewsListBean(){}
    public NewsListBean(String newId, String title, String imgId,String hint,String url){
        this.newId = newId;
        this.title = title;
        this.imgId = imgId;
        this.hint = hint;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
