package com.javawy.jordanpharmacyguide;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.javawy.jordanpharmacyguide.adapters.DataModel;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

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
                    new String[]{"_id","NAME","CITY","ADDRESS","CONTACT_NUMBER","EMAIL"},
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

            e.printStackTrace();
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

            String name = cursor.getString(1);

            TextView pharmacyCityValue = (TextView) findViewById(R.id.pharmacyCityValue);
            pharmacyCityValue.setText(cursor.getString(2));

            TextView pharmacyAddressValue = (TextView) findViewById(R.id.pharmacyAddressValue);
            pharmacyAddressValue.setText(cursor.getString(3));

            TextView pharmacyContactValue = (TextView) findViewById(R.id.pharmacyContactValue);
            pharmacyContactValue.setText(cursor.getString(4));

            TextView pharmacyEmailValue = (TextView) findViewById(R.id.pharmacyEmailValue);
            pharmacyEmailValue.setText(cursor.getString(5));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
