package com.alex.shoplist.ui.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.shoplist.R;
import com.alex.shoplist.sqlite.InstrumentsProvider;
import com.alex.shoplist.sqlite.ShopsProvider;
import com.alex.shoplist.ui.activity.MainActivity;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String[] SHOPS_PROJECTION = new String[]{
            ShopsProvider.Columns._ID,
            ShopsProvider.Columns.NAME,
            ShopsProvider.Columns.ADDRESS,
            ShopsProvider.Columns.PHONE,
            ShopsProvider.Columns.WEBSITE
    };

    private final String[] INSTRUMENTS_PROJECTION = new String[]{
            InstrumentsProvider.Columns._ID,
            InstrumentsProvider.Columns.SHOP_ID,
            InstrumentsProvider.Columns.BRAND,
            InstrumentsProvider.Columns.MODEL,
            InstrumentsProvider.Columns.TYPE,
            InstrumentsProvider.Columns.PRICE,
            InstrumentsProvider.Columns.QUANTITY
    };

    public static final String ARG_POSITION = "position";

    private DetailsAdapter adapter;
    private ProgressDialog progress;

    private TextView shopName;
    private TextView shopAddr;
    private TextView shopPhone;
    private TextView shopWeb;

    private static final int SHOPS_LOADER = 0;
    private static final int INSTRUMENTS_LOADER = 1;

    private int position;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        checkBundle();
        initUi(v);
        initProgress();

        getLoaderManager().initLoader(SHOPS_LOADER, null, this);
        getLoaderManager().initLoader(INSTRUMENTS_LOADER, null, this);

        return v;
    }

    private void checkBundle() {
        Bundle budle = getArguments();
        if (budle != null) {
            position = budle.getInt(MainActivity.POSITION);
        }
    }

    private void initUi(View v) {
        shopName = (TextView) v.findViewById(R.id.detailShopNameTv);
        shopAddr = (TextView) v.findViewById(R.id.detailShopAddrTv);
        shopPhone = (TextView) v.findViewById(R.id.detailShopNumberTv);
        shopWeb = (TextView) v.findViewById(R.id.detailShopWebTv);
        ListView instrumentList = (ListView) v.findViewById(R.id.instrumentList);

        adapter = new DetailsAdapter(getActivity(), null, 0);
        instrumentList.setAdapter(adapter);
    }

    private void initProgress() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.instruments_loading));
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void updateContent(int position) {
        this.position = position;
        getLoaderManager().restartLoader(SHOPS_LOADER, null, this);
        getLoaderManager().restartLoader(INSTRUMENTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String shopSelection = ShopsProvider.Columns.SHOP_ID + "=?";
        String[] shopSelectionArgs = new String[]{Integer.toString(position)};

        String instrumentSelection = InstrumentsProvider.Columns.SHOP_ID + "=?";
        String[] instrumentSelectionArgs = new String[]{Integer.toString(position)};

        switch (id) {
            case SHOPS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        ShopsProvider.URI,
                        SHOPS_PROJECTION,
                        shopSelection,
                        shopSelectionArgs,
                        null);
            case INSTRUMENTS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        InstrumentsProvider.URI,
                        INSTRUMENTS_PROJECTION,
                        instrumentSelection,
                        instrumentSelectionArgs,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SHOPS_LOADER:
                updateDetails(data);
                break;
            case INSTRUMENTS_LOADER:
                shouldShowProgress(data);
                adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void shouldShowProgress(Cursor cursor) {
        if (cursor.getCount() == 0)
            progress.show();
        else
            progress.dismiss();
    }

    private void updateDetails(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.NAME));
            String addr = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.ADDRESS));
            String phone = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.PHONE));
            String web = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.WEBSITE));

            shopName.setText(name);
            shopAddr.setText(addr);
            shopPhone.setText(phone);
            shopWeb.setText(web);
        }
    }

    private class DetailsAdapter extends CursorAdapter {

        private LayoutInflater inflater;

        DetailsAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.instrument_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView brandTv = (TextView) view.findViewById(R.id.instrumentBrandTv);
            TextView modelTv = (TextView) view.findViewById(R.id.instrumentModelTv);
            TextView typeTv = (TextView) view.findViewById(R.id.instrumentTypeTv);
            TextView priceTv = (TextView) view.findViewById(R.id.instrumentPriceTv);
            TextView quantityTv = (TextView) view.findViewById(R.id.instrumentQuantityTv);

            String brand = cursor.getString(cursor.getColumnIndex(InstrumentsProvider.Columns.BRAND));
            String model = cursor.getString(cursor.getColumnIndex(InstrumentsProvider.Columns.MODEL));
            String type = cursor.getString(cursor.getColumnIndex(InstrumentsProvider.Columns.TYPE));
            String price = cursor.getString(cursor.getColumnIndex(InstrumentsProvider.Columns.PRICE));
            String quantity = cursor.getString(cursor.getColumnIndex(InstrumentsProvider.Columns.QUANTITY));

            brandTv.setText(brand);
            modelTv.setText(model);
            typeTv.setText(type);
            priceTv.setText(price);
            quantityTv.setText(quantity);
        }
    }
}
