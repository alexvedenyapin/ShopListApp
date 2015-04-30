package com.alex.shoplist.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.alex.shoplist.R;
import com.alex.shoplist.services.DownloadService;
import com.alex.shoplist.ui.fragments.DetailsFragment;
import com.alex.shoplist.ui.fragments.TitlesFragment;


public class MainActivity extends ActionBarActivity implements TitlesFragment.OnTitleSelectedListener{

    public static final String POSITION = "position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sync starts on each launch of the app in case there are changes on the Server
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
    }

    @Override
    public void onTitleSelected(int position) {
        DetailsFragment detailsFrag = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details_frag);
        if (detailsFrag == null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(POSITION, position);
            startActivity(intent);
        } else {
            detailsFrag.updateContent(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
