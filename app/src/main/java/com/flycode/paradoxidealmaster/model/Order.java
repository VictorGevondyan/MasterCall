package com.flycode.paradoxidealmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.flycode.paradoxidealmaster.api.response.OrderResponse;
import com.flycode.paradoxidealmaster.api.response.SimpleOrderResponse;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by acerkinght on 8/22/16.
 */
public class Order extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String status;
    private String description;
    private String userId;
    private String userName;
    private String userSurname;
    private String serviceId;
    private String serviceName;
    private String serviceUnit;
    private String serviceColor;
    private boolean serviceIsCountable;
    private int serviceCost;
    private String chosenFavorite;
    private String locationName;
    private Double locationLatitude;
    private Double locationLongitude;
    private int quantity;
    private Date orderTime;
    private Date updated;

    public Order() {
        super();
    }

    protected Order(Parcel in) {
        id = in.readString();
        status = in.readString();
        description = in.readString();
        userId = in.readString();
        userName = in.readString();
        userSurname = in.readString();
        chosenFavorite = in.readString();
        serviceId = in.readString();
        serviceName = in.readString();
        serviceColor = in.readString();
        serviceUnit = in.readString();
        serviceCost = in.readInt();
        serviceIsCountable = in.readInt() == 1;
        locationName = in.readString();
        locationLatitude = in.readDouble();
        locationLongitude = in.readDouble();
        quantity = in.readInt();
        updated = new Date(in.readLong());
        orderTime = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(description);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userSurname);
        dest.writeString(chosenFavorite);
        dest.writeString(serviceId);
        dest.writeString(serviceName);
        dest.writeString(serviceColor);
        dest.writeString(serviceUnit);
        dest.writeInt(serviceCost);
        dest.writeInt(serviceIsCountable ? 1 : 0);
        dest.writeString(locationName);
        dest.writeDouble(locationLatitude);
        dest.writeDouble(locationLongitude);
        dest.writeInt(quantity);
        dest.writeLong(updated.getTime());
        dest.writeLong(orderTime.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public static Order fromResponse(OrderResponse orderResponse) {
        if (orderResponse.getService() == null) {
            return null;
        }

        Order order = new Order();
        order.id = orderResponse.getId();
        order.status = orderResponse.getStatus();
        order.description = orderResponse.getDescription();
        order.userId = orderResponse.getUser().getId();
        order.userName = orderResponse.getUser().getName();
        order.userSurname = orderResponse.getUser().getSurname();
        order.chosenFavorite = orderResponse.getChosenFavorite();
        order.serviceId = orderResponse.getService().getId();
        order.serviceName = orderResponse.getService().getName();
        order.serviceColor = orderResponse.getService().getColor();
        order.serviceUnit = orderResponse.getService().getUnit();
        order.serviceCost = orderResponse.getService().getCost();
        order.serviceIsCountable = orderResponse.getService().isCountable();
        order.locationName = orderResponse.getEndPoint().getName();
        order.locationLatitude = orderResponse.getEndPoint().getGeo().get(0);
        order.locationLongitude= orderResponse.getEndPoint().getGeo().get(1);
        order.quantity = orderResponse.getQuantity();
        order.orderTime = orderResponse.getOrderTime();
        order.updated = orderResponse.getUpdated();

        return order;
    }

    public void mergeSimpleResponse(SimpleOrderResponse orderResponse) {
        if (!this.id.equals(orderResponse.getId())) {
            return;
        }

        this.status = orderResponse.getStatus();
        this.updated = orderResponse.getUpdated();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getChosenFavorite() {
        return chosenFavorite;
    }

    public void setChosenFavorite(String chosenFavorite) {
        this.chosenFavorite = chosenFavorite;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceColor() {
        return serviceColor;
    }

    public void setServiceColor(String serviceColor) {
        this.serviceColor = serviceColor;
    }

    public int getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(int serviceCost) {
        this.serviceCost = serviceCost;
    }

    public boolean isServiceIsCountable() {
        return serviceIsCountable;
    }

    public void setServiceIsCountable(boolean serviceIsCountable) {
        this.serviceIsCountable = serviceIsCountable;
    }

    public String getServiceUnit() {
        return serviceUnit;
    }

    public void setServiceUnit(String serviceUnit) {
        this.serviceUnit = serviceUnit;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
}
