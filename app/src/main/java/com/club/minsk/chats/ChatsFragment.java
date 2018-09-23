package com.club.minsk.chats;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.R;
import com.club.minsk.App;
import com.club.minsk.db.Messages;
import com.club.minsk.utils.AppFragment;

public class ChatsFragment extends AppFragment {
    RecyclerView dialog_list;
    ChatAdapter adapter;
    SwipeRefreshLayout swipe_container;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        setTitle(R.string.messages_title);

        dialog_list = (RecyclerView) view.findViewById(R.id.recycler);
        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        dialog_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipe_container.setColorSchemeColors(App.app_color);

        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        dialog_list.setBackgroundColor(Color.WHITE);

        update();

        return view;
    }

    private void update() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe_container.setRefreshing(false);
            }
        }, 10000);
        new Messages().chats(new Messages.ChatsListener() {
            @Override
            public void run(Messages.ChatsList response) {
                swipe_container.setRefreshing(false);
                adapter = new ChatAdapter(response.chats);
                dialog_list.setAdapter(adapter);
                if (response.chats == null)
                    App.user_show(R.string.chats_is_empty);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            dialog_list.setAdapter(adapter);
        }
    }

    @Override
    public void onBack() {

    }
}
