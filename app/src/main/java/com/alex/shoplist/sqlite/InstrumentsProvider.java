package com.alex.shoplist.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class InstrumentsProvider extends SQLiteTableProvider {

    public static final String TABLE_NAME = "instruments";

    public static final Uri URI = Uri.parse("content://" + SQLiteContentProvider.CONTENT_AUTHORITY + "/" + TABLE_NAME);

    public InstrumentsProvider() {
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
                + Columns.INSTRUMENT_ID + " integer, "
                + Columns.SHOP_ID + " integer, "
                + Columns.BRAND + " text, "
                + Columns.MODEL + " text, "
                + Columns.TYPE + " text, "
                + Columns.PRICE + " integer, "
                + Columns.QUANTITY + " integer);");
    }

    public interface Columns extends BaseColumns {
        String INSTRUMENT_ID = "instrument_id";
        String SHOP_ID = "shop_id";
        String BRAND = "brand";
        String MODEL = "model";
        String TYPE = "type";
        String PRICE = "price";
        String QUANTITY = "quantity";
    }
}