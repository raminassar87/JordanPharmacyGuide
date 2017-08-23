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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.adapters.CustomAdapter;
import com.javawy.jordanpharmacyguide.adapters.DataModel;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;
import com.javawy.jordanpharmacyguide.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Result Activity
 *
 * @author Rami Nassar
 */
public class ResultActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Fields
     */

    /** Data Models */
    ArrayList<DataModel> dataModels;

    /** List View */
    ListView listView;

    /** Custom Adapter */
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        String pharmacyName = intent.getStringExtra("pharmacyName");
        String city = intent.getStringExtra("city");
        String pharmacyLocation = intent.getStringExtra("pharmacyLocation");

        // Fetch Pharmacies..
        fetchPharmacies(pharmacyName,city,pharmacyLocation);

        // Populate Drawer Layout
        populateDrawerLayout();

        TextView message = (TextView)findViewById(R.id.not_exists_message);
        listView = (ListView)findViewById(R.id.results);
        if(listView.getAdapter().getCount() == 0) {
            message.setVisibility(View.VISIBLE);
        } else {
            message.setVisibility(View.GONE);
        }
    }

    /**
     * Populate Drawer Layout
     */
    private void populateDrawerLayout() {
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
     * Fetch Pharmacies..
     */
    private void fetchPharmacies(String name,String city,String location) {

        String pharmacyNameParam = getIntent().getStringExtra("pharmacyName");
        String pharmacyLocationParam = getIntent().getStringExtra("pharmacyLocation");
        String cityParam = getIntent().getStringExtra("city");

        pharmacyNameParam = !Utils.isBlankOrNull(pharmacyNameParam) ? pharmacyNameParam : "";
        pharmacyLocationParam = !Utils.isBlankOrNull(pharmacyLocationParam) ? pharmacyLocationParam : "" ;
        cityParam = !Utils.isBlankOrNull(cityParam) ? cityParam : "" ;

        String[] parameters = new String[3];
        String whereClause = "";

        whereClause += " NAME LIKE ? AND ADDRESS LIKE ? AND CITY LIKE ? ";
        parameters[0] = "%" + pharmacyNameParam + "%";
        parameters[1] = "%" + pharmacyLocationParam + "%";
        parameters[2] = "%" + cityParam + "%";

        PharmacyGuideSQLLitehelper dpHelper = new PharmacyGuideSQLLitehelper(this);
        SQLiteDatabase liteDatabase = dpHelper.getReadableDatabase();
        Cursor cursor =  null;

        try {
            cursor = liteDatabase.query("PHARMACY",
                    new String[]{"_id","NAME","CITY","ADDRESS","IS_FAVORITE"},
                    whereClause,
                    parameters,
                    null, null, null);

            // Populate List..
            populateList(cursor);

        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);

            String exception =  stringWriter.getBuffer().toString();

            e.printStackTrace();
        } finally {
            try {
                if(cursor != null) {
                    cursor.close();
                }
                liteDatabase.close();
            } catch (Exception e) {
                // Nothing..
            }
        }
    }

    /**
     * Populate List
     *
     * @param cursor : Cursor
     */
    private void populateList(Cursor cursor) {

        listView = (ListView)findViewById(R.id.results);
        dataModels = new ArrayList<>();

        if (cursor.moveToFirst()) {
            dataModels.add(new DataModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    "",
                    cursor.getString(0),
                    cursor.getString(0),
                    cursor.getInt(4)));
        };

        while(cursor.moveToNext()) {
            dataModels.add(new DataModel(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    "",
                    cursor.getString(0),
                    cursor.getString(0),
                    cursor.getInt(4)));
        }

        adapter = new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel= dataModels.get(position);
                Intent intent = new Intent(getApplicationContext(), ViewDetailsActivity.class);
                intent.putExtra("pharmacyId", dataModel.getId());
                startActivity(intent);
            }
        });
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
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(this, FavoriteActivity.class);
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
