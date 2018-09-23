package com.club.minsk.messages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.club.minsk.App;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Messages;
import com.club.minsk.db.tables.MessagesTable;
import com.club.minsk.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<MessagesTable.Message> messages;


    public MessagesAdapter(List<Long> messages) {
        this.messages = new ArrayList<>();
        if (messages != null)
            for (Long message_id : messages)
                this.messages.add(Messages.get(message_id));
        this.messages.add(0, Messages.getInstance().newMessageInstance());
        lastPosition = this.messages.size() - 1;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static final int SECURITY_MESSAGE = 0;
    static final int OUT_MESSAGE = 1;
    static final int IN_MESSAGE = 2;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return SECURITY_MESSAGE;
        MessagesTable.Message message = messages.get(position);
        return message.owner_id.equals(Owners.self().owner_id) ? OUT_MESSAGE : IN_MESSAGE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECURITY_MESSAGE)
            return new SecurityMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_security, null));
        if (viewType == OUT_MESSAGE)
            return new TextMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_my, null));
        if (viewType == IN_MESSAGE)
            return new TextMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_her, null));
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        View view = null;
        if (getItemViewType(position) == SECURITY_MESSAGE) {
            SecurityMessageHolder holder = (SecurityMessageHolder) holder1;
            holder.setData();
            view = holder.view;
        }
        if (getItemViewType(position) == OUT_MESSAGE || getItemViewType(position) == IN_MESSAGE) {
            TextMessageHolder holder = (TextMessageHolder) holder1;
            holder.setData(messages, position);
            view = holder.view;
        }
        setAnimation(view, position);
    }


    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(App.getActiveActivity(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    void dataChanged() {
        App.getActiveActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void setReadMessages() {
        for (MessagesTable.Message message : messages)
            if (message.owner_id != null && message.owner_id.equals(Owners.self().owner_id)
                    && message.message_read_time == null)
                message.message_read_time = new Date().getTime() / 1000;
        dataChanged();
    }


    public void addMessage(MessagesTable.Message message) {
        messages.add(message);
        dataChanged();
    }


}