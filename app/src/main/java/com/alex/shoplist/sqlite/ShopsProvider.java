package com.alex.shoplist.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;


public class ShopsProvider extends SQLiteTableProvider {

    public static final String TABLE_NAME = "shops";

    public static final Uri URI = Uri.parse("content://" + SQLiteContentProvider.CONTENT_AUTHORITY + "/" + TABLE_NAME);

    public ShopsProvider() {
        super(TABLE_NAME);
    }

    @Override
    public Uri getBaseUri() {
        return URI;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(" + Columns._ID + " integer primary key on conflict replace, "
                + Columns.SHOP_ID + " integer, "
                + Columns.NAME + " text, "
                + Columns.ADDRESS + " text, "
                + Columns.PHONE + " text, "
                + Columns.WEBSITE + " text, "
                + Columns.LATITUDE + " text, "
                + Columns.LONGITUDE + " text);");
    }

    public interface Columns extends BaseColumns {
        String SHOP_ID = "shop_id";
        String NAME = "name";
        String ADDRESS = "address";
        String PHONE = "phone";
        String WEBSITE = "website";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
    }
}
