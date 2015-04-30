package com.alex.shoplist.net.requests;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

public abstract class BaseGetRequest<Result extends Serializable> extends BaseRequest implements Parcelable {

    public BaseGetRequest(String routine, Context context) {
        super(routine, context);
    }

    public BaseGetRequest(Parcel parcel) {
        super(parcel);
    }

    public Bundle execute() throws Exception {
        Reader r = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(buildApiUri());
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded");

        HttpResponse response = httpClient.execute(httpGet);
        InputStream is = response.getEntity().getContent();
        r = new InputStreamReader(is);

        Result result = (Result)parseResponse(r);
        Bundle b = new Bundle();
        b.putSerializable(INTENT_RESULT, result);
        return b;
    }
}
