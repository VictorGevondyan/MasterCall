package com.idealsystems.idealmaster.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by acerkinght on 8/27/16.
 */
public class SimpleOrderResponse {
    @SerializedName("_id")
    private String id;
    private Date updated;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
