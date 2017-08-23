package com.javawy.jordanpharmacyguide.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * View Details Activity
 *
 * @author Rami Nassar
 */
public class ViewDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /** List View */
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Populate Drawer Layout
        populateDrawerLayout();

        try {
            // Fetch Pharmacy Details..
            fetchPharmacyDetails();

            // Load Page Add..
            loadPageAdd();

            Button shareResult = (Button) findViewById(R.id.shareResult);
            shareResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareResult();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Page Add
     */
    private void loadPageAdd() {
        mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    /**
     * Fetch Pharmacy Details..
     */
    private void fetchPharmacyDetails() {

        Integer pharmacyId = getIntent().getIntExtra("pharmacyId", -1);

        PharmacyGuideSQLLitehelper dpHelper = new PharmacyGuideSQLLitehelper(this);
        SQLiteDatabase liteDatabase = dpHelper.getReadableDatabase();
        Cursor cursor = null;
        String[] parameter = new String[]{pharmacyId.toString()};

        try {
            cursor = liteDatabase.query("PHARMACY",
                    new String[]{"_id", "NAME", "CITY", "ADDRESS", "CONTACT_NUMBER", "FAX", "EMAIL"},
                    "_id = ?",
                    parameter,
                    null, null, null);

            // Populate Details..
            populateDetails(cursor);

        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);

            String exception = stringWriter.getBuffer().toString();
        } finally {
            cursor.close();
            liteDatabase.close();
        }
    }

    /**
     * Populate List
     *
     * @param cursor : Cursor
     */
    private void populateDetails(Cursor cursor) {

        if (cursor.moveToFirst()) {
            TextView pharmacyNameValue = (TextView) findViewById(R.id.pharmacyNameValue);
            pharmacyNameValue.setText(cursor.getString(1));

            TextView pharmacyCityValue = (TextView) findViewById(R.id.pharmacyCityValue);
            pharmacyCityValue.setText(cursor.getString(2));

            TextView pharmacyAddressValue = (TextView) findViewById(R.id.pharmacyAddressValue);
            pharmacyAddressValue.setText(cursor.getString(3));

            final TextView pharmacyContactValue = (TextView) findViewById(R.id.pharmacyContactValue);
            pharmacyContactValue.setText(cursor.getString(4));

            TextView faxValue = (TextView) findViewById(R.id.faxValue);
            faxValue.setText(cursor.getString(5));

            TextView pharmacyEmailValue = (TextView) findViewById(R.id.pharmacyEmailValue);
            pharmacyEmailValue.setText(cursor.getString(6));

            if (!pharmacyEmailValue.getText().equals(getString(R.string.not_exists))) {
                pharmacyEmailValue.setGravity(android.view.Gravity.END);
            }
            if (!faxValue.getText().equals(getString(R.string.not_exists))) {
                faxValue.setGravity(android.view.Gravity.END);
            }
            if (!pharmacyContactValue.getText().equals(getString(R.string.not_exists))) {
                pharmacyContactValue.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_call, 0, 0, 0);
                pharmacyContactValue.setCompoundDrawablePadding(0);
                pharmacyContactValue.setGravity(Gravity.END);
                //callIcon.setBackground(ContextCompat.getDrawable(ViewDetailsActivity.this, R.mipmap.ic_call));
                pharmacyContactValue.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        makeCall();
                    }
                });
            }
        }
    }

    /**
     * Make a Call
     */
    public void makeCall() {
        final TextView pharmacyContactValue = (TextView) findViewById(R.id.pharmacyContactValue);

        // Check Self Permission..
        if (ActivityCompat.checkSelfPermission(ViewDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+pharmacyContactValue.getText()));
        ActivityCompat.checkSelfPermission(ViewDetailsActivity.this, Manifest.permission.CALL_PHONE);

        // Start Activity..
        startActivity(intent);
    }

    /**
     * Request Permission
     */
    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewDetailsActivity.this, Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(ViewDetailsActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                ActivityCompat.requestPermissions(ViewDetailsActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        } catch(Throwable e) {
            // Nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                }
                break;
        }
    }

    /**
     * Populate Drawer Layout
     */
    private void populateDrawerLayout() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Back Button
     *
     * @param view : View
     */
    public void backButton(View view) {
        super.onBackPressed();
    }

    /**
     * Share Result Intent
     */
    private void shareResult() {
        final StringBuffer shareTextObj = new StringBuffer("");

        TextView pharmacyNameValue = (TextView) findViewById(R.id.pharmacyNameValue);
        TextView pharmacyCityValue = (TextView) findViewById(R.id.pharmacyCityValue);
        TextView pharmacyAddressValue = (TextView) findViewById(R.id.pharmacyAddressValue);
        TextView pharmacyContactValue = (TextView) findViewById(R.id.pharmacyContactValue);
        TextView faxValue = (TextView) findViewById(R.id.faxValue);
        TextView pharmacyEmailValue = (TextView) findViewById(R.id.pharmacyEmailValue);

        if(!pharmacyNameValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.pharmacy_name)).append(" ").append(pharmacyNameValue.getText()).append("\n");;
        }
        if(!pharmacyCityValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.city_name)).append(" ").append(pharmacyCityValue.getText()).append("\n");;
        }
        if(!pharmacyAddressValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.pharmacy_location)).append(" ").append(pharmacyAddressValue.getText()).append("\n");;
        }
        if(!pharmacyContactValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.pharmacy_contact)).append(" ").append(pharmacyContactValue.getText()).append("\n");;
        }
        if(!faxValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.fax)).append(" ").append(faxValue.getText()).append("\n");;
        }
        if(!pharmacyEmailValue.getText().equals(getString(R.string.not_exists))) {
            shareTextObj.append(getString(R.string.pharmacy_email)).append(" ").append(pharmacyEmailValue.getText()).append("\n");;
        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, pharmacyNameValue.getText());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareTextObj.toString());
        startActivity(Intent.createChooser(sharingIntent, pharmacyNameValue.getText()));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_page) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(this, FavoriteActivity.class);
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