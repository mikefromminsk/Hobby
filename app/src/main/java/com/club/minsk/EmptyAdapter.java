package com.club.minsk;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EmptyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> list;

    public EmptyAdapter(List<String> list) {
        setData(list);
    }

    public void setData(List<String> events) {
        this.list = events;
        if (this.list == null)
            this.list = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    int FIRST = 0;
    int OTHER = 1;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return FIRST;
        else
            return OTHER;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (viewType == FIRST)
            return new EmptyHsolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, null));
        if (viewType == OTHER)
            return new InviteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, null));*/
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        Object object = list.get(position);

        if (getItemViewType(position) == FIRST){
            EmptyHolder holder = (EmptyHolder) holder1;

        }
        /*if (getItemViewType(position) == OTHER){

        }*/
    }

}