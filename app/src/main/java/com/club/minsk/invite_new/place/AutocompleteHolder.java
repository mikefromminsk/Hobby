package com.club.minsk.invite_new.place;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.utils.Geocoder;

public class AutocompleteHolder extends RecyclerView.ViewHolder {
    View view;

    public AutocompleteHolder(View view) {
        super(view);
        this.view = view;
    }
    Geocoder.Prediction prediction;
    void setData(Geocoder.Prediction prediction1){
        prediction = prediction1;
        TextView autocomplete_item_text = (TextView) view.findViewById(R.id.autocomplete_item_text);
        autocomplete_item_text.setText(prediction.description);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.getActiveFragment() instanceof PlaceFragment)
                    ((PlaceFragment)App.getActiveFragment()).setAutocomplete(prediction);
            }
        });
    }
}