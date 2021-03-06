package com.idealsystems.idealmaster.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.idealsystems.idealmaster.api.response.OrderResponse;
import com.idealsystems.idealmaster.api.response.SimpleOrderResponse;

import java.util.Date;

import io.realm.Realm;
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
    private String masterId;
    private boolean serviceIsCountable;
    private IdealTranslation serviceTranslation;
    private IdealTranslation serviceUnitTranslation;
    private int serviceCost;
    private String chosenFavorite;
    private String locationName;
    private Double locationLatitude;
    private Double locationLongitude;
    private int quantity;
    private Date orderTime;
    private Date updated;
    private String userPhone;

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
        userPhone = in.readString();
        masterId = in.readString();
        serviceTranslation = in.readParcelable(IdealTranslation.class.getClassLoader());
        serviceUnitTranslation = in.readParcelable(IdealTranslation.class.getClassLoader());
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
        dest.writeString(userPhone);
        dest.writeString(masterId);
        dest.writeParcelable(getServiceTranslation(), 0);
        dest.writeParcelable(getServiceUnitTranslation(), 0);
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

    public String getTranslatedServiceName(Context context) {
        String name = null;

        if (getServiceTranslation() != null) {
            name = getServiceTranslation().getTranslationForLocale(context);
        }

        return name != null ? name : getServiceName();
    }

    public String getTranslatedServiceUnit(Context context) {
        String unit = null;

        if (getServiceUnitTranslation() != null) {
            unit = getServiceUnitTranslation().getTranslationForLocale(context);
        }

        return unit != null ? unit : getServiceUnit();
    }

    public static Order fromResponse(OrderResponse orderResponse) {
        if (orderResponse.getService() == null
                || orderResponse.getUser() == null) {
            return null;
        }

        Log.d("ORDER_ID", orderResponse.getId());

        Order order = new Order();
        order.id = orderResponse.getId();
        order.status = orderResponse.getStatus();
        order.description = orderResponse.getDescription();
        order.userId = orderResponse.getUser().getId();
        order.userName = orderResponse.getUser().getName();
        order.userSurname = orderResponse.getUser().getSurname();
        order.userPhone = orderResponse.getUser().getPhone();
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

        order.serviceTranslation = orderResponse.getService().getTranslation();
        order.serviceUnitTranslation = orderResponse.getService().getUnitTranslation();

        if (orderResponse.getService().getTranslation() == null) {
            order.serviceTranslation = new IdealTranslation();
            order.serviceTranslation.setBase(order.getServiceName());
        }

        if (orderResponse.getService().getUnitTranslation() == null) {
            order.serviceUnitTranslation = new IdealTranslation();
            order.serviceUnitTranslation.setBase(order.getServiceUnit());
        }

        IdealService idealService = Realm
                .getDefaultInstance()
                .where(IdealService.class)
                .equalTo("id", order.serviceId)
                .findFirst();

        if (idealService != null) {
            order.serviceColor = idealService.getColor();
        }

        if (orderResponse.getMaster() != null) {
            order.masterId = orderResponse.getMaster().getId();
        }

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

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public IdealTranslation getServiceTranslation() {
        return serviceTranslation;
    }

    public void setServiceTranslation(IdealTranslation serviceTranslation) {
        this.serviceTranslation = serviceTranslation;
    }

    public IdealTranslation getServiceUnitTranslation() {
        return serviceUnitTranslation;
    }

    public void setServiceUnitTranslation(IdealTranslation serviceUnitTranslation) {
        this.serviceUnitTranslation = serviceUnitTranslation;
    }
}
