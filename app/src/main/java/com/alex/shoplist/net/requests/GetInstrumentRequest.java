package com.alex.shoplist.net.requests;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.alex.shoplist.data.gson.results.GsonInstrument;
import com.google.gson.Gson;

import java.io.Reader;


public class GetInstrumentRequest extends BaseGetRequest {

    public GetInstrumentRequest(Context context, int shopId) {
        super("stores/" + shopId + "/instruments", context);
    }

    public GetInstrumentRequest(Parcel parcel) {
        super(parcel);
    }

    @Override
    protected GsonInstrument[] parseResponse(Reader response) {
        Gson gson = new Gson();
        GsonInstrument[] instruments = gson.fromJson(response, GsonInstrument[].class);
        return instruments;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
    }

    public static final Parcelable.Creator<GetInstrumentRequest> CREATOR
            = new Parcelable.Creator<GetInstrumentRequest>() {

        public GetInstrumentRequest createFromParcel(Parcel in) {
            return new GetInstrumentRequest(in);
        }

        public GetInstrumentRequest[] newArray(int size) {
            return new GetInstrumentRequest[size];

        }
    };
}