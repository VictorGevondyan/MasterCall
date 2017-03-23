package com.idealsystems.idealmaster.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created - Schumakher on 26-Aug-16.
 */
public class TransactionResponse {
    @SerializedName("_id")
    private String id;
    private String paymentType;
    private String description;
    private int moneyAmount;
    private SimpleUserResponse to;
    private SimpleUserResponse from;
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public SimpleUserResponse getTo() {
        return to;
    }

    public void setTo(SimpleUserResponse to) {
        this.to = to;
    }

    public SimpleUserResponse getFrom() {
        return from;
    }

    public void setFrom(SimpleUserResponse from) {
        this.from = from;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
