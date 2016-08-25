package com.flycode.paradoxidealmaster.model;

import com.google.gson.annotations.SerializedName;

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
    private Date dateOfBirth;
    private IdealService[] services;
    private int balance;
    private boolean sex;

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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public IdealService[] getServices() {
        return services;
    }

    public void setServices(IdealService[] services) {
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
