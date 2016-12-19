package com.example.user.CheeseBurgerYummy.HAB;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by user on 12/13/16.
 */

public class HouseholdAccountBook implements Serializable {
    @SerializedName("pk")
    private int pk;

    @SerializedName("state")
    private String state;

    @SerializedName("price")
    private int price;

    @SerializedName("date")
    private String date;

    @SerializedName("category")
    private String category;

    @SerializedName("memo")
    private String memo;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
