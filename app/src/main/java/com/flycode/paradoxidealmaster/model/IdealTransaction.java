package com.flycode.paradoxidealmaster.model;

import com.flycode.paradoxidealmaster.api.response.TransactionResponse;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created - Schumakher on 25-Aug-16.
 */
public class IdealTransaction extends RealmObject {
    @PrimaryKey
    private String id;
    private int moneyAmount;
    private String toId;
    private String toFullName;
    private String fromId;
    private String fromFullName;
    private String paymentType;
    private String description;
    private Date date;

    public static IdealTransaction transactionFromResponse(TransactionResponse transactionResponse) {
        IdealTransaction idealTransaction = new IdealTransaction();

        idealTransaction.setDate(transactionResponse.getDate());
        idealTransaction.setId(transactionResponse.getId());
        idealTransaction.setPaymentType(transactionResponse.getPaymentType());
        idealTransaction.setDescription(transactionResponse.getDescription());

        if (transactionResponse.getFrom() == null) {
            idealTransaction.setFromId(null);
            idealTransaction.setFromFullName("Ideal Systems");
        } else {
            idealTransaction.setFromId(transactionResponse.getFrom().getId());
            idealTransaction.setFromFullName(transactionResponse.getFrom().getName()
                                                + " " + transactionResponse.getFrom().getSurname());
        }

        if (transactionResponse.getTo() == null) {
            idealTransaction.setToId(null);
            idealTransaction.setToFullName("Ideal Systems");
        } else {
            idealTransaction.setToId(transactionResponse.getTo().getId());
            idealTransaction.setToFullName(transactionResponse.getTo().getName()
                                                + " " + transactionResponse.getTo().getSurname());
        }

        return idealTransaction;
    }

    public String getId() {
        return id;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToFullName() {
        return toFullName;
    }

    public void setToFullName(String toFullName) {
        this.toFullName = toFullName;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromFullName() {
        return fromFullName;
    }

    public void setFromFullName(String fromFullName) {
        this.fromFullName = fromFullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
