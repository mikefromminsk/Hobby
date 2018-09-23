package com.club.minsk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.utils.AppFragment;

import java.util.List;

public class EmptyFragment extends AppFragment {

    private RecyclerView recycler;
    EmptyAdapter adapter;

    public void update() {
        new EmptyRequest().select(new EmptyRequest.Listener() {
            @Override
            public void run(EmptyRequest.Response response) {
                List<String> list = response.list;
                if (list == null)
                    App.user_show(R.string.empty);

                if (adapter == null) {
                    adapter = new EmptyAdapter(list);
                    recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recycler.setAdapter(adapter);
                } else {
                    adapter.setData(list);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        setTitle(R.string.menu_invites);

        Bundle args = getArguments();
        if (args != null){

        }

        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        update();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            recycler.setAdapter(adapter);
    }

    @Override
    public void onBack() {

    }
}
