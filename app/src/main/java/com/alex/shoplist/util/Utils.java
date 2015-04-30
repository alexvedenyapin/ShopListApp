package com.alex.shoplist.util;

import com.alex.shoplist.data.gson.results.GsonInstrument;
import com.alex.shoplist.data.gson.results.GsonShop;
import com.alex.shoplist.data.model.Instrument;
import com.alex.shoplist.data.model.Shop;

public class Utils {

    public static Shop[] convertGsonShopsToShops(GsonShop[] gsonShops) {
        Shop[] shops = new Shop[gsonShops.length];

        for (int i=0; i < gsonShops.length; i++) {
            shops[i] = new Shop(gsonShops[i]);
        }

        return shops;
    }

    public static Instrument[] convertGsonInstrumentsToInstruments(GsonInstrument[] gsonInstruments) {
        Instrument[] instruments = new Instrument[gsonInstruments.length];

        for (int i=0; i < gsonInstruments.length; i++) {
            instruments[i] = new Instrument(gsonInstruments[i]);
        }

        return instruments;
    }
}
