package com.alex.shoplist.sqlite;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteContentProvider extends ContentProvider {

    private static final String DATABASE_NAME = "shoplist.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MIME_DIR = "vnd.android.cursor.dir/";

    private static final String MIME_ITEM = "vnd.android.cursor.item/";

    public static final String CONTENT_AUTHORITY = "com.alex.shoplist";

    private static final Map<String, SQLiteTableProvider> SCHEMA = new ConcurrentHashMap<>();

    static {
        SCHEMA.put(ShopsProvider.TABLE_NAME, new ShopsProvider());
        SCHEMA.put(InstrumentsProvider.TABLE_NAME, new InstrumentsProvider());
    }

    private final SQLiteUriMatcher mUriMatcher = new SQLiteUriMatcher();
    private SQLiteOpenHelper mHelper;

    private static ProviderInfo getProviderInfo(Context context, Class<? extends ContentProvider> provider, int flags)
            throws PackageManager.NameNotFoundException {
        return context.getPackageManager().
                getProviderInfo(new ComponentName(context.getPackageName(), provider.getName()), flags);
    }

    private static String getTableName(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    @Override
    public boolean onCreate() {
        try {
            final ProviderInfo pi = getProviderInfo(getContext(), getClass(), 0);
            final String[] authorities = TextUtils.split(pi.authority, ";");
            for (final String authority : authorities) {
                mUriMatcher.addAuthority(authority);
            }
            mUriMatcher.addAuthority(CONTENT_AUTHORITY);
            mHelper = new SQLiteOpenHelperImpl(getContext());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            throw new SQLiteException(e.getMessage());
        }
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final int matchResult = mUriMatcher.match(uri);
        checkMatchResult(uri, matchResult);

        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        checkTableName(tableProvider, tableName);

        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        final Cursor cursor = tableProvider.query(mHelper.getReadableDatabase(), columns, where, whereArgs, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int matchResult = mUriMatcher.match(uri);
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        } else if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            return MIME_ITEM + getTableName(uri);
        }
        return MIME_DIR + getTableName(uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int matchResult = mUriMatcher.match(uri);
        checkMatchResult(uri, matchResult);

        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        checkTableName(tableProvider, tableName);

        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            final int affectedRows = tableProvider.
                    update(mHelper.getWritableDatabase(), values, BaseColumns._ID + "=?", new String[]{uri.getLastPathSegment()});
            if (affectedRows > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
                return uri;
            }
        }
        final long lastId = tableProvider.insert(mHelper.getWritableDatabase(), values);
        getContext().getContentResolver().notifyChange(tableProvider.getBaseUri(), null);
        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final int matchResult = mUriMatcher.match(uri);
        checkMatchResult(uri, matchResult);

        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        checkTableName(tableProvider, tableName);

        if (matchResult == SQLiteUriMatcher.MATCH_ID) {
            where = BaseColumns._ID + "=?";
            whereArgs = new String[]{uri.getLastPathSegment()};
        }
        final int affectedRows = tableProvider.update(mHelper.getWritableDatabase(),values, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final int matchResult = mUriMatcher.match(uri);
        checkMatchResult(uri, matchResult);

        final String tableName = getTableName(uri);
        final SQLiteTableProvider tableProvider = SCHEMA.get(tableName);
        checkTableName(tableProvider, tableName);

        final int affectedRows = tableProvider.delete(mHelper.getWritableDatabase(), where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    private void checkMatchResult(Uri uri, int matchResult) {
        if (matchResult == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Unknown uri " + uri);
        }
    }

    private void checkTableName(SQLiteTableProvider tableProvider, String tableName) {
        if (tableProvider == null) {
            throw new SQLiteException("No such table " + tableName);
        }
    }

    private static final class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

        public SQLiteOpenHelperImpl(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("Alex", "SQLiteContentProvider onCreate()");
            db.beginTransactionNonExclusive();
            try {
                for (final SQLiteTableProvider tableProvider : SCHEMA.values()) {
                    tableProvider.onCreate(db);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e("Alex", "SQLiteContentProvider onUpgrade()");
            db.beginTransactionNonExclusive();
            try {
                for (SQLiteTableProvider tableProvider : SCHEMA.values()) {
                    tableProvider.onUpgrade(db, oldVersion, newVersion);
                    db.setTransactionSuccessful();
                }
            } finally {
                db.endTransaction();
            }
        }
    }
}
