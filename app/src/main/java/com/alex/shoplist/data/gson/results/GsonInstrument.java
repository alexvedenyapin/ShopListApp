package com.alex.shoplist.data.gson.results;

public class GsonInstrument extends GsonResult<GsonInstrument[]> {

    private Instrument instrument;
    private int quantity;

    public class Instrument {
        private int id;
        private String brand;
        private String model;
        private String type;
        private int price;

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

        @Override
        public String toString() {
            return "Instrument{" +
                    "id=" + id +
                    ", brand='" + brand + '\'' +
                    ", model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", price='" + price + '\'' +
                    '}';
        }
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "GsonInstrument{" +
                "instrument=" + instrument +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}