package com.club.minsk.chats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.club.minsk.db.Messages;
import com.club.minsk.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Messages.ChatsList.Chat> chats;

    public ChatAdapter(List<Messages.ChatsList.Chat> chats) {
        if (chats == null)
            chats = new ArrayList<>();
        this.chats = chats;
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        Messages.ChatsList.Chat chat = chats.get(position);
        ChatHolder holder = (ChatHolder) holder1;
        holder.setData(chat);
    }
}
