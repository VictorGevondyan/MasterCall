package com.flycode.paradoxidealmaster.api.response;

import com.flycode.paradoxidealmaster.model.IdealService;
import com.flycode.paradoxidealmaster.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by acerkinght on 8/22/16.
 */
public class OrderResponse {
    @SerializedName("_id")
    private String id;
    private String description;
    private User user;
    private IdealService service;
    private Date orderTime;
    private Date updated;
    private int quantity;
    private String status;
    private GeoResponse endPoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public IdealService getService() {
        return service;
    }

    public void setService(IdealService service) {
        this.service = service;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeoResponse getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(GeoResponse endPoint) {
        this.endPoint = endPoint;
    }
}
