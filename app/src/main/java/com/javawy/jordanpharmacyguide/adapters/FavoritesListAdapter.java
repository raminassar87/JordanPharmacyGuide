package com.javawy.jordanpharmacyguide.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javawy.jordanpharmacyguide.R;
import com.javawy.jordanpharmacyguide.activities.MainActivity;
import com.javawy.jordanpharmacyguide.utils.PharmacyGuideSQLLitehelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class FavoritesListAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

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

    public FavoritesListAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        // Nothing..
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
            public void onClick(final View v) {
                try {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getString(R.string.confirm_remove_fav_title))
                            .setMessage(getContext().getString(R.string.confirm_remove_fav))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String message = null;
                                    remove(dataModel);

                                    message = getContext().getString(R.string.remove) + " \"" + dataModel.getPharmacyName() + "\" " + getContext().getString(R.string.from_fav);
                                    addRemoveToFavorites("Remove", dataModel.getId());
                                    Snackbar.make(v, message, Snackbar.LENGTH_LONG).setAction("No action", null).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } catch(Exception e) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter);
                    e.printStackTrace(writer);

                    String exception =  stringWriter.getBuffer().toString();
                    System.out.println(stringWriter.getBuffer().toString());
                }

                }
        });

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