package com.javawy.jordanpharmacyguide.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ViewDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Populate Drower Layout
        populateDrowerLayout();

        try {
            // Fetch Pharmacy Details..
            fetchPharmacyDetails();

            Button shareResult = (Button)findViewById(R.id.shareResult);
            shareResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareResult();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch Pharmacy Details..
     */
    private void fetchPharmacyDetails() {

        Integer pharmacyId = getIntent().getIntExtra("pharmacyId", -1);

        PharmacyGuideSQLLitehelper dpHelper = new PharmacyGuideSQLLitehelper(this);
        SQLiteDatabase liteDatabase = dpHelper.getReadableDatabase();
        Cursor cursor =  null;
        String[] parameter = new String[] {pharmacyId.toString()};

        try {
            cursor = liteDatabase.query("PHARMACY",
                    new String[]{"_id","NAME","CITY","ADDRESS","CONTACT_NUMBER","FAX","EMAIL"},
                    "_id = ?",
                    parameter,
                    null, null, null);

            // Populate Details..
            populateDetails(cursor);

        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);

            String exception =  stringWriter.getBuffer().toString();
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

            TextView pharmacyContactValue = (TextView) findViewById(R.id.pharmacyContactValue);
            pharmacyContactValue.setText(cursor.getString(4));

            TextView faxValue = (TextView) findViewById(R.id.faxValue);
            faxValue.setText(cursor.getString(5));

            TextView pharmacyEmailValue = (TextView) findViewById(R.id.pharmacyEmailValue);
            pharmacyEmailValue.setText(cursor.getString(6));

            if(!pharmacyEmailValue.getText().equals(getString(R.string.not_exists))) {
                pharmacyEmailValue.setGravity(android.view.Gravity.END);
            }
            if(!faxValue.getText().equals(getString(R.string.not_exists))) {
                faxValue.setGravity(android.view.Gravity.END);
            }
            if(!pharmacyContactValue.getText().equals(getString(R.string.not_exists))) {
                pharmacyContactValue.setGravity(android.view.Gravity.END);
            }
        }
    }

    /**
     * Populate Drower Layout
     */
    private void populateDrowerLayout() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        } catch(Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            ex.printStackTrace(writer);

            String exception =  stringWriter.getBuffer().toString();
            System.out.println(stringWriter.getBuffer().toString());
        }
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