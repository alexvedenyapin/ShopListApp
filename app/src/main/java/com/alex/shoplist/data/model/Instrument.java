package com.alex.shoplist.data.model;

import com.alex.shoplist.data.gson.results.GsonInstrument;

public class Instrument {

    private int id;
    private String brand;
    private String model;
    private String type;
    private int price;
    private int quantity;

    public Instrument (GsonInstrument instrument) {
        this.id = instrument.getInstrument().getId();
        this.brand = instrument.getInstrument().getBrand();
        this.model = instrument.getInstrument().getModel();
        this.type = instrument.getInstrument().getType();
        this.price = instrument.getInstrument().getPrice();
        this.quantity = instrument.getQuantity();
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
