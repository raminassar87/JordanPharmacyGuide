package com.javawy.jordanpharmacyguide.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText pharmacyNameText = (EditText) findViewById(R.id.pharmacyNameText);
        EditText pharmacyLocationText = (EditText) findViewById(R.id.locationText);
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);

        //pharmacyNameText.setImeActionLabel("Search",EditorInfo.IME_ACTION_UNSPECIFIED);

        pharmacyNameText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        pharmacyNameText.setImeActionLabel("Next",EditorInfo.IME_ACTION_NEXT);

        pharmacyNameText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int actionId, KeyEvent event) {

                int keyCode = event.getKeyCode();
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    System.out.println("IME_ACTION_NEXT");
                }
//                if(actionId == EditorInfo.IME_ACTION_NEXT) {
//                    System.out.println("IME_ACTION_NEXT");
//                }
                return false;
            }
        });

        // Init City Spinner
        initCitySpinner();

        // Populate Drower Layout
        populateDrowerLayout();
    }

    /**
     * Populate Drower Layout
     */
    private void populateDrowerLayout() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Fetch Pharmacy
     */
    public void doSearch(View view) {

        Intent intent = new Intent(this, ResultActivity.class);

        EditText pharmacyNameText = (EditText) findViewById(R.id.pharmacyNameText);
        EditText pharmacyLocationText = (EditText) findViewById(R.id.locationText);
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        String pharmacyName = pharmacyNameText.getText().toString().trim();
        String pharmacyLocation = pharmacyLocationText.getText().toString().trim();
        String city = citiesSpinner.getSelectedItem().toString();

        if(!Utils.isBlankOrNull(pharmacyName)) {
            intent.putExtra("pharmacyName", pharmacyName);
        }
        if(!Utils.isBlankOrNull(pharmacyLocation)) {
            intent.putExtra("pharmacyLocation", pharmacyLocation);
        }
        if(!Utils.isBlankOrNull(city)) {
            intent.putExtra("city", city);
        }

        startActivity(intent);
    }

    /**
     * Init City Spinner
     */
    private void initCitySpinner() {
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this);
        citiesSpinner.setAdapter(customSpinnerAdapter);
    }


    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private List<String> asr;

        public CustomSpinnerAdapter(Context context) {
            String[] cities = getResources().getStringArray(R.array.cities_values);
            this.asr = Arrays.asList(cities);
            activity = context;
        }
        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }

        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(MainActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(MainActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_page) {
            return true;
        } else if (id == R.id.nav_rate) {
            String str ="https://play.google.com/store/apps/details?id=com.javawy.jordanpharmacyguide";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
            return true;
        } else if (id == R.id.nav_share) {
            String shareBody = getString(R.string.share_desc);
            shareBody += "\n";
            shareBody += "https://play.google.com/store/apps/details?id=com.javawy.jordanpharmacyguide";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "دليل صيدليات الأردن");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "دليل صيدليات الأردن"));
        } else if (id == R.id.nav_developer) {
            Intent intent = new Intent(this, DeveloperActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutAppActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_close) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}