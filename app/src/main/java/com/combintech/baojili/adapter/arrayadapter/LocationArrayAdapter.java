package com.combintech.baojili.adapter.arrayadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.combintech.baojili.model.BJLocation;

import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class LocationArrayAdapter extends ArrayAdapter<BJLocation> {
    public LocationArrayAdapter(Context context, int textViewResourceId,
                                     List<BJLocation> bjLocationList) {
        super(context, textViewResourceId, bjLocationList );
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText( getItem(position).getName());

        return view;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());

        return view;
    }

}
