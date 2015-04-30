package com.alex.shoplist.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import com.alex.shoplist.data.gson.results.GsonInstrument;
import com.alex.shoplist.data.gson.results.GsonShop;
import com.alex.shoplist.data.model.Instrument;
import com.alex.shoplist.data.model.Shop;
import com.alex.shoplist.net.requests.BaseRequest;
import com.alex.shoplist.net.requests.GetInstrumentRequest;
import com.alex.shoplist.net.requests.GetShopsRequest;
import com.alex.shoplist.util.SyncUtils;
import com.alex.shoplist.util.Utils;

public class DownloadService extends IntentService {

    private ContentResolver mResolver;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mResolver = getContentResolver();

        Shop[] shops = downloadShops();
        SyncUtils.syncShopsWithDB(shops, mResolver);
        downloadAndSyncInstruments(shops);
    }

    private Shop[] downloadShops() {
        Shop[] shops = new Shop[0];
        GetShopsRequest shopsRequest = new GetShopsRequest(this);

        try {
            Bundle resultData = shopsRequest.execute();
            GsonShop[] gsonShops = (GsonShop[]) resultData.getSerializable(BaseRequest.INTENT_RESULT);
            shops = Utils.convertGsonShopsToShops(gsonShops);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shops;
    }

    private void downloadAndSyncInstruments(Shop[] shops) {
        for (Shop shop : shops) {
            Instrument[] instruments = downloadInstruments(shop.getId());
            SyncUtils.syncInstrumentsWithDB(instruments, mResolver, shop.getId());
        }
    }

    private Instrument[] downloadInstruments(int shopId) {
        Instrument[] instruments = new Instrument[0];

        GetInstrumentRequest instrumentRequest = new GetInstrumentRequest(this, shopId);

        try {
            Bundle resultData = instrumentRequest.execute();
            GsonInstrument[] gsonInstruments = (GsonInstrument[]) resultData.getSerializable(BaseRequest.INTENT_RESULT);
            instruments = Utils.convertGsonInstrumentsToInstruments(gsonInstruments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instruments;
    }
}
