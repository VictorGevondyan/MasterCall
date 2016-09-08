package com.flycode.paradoxidealmaster.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by acerkinght on 7/28/16.
 */
public class User {
    @SerializedName("_id")
    private String id;
    private String username;
    private String surname;
    private String name;
    private String role;
    private String image;
    private Date dateOfBirth;
    private ArrayList<IdealMasterService> services;
    private int balance;
    private boolean sex;
    private boolean sticker;

    public boolean isSticker() {
        return sticker;
    }

    public void setSticker(boolean sticker) {
        this.sticker = sticker;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ArrayList<IdealMasterService> getServices() {
        return services;
    }

    public void setServices(ArrayList<IdealMasterService> services) {
        this.services = services;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }
}
