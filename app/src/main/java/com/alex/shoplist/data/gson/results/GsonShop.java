package com.alex.shoplist.data.gson.results;

public class GsonShop extends GsonResult<GsonShop[]> {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private Location location;

    public class Location {
        private String latitude;
        private String longitude;

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }
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

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "GsonShop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", location=" + location +
                '}';
    }
}