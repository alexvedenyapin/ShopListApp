package com.alex.shoplist.data.model;

import com.alex.shoplist.data.gson.results.GsonShop;

public class Shop {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private String latitude;
    private String longitude;

    public Shop(GsonShop gsonShop) {
        this.id = gsonShop.getId();
        this.name = gsonShop.getName();
        this.address = gsonShop.getAddress();
        this.phone = gsonShop.getPhone();
        this.website = gsonShop.getWebsite();
        this.latitude = gsonShop.getLocation().getLatitude();
        this.longitude = gsonShop.getLocation().getLongitude();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", website='" + latitude + '\'' +
                ", location=" + longitude +
                '}';
    }
}
