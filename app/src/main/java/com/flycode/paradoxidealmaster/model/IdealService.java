package com.flycode.paradoxidealmaster.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by acerkinght on 8/8/16.
 */
public class IdealService extends RealmObject {
    @SerializedName("_id")
    private String id;
    private String name;
    @SerializedName("super")
    private String superService;
    private int cost;
    private boolean countable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuperService() {
        return superService;
    }

    public void setSuperService(String superService) {
        this.superService = superService;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isCountable() {
        return countable;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }
}
