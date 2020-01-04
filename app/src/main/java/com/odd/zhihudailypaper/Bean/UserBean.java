package com.odd.zhihudailypaper.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class UserBean implements Serializable {

    private int id;
    private String account;
    private String password;
    private String name;
    private String telephone;
    private String avatar;

    public static final String USER_DATA = "data";
    public static final String ID = "id";
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String TELEPHONE = "telephone";
    public static final String AVATAR = "avatar";

    public UserBean(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
