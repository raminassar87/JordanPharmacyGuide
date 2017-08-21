package com.javawy.jordanpharmacyguide.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.activities.ViewDetailsActivity;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static android.R.drawable.btn_star_big_on;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView pharmacyName;
        TextView txtType;
        TextView txtVersion;
        ImageView favIcon;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;
/*
        switch (v.getId()) {
            case R.id.fav_icon:
                String message = null;
                if(getIsFavorites(dataModel.getId())) {
                    message = "Removed From Favorite";
                    addRemoveToFavorites("Remove",dataModel.getId());
                    v.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off));
                } else {
                    message = "Added To Favorite";
                    addRemoveToFavorites("Add",dataModel.getId());
                    v.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));
                }
                Snackbar.make(v, dataModel.getPharmacyName() + message, Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            break;
        }
*/
    }

    private int lastPosition = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataModel dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            //viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.pharmacyName = (TextView) convertView.findViewById(R.id.pharmacy_name);
            viewHolder.favIcon = (ImageView) convertView.findViewById(R.id.fav_icon);
            //viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.pharmacyName.setText(dataModel.getPharmacyName());

        if(getIsFavorites(dataModel.getId())) {
            viewHolder.favIcon.setBackground(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));
        } else {
            viewHolder.favIcon.setBackground(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off));
        }

        viewHolder.favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = null;
                if(getIsFavorites(dataModel.getId())) {
                    message = getContext().getString(R.string.remove) + " \"" +
                                dataModel.getPharmacyName() + "\" " + getContext().getString(R.string.from_fav);
                    addRemoveToFavorites("Remove",dataModel.getId());
                    v.setBackground(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off));
                } else {
                    message = getContext().getString(R.string.added) + " \"" +
                            dataModel.getPharmacyName() + "\" " + getContext().getString(R.string.to_fav);
                    addRemoveToFavorites("Add",dataModel.getId());
                    v.setBackground(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));
                }

                Snackbar.make(v, message, Snackbar.LENGTH_LONG).setAction("No action", null).show();
            }
        });

        /*
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        */
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Get Is Favorites
     *
     * @param id : Pharmacy id
     * @return True if Pharmacy included in Favorite List
     */
    private boolean getIsFavorites(Integer id) {

        PharmacyGuideSQLLitehelper dpHelper = new PharmacyGuideSQLLitehelper(getContext());
        SQLiteDatabase liteDatabase = dpHelper.getReadableDatabase();
        Cursor cursor =  null;

        try {
            cursor = liteDatabase.query("PHARMACY",
                    new String[]{"IS_FAVORITE"},
                    "_id = ?",
                    new String[] {id.toString()},
                    null, null, null);
            if (!cursor.moveToFirst()) {
                return false;
            }
            Integer isFavorites = cursor.getInt(0);
            return isFavorites == 2;

        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);

            String exception =  stringWriter.getBuffer().toString();

            e.printStackTrace();
            return false;
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
     * Add Remove To Favorites
     *
     * @param mode : "Add" / "Remove"
     * @param id : Pharmacy Id
     */
    private void addRemoveToFavorites(String mode,Integer id) {
        PharmacyGuideSQLLitehelper dpHelper = null;
        SQLiteDatabase liteDatabase = null;

        try {
            dpHelper = new PharmacyGuideSQLLitehelper(getContext());
            liteDatabase = dpHelper.getReadableDatabase();

            ContentValues isFavoriteVal = new ContentValues();

            if(mode == "Add") {
                isFavoriteVal.put("IS_FAVORITE", 2);
            } else {
                isFavoriteVal.put("IS_FAVORITE", 1);
            }

            liteDatabase.update("PHARMACY",
                    isFavoriteVal,
                    "_id = ?",
                    new String[] {id.toString()});
        } finally {
            if(liteDatabase!= null) {
                    liteDatabase.close();
            }
        }
    }
}