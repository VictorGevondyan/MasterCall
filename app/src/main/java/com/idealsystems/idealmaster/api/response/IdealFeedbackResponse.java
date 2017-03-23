package com.idealsystems.idealmaster.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by acerkinght on 9/7/16.
 */
public class IdealFeedbackResponse {
    @SerializedName("_id")
    private String id;
    private String feedback;
    private SimpleUserResponse user;
    private double stars;
    private Date updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public SimpleUserResponse getUser() {
        return user;
    }

    public void setUser(SimpleUserResponse user) {
        this.user = user;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
