package com.alex.shoplist.net.requests;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

abstract public class BaseRequest <Result extends Serializable> implements Parcelable {

    protected String fullPath;
    public static final String INTENT_RESULT = "request_result";
    protected String baseUrl = "http://aschoolapi.appspot.com/";
    protected Context context;

    public BaseRequest(String routine, Context context) {
        fullPath = baseUrl + routine;
        this.context = context;
    }

    public BaseRequest(Parcel parcel) {
        readFromParcel(parcel);
    }

    public abstract Bundle execute() throws Exception;

    protected URI buildApiUri() throws Exception{
        try {
            return new URI(fullPath);
        } catch (URISyntaxException e) {
            throw new Exception("Failed to generate api path: " + fullPath, e);
        }
    }

    protected abstract Result parseResponse(Reader response);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullPath);
    }

    protected void readFromParcel(Parcel parcel) {
        fullPath = parcel.readString();
    }
}
