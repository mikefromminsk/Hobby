package com.club.minsk.invites;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.club.minsk.App;
import com.club.minsk.db.Events;
import com.club.minsk.R;
import com.club.minsk.db.utils.ApiRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InvitesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    InvitesFragment fragment;
    List<Long> event_id_list = new ArrayList<>();

    public InvitesAdapter(InvitesFragment fragment) {
        this.fragment = fragment;
    }

    boolean requestActive = false;

    int page_offset = 0;
    final int page_size = 20;
    boolean newRequest = true;

    public void update(boolean newRequest) {
        if (!requestActive && (page_offset % page_size == 0 || newRequest)) {
            this.newRequest = newRequest;
            if (newRequest)
                page_offset = 0;
            this.requestActive = true;
            new Events().select(page_offset, page_size,
                    new Events.ListListener() {
                        @Override
                        public void run(Events.ListResponse response) {
                            setData(response.event_id_list);
                            requestActive = false;
                            fragment.swipe_container.setRefreshing(false);
                        }
                    }, new ApiRequest.ErrorListener() {
                        @Override
                        public void run(ApiRequest.Error error) {
                            setData(null);
                            requestActive = false;
                            fragment.swipe_container.setRefreshing(false);
                        }
                    });
        }
    }

    void setData(List<Long> event_id_list) {
        if (event_id_list == null)
            event_id_list = new ArrayList<>();
        if (newRequest) {
            this.event_id_list = event_id_list;
            lastPosition = -1;
            if (this.event_id_list.size() == 0)
                App.user_show(R.string.events_not_exist);
        } else {
            this.event_id_list.addAll(event_id_list);
        }
        page_offset += event_id_list.size() == 0 ? 1 : page_size;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return event_id_list.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InviteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_holder, null));
    }

    private Events.Event getItem(int position) {
        return Events.get(event_id_list.get(position));
    }

    long beginAnimationTime;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        InviteHolder holder = (InviteHolder) holder1;
        Events.Event event = getItem(position);

        Events.Event prevEvent = position == 0 ? null : getItem(position - 1);
        Long prev_event_time = 0L;
        if (prevEvent != null)
            prev_event_time = prevEvent.event_time;

        holder.setEvent(event, prev_event_time, this, position);

        setAnimation(holder.view, position);

        if (position == getItemCount() - 1)
            update(false);
    }

    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition && new Date().getTime() - beginAnimationTime < 3000) {
            Animation animation = AnimationUtils.loadAnimation(App.getActiveActivity(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
