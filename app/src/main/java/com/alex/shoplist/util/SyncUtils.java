package com.alex.shoplist.util;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.alex.shoplist.data.model.Instrument;
import com.alex.shoplist.data.model.Shop;
import com.alex.shoplist.sqlite.InstrumentsProvider;
import com.alex.shoplist.sqlite.SQLiteContentProvider;
import com.alex.shoplist.sqlite.ShopsProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncUtils {

    public static void syncShopsWithDB(Shop[] shops, ContentResolver mResolver) {
        try {
            ArrayList<ContentProviderOperation> batch = new ArrayList<>();

            // Build hash table of incoming entries
            HashMap<Integer, Shop> entryMap = new HashMap<>();
            for (Shop shop : shops) {
                entryMap.put(shop.getId(), shop);
            }

            String[] PROJECTION = new String[]{ShopsProvider.Columns._ID,
                    ShopsProvider.Columns.SHOP_ID,
                    ShopsProvider.Columns.NAME,
                    ShopsProvider.Columns.ADDRESS,
                    ShopsProvider.Columns.PHONE,
                    ShopsProvider.Columns.WEBSITE,
                    ShopsProvider.Columns.LATITUDE,
                    ShopsProvider.Columns.LONGITUDE};

            Cursor c = mResolver.query(ShopsProvider.URI, PROJECTION, null, null, null);
            assert c != null;

            // Find stale data
            int id, shop_id;
            String name, addr, phone, website, lat, lng;

            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(ShopsProvider.Columns._ID));
                shop_id = c.getInt(c.getColumnIndex(ShopsProvider.Columns.SHOP_ID));
                name = c.getString(c.getColumnIndex(ShopsProvider.Columns.NAME));
                addr = c.getString(c.getColumnIndex(ShopsProvider.Columns.ADDRESS));
                phone = c.getString(c.getColumnIndex(ShopsProvider.Columns.PHONE));
                website = c.getString(c.getColumnIndex(ShopsProvider.Columns.WEBSITE));
                lat = c.getString(c.getColumnIndex(ShopsProvider.Columns.LATITUDE));
                lng = c.getString(c.getColumnIndex(ShopsProvider.Columns.LONGITUDE));

                Shop match = entryMap.get(shop_id);

                if (match != null) {
                    // Entry exists. Remove from entry map to prevent insert later.
                    entryMap.remove(shop_id);
                    // Check to see if the entry needs to be updated
                    Uri existingUri = ShopsProvider.URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    if ((match.getName() != null && !match.getName().equals(name)) ||
                            (match.getAddress() != null && !match.getAddress().equals(addr)) ||
                            (match.getPhone() != null && !match.getPhone().equals(phone)) ||
                            (match.getWebsite() != null && !match.getWebsite().equals(website)) ||
                            (match.getLatitude() != null && !match.getLatitude().equals(lat)) ||
                            (match.getLongitude() != null && !match.getLongitude().equals(lng))) {
                        // Update existing record
                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(existingUri);
                        builder.withValue(ShopsProvider.Columns.NAME, match.getName())
                                .withValue(ShopsProvider.Columns.ADDRESS, match.getAddress())
                                .withValue(ShopsProvider.Columns.PHONE, match.getPhone())
                                .withValue(ShopsProvider.Columns.WEBSITE, match.getWebsite())
                                .withValue(ShopsProvider.Columns.LATITUDE, match.getLatitude())
                                .withValue(ShopsProvider.Columns.LONGITUDE, match.getLongitude());

                        batch.add(builder.build());
                    }
                } else {
                    // Entry doesn't exist. Remove it from the database.
                    Uri deleteUri = ShopsProvider.URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();

            // Add new items
            for (Shop shop : entryMap.values()) {
                batch.add(ContentProviderOperation.newInsert(ShopsProvider.URI)
                        .withValue(ShopsProvider.Columns.SHOP_ID, shop.getId())
                        .withValue(ShopsProvider.Columns.NAME, shop.getName())
                        .withValue(ShopsProvider.Columns.ADDRESS, shop.getAddress())
                        .withValue(ShopsProvider.Columns.PHONE, shop.getPhone())
                        .withValue(ShopsProvider.Columns.WEBSITE, shop.getWebsite())
                        .withValue(ShopsProvider.Columns.LATITUDE, shop.getLatitude())
                        .withValue(ShopsProvider.Columns.LONGITUDE, shop.getLongitude())
                        .build());
            }

            if (batch.size() != 0) {
                mResolver.applyBatch(SQLiteContentProvider.CONTENT_AUTHORITY, batch);
                mResolver.notifyChange(
                        ShopsProvider.URI, null, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncInstrumentsWithDB(Instrument[] instruments, ContentResolver mResolver, int shopId) {
        try {
            ArrayList<ContentProviderOperation> batch = new ArrayList<>();

            // Build hash table of incoming entries
            HashMap<Integer, Instrument> entryMap = new HashMap<>();
            for (Instrument instrument : instruments) {
                entryMap.put(instrument.getId(), instrument);
            }

            String[] PROJECTION = new String[]{InstrumentsProvider.Columns._ID,
                    InstrumentsProvider.Columns.INSTRUMENT_ID,
                    InstrumentsProvider.Columns.SHOP_ID,
                    InstrumentsProvider.Columns.BRAND,
                    InstrumentsProvider.Columns.MODEL,
                    InstrumentsProvider.Columns.TYPE,
                    InstrumentsProvider.Columns.PRICE,
                    InstrumentsProvider.Columns.QUANTITY};

            String instrumentSelection = InstrumentsProvider.Columns.SHOP_ID + "=?";
            String[] instrumentSelectionArgs = new String[]{Integer.toString(shopId)};

            Cursor c = mResolver.query(InstrumentsProvider.URI, PROJECTION, instrumentSelection, instrumentSelectionArgs, null);
            assert c != null;

            // Find stale data
            int id, instrument_id, price, quantity;
            String brand, model, type;

            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(InstrumentsProvider.Columns._ID));
                instrument_id = c.getInt(c.getColumnIndex(InstrumentsProvider.Columns.INSTRUMENT_ID));
                brand = c.getString(c.getColumnIndex(InstrumentsProvider.Columns.BRAND));
                model = c.getString(c.getColumnIndex(InstrumentsProvider.Columns.MODEL));
                type = c.getString(c.getColumnIndex(InstrumentsProvider.Columns.TYPE));
                price = c.getInt(c.getColumnIndex(InstrumentsProvider.Columns.PRICE));
                quantity = c.getInt(c.getColumnIndex(InstrumentsProvider.Columns.QUANTITY));

                Instrument match = entryMap.get(instrument_id);

                if (match != null) {
                    // Entry exists. Remove from entry map to prevent insert later.
                    entryMap.remove(instrument_id);
                    // Check to see if the entry needs to be updated
                    Uri existingUri = InstrumentsProvider.URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    if ((match.getBrand() != null && !match.getBrand().equals(brand)) ||
                            (match.getModel() != null && !match.getModel().equals(model)) ||
                            (match.getType() != null && !match.getType().equals(type)) ||
                            (match.getPrice() != price) ||
                            (match.getQuantity() != quantity)) {
                        // Update existing record
                        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(existingUri);
                        builder.withValue(InstrumentsProvider.Columns.BRAND, match.getBrand())
                                .withValue(InstrumentsProvider.Columns.MODEL, match.getModel())
                                .withValue(InstrumentsProvider.Columns.TYPE, match.getType())
                                .withValue(InstrumentsProvider.Columns.PRICE, match.getPrice())
                                .withValue(InstrumentsProvider.Columns.QUANTITY, match.getQuantity());

                        batch.add(builder.build());
                    }
                } else {
                    // Entry doesn't exist. Remove it from the database.
                    Uri deleteUri = InstrumentsProvider.URI.buildUpon()
                            .appendPath(Integer.toString(id)).build();
                    batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                }
            }
            c.close();

            // Add new items
            for (Instrument instrument : entryMap.values()) {
                batch.add(ContentProviderOperation.newInsert(InstrumentsProvider.URI)
                        .withValue(InstrumentsProvider.Columns.INSTRUMENT_ID, instrument.getId())
                        .withValue(InstrumentsProvider.Columns.SHOP_ID, shopId)
                        .withValue(InstrumentsProvider.Columns.BRAND, instrument.getBrand())
                        .withValue(InstrumentsProvider.Columns.MODEL, instrument.getModel())
                        .withValue(InstrumentsProvider.Columns.TYPE, instrument.getType())
                        .withValue(InstrumentsProvider.Columns.PRICE, instrument.getPrice())
                        .withValue(InstrumentsProvider.Columns.QUANTITY, instrument.getQuantity())
                        .build());
            }

            if (batch.size() != 0) {
                mResolver.applyBatch(SQLiteContentProvider.CONTENT_AUTHORITY, batch);
                mResolver.notifyChange(
                        InstrumentsProvider.URI, null, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}