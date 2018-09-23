package com.club.minsk.invites;

import android.content.Intent;
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
import com.club.minsk.ScannerActivity;
import com.club.minsk.invite_new.place.PlaceFragment;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.Cookies;
import com.github.clans.fab.FloatingActionButton;

public class InvitesFragment extends AppFragment {

    SwipeRefreshLayout swipe_container;
    protected RecyclerView events_list;
    InvitesAdapter invitesAdapter;

    FloatingActionButton event_add;
    Integer scrollSum = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reinvites, container, false);

        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        events_list = (RecyclerView) view.findViewById(R.id.events_list);
        event_add = (FloatingActionButton) view.findViewById(R.id.invites_help_new_invite_button);


        events_list.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe_container.setColorSchemeColors(App.app_color);
        event_add.setColorNormal(App.app_color);
        event_add.setColorPressed(App.app_color);
        event_add.setEnabled(true);

        invitesAdapter = new InvitesAdapter(this);
        events_list.setAdapter(invitesAdapter);


        setTitle(R.string.menu_invites, R.drawable.qr, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getActiveActivity().startActivity(new Intent(App.getActiveActivity(), ScannerActivity.class));
            }
        });

        events_list.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            if (scrollSum < 0)
                                scrollSum = 0;
                        } else {
                            if (scrollSum > 0)
                                scrollSum = 0;
                        }

                        if (scrollSum > 20 && event_add.getVisibility() == View.VISIBLE) {
                            event_add.hide(true);
                        }
                        if (scrollSum < -20 && event_add.getVisibility() == View.INVISIBLE) {
                            event_add.show(true);
                        }

                        scrollSum += dy;
                    }
                }

        );

        swipe_container.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        invitesAdapter.update(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe_container.setRefreshing(false);
                            }
                        }, 10000);
                    }
                }

        );

        event_add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        event_add.setEnabled(false);
                        App.event = null;
                        App.openFragment(new PlaceFragment());
                    }
                }
        );


        invitesAdapter.update(true);


        if (Cookies.getInt("first_start") == null){
            Cookies.set("first_start", "0");
            App.addFragment(new InvitesHelpFragment());
        }

        return view;
    }


    @Override
    public void onBack() {
        if (invitesAdapter != null) {
            events_list.setAdapter(invitesAdapter);
        }
    }
}
