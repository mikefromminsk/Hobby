package com.club.minsk.invite_new.place;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.Geocoder;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Geocoder.Prediction> list;

    public AutocompleteAdapter(List<Geocoder.Prediction> list) {
        setData(list);
    }

    public void setData(List<Geocoder.Prediction> events) {
        this.list = events;
        if (this.list == null)
            this.list = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 1 : list.size();
    }


    int ITEM = 0;
    int EMPTY = 1;

    @Override
    public int getItemViewType(int position) {
        if (list.size() != 0)
            return ITEM;
        else
            return EMPTY;
    }

    class AutocompleteEmptyHolder extends RecyclerView.ViewHolder {
        public AutocompleteEmptyHolder(View view) {
            super(view);
            TextView autocomplete_empty_text = (TextView)view.findViewById(R.id.autocomplete_empty_text);
            autocomplete_empty_text.setText(Strings.get(R.string.place_not_found));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM)
            return new AutocompleteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_item, null));
        if (viewType == EMPTY)
            return new AutocompleteEmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_empty, null));
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (getItemViewType(position) == ITEM){
            AutocompleteHolder holder = (AutocompleteHolder) holder1;
            Geocoder.Prediction prediction = list.get(position);
            holder.setData(prediction);
        }
    }

}