package com.alex.shoplist.net.requests;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.alex.shoplist.data.gson.results.GsonShop;
import com.google.gson.Gson;

import java.io.Reader;


public class GetShopsRequest extends BaseGetRequest {

    public GetShopsRequest(Context context) {
        super("stores", context);
    }

    public GetShopsRequest(Parcel parcel) {
        super(parcel);
    }

    @Override
    protected GsonShop[] parseResponse(Reader response) {
        Gson gson = new Gson();
        GsonShop[] gsonShops = gson.fromJson(response, GsonShop[].class);
        return gsonShops;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
    }

    public static final Parcelable.Creator<GetShopsRequest> CREATOR
            = new Parcelable.Creator<GetShopsRequest>() {

        public GetShopsRequest createFromParcel(Parcel in) {
            return new GetShopsRequest(in);
        }

        public GetShopsRequest[] newArray(int size) {
            return new GetShopsRequest[size];

        }
    };
}