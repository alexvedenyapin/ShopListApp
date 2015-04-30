package com.alex.shoplist.ui.fragments;


import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.shoplist.R;
import com.alex.shoplist.sqlite.ShopsProvider;

public class TitlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] SHOPS_PROJECTION = new String[]{
            ShopsProvider.Columns._ID,
            ShopsProvider.Columns.NAME
    };

    private static final int URL_LOADER = 0;
    private OnTitleSelectedListener mCallback;
    private ListView shopsList;
    private ShopAdapter adapter;
    private ProgressDialog progress;

    public TitlesFragment() {
    }

    public interface OnTitleSelectedListener {
        public void onTitleSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnTitleSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTitleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_titles, container, false);
        initUi(v);
        initListeners();
        initProgress();

        getLoaderManager().initLoader(URL_LOADER, null, this);
        return v;
    }

    private void initUi(View v) {
        shopsList = (ListView)v.findViewById(R.id.shopList);

        adapter = new ShopAdapter(getActivity(), null, 0);
        shopsList.setAdapter(adapter);
    }

    private void initListeners() {
        shopsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onTitleSelected(position);
            }
        });
    }

    private void initProgress() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.shops_loading));
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ShopsProvider.URI,
                SHOPS_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dismissProgressIfVisible();
        showProgressIfNeeded(data);
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void showProgressIfNeeded(Cursor cursor){
        if (cursor.getCount() == 0) {
                progress.show();
        }
    }

    private void dismissProgressIfVisible() {
        if (progress.isShowing())
            progress.dismiss();
    }

    private class ShopAdapter extends CursorAdapter {

        private LayoutInflater inflater;

        ShopAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(R.layout.shop_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView nameTv = (TextView) view.findViewById(R.id.shopNameTv);
            String name = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.NAME));
            nameTv.setText(name);
        }
    }
}
