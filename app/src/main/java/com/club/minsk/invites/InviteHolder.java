package com.club.minsk.invites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Links;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.invite.InviteFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.FadeInNetworkImageView;
import com.club.minsk.utils.Format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InviteHolder extends RecyclerView.ViewHolder {


    public View view;
    RelativeLayout invite_item_title_layout;
    FrameLayout invite_item_layout;
    TextView invite_item_text;
    TextView invite_item_owner_name;
    FadeInNetworkImageView invite_item_owner_photo;
    TextView invite_item_title;
    TextView invite_item_status_text;
    FadeInNetworkImageView invite_item_map;
    ImageView invite_item_status_image;
    ImageView invite_item_picker;
    TextView invite_item_time;
    TextView invite_item_date;
    TextView invite_item_address;
    RelativeLayout event_item_adress_layout;
    RelativeLayout invite_item_status_layout;
    Display display;

    public InviteHolder(View view) {
        super(view);
        this.view = view;

        invite_item_status_layout = (RelativeLayout) view.findViewById(R.id.invite_item_status_layout);
        invite_item_status_image = (ImageView) view.findViewById(R.id.invite_item_status_image);
        invite_item_title_layout = (RelativeLayout) view.findViewById(R.id.invite_item_title_layout);
        invite_item_layout = (FrameLayout) view.findViewById(R.id.invite_item_layout);
        invite_item_text = (TextView) view.findViewById(R.id.invite_item_text);
        invite_item_status_text = (TextView) view.findViewById(R.id.invite_item_status_text);
        invite_item_owner_name = (TextView) view.findViewById(R.id.invite_item_owner_name);
        invite_item_owner_photo = (FadeInNetworkImageView) view.findViewById(R.id.invite_item_owner_photo);
        invite_item_title = (TextView) view.findViewById(R.id.invite_item_title);
        invite_item_map = (FadeInNetworkImageView) view.findViewById(R.id.invite_item_map);
        invite_item_picker = (ImageView) view.findViewById(R.id.invite_item_picker);
        invite_item_time = (TextView) view.findViewById(R.id.invite_item_time);
        invite_item_date = (TextView) view.findViewById(R.id.invite_item_date);
        invite_item_address = (TextView) view.findViewById(R.id.invite_item_address);
        event_item_adress_layout = (RelativeLayout) view.findViewById(R.id.event_item_adress_layout);

        display = ((WindowManager) App.getActiveActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    Events.Event event;
    RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    Integer position;


    public void setEvent(Events.Event event1,
                         Long prev_event_time,
                         RecyclerView.Adapter<RecyclerView.ViewHolder> adapter1,
                         Integer position1) {
        event = event1;
        adapter = adapter1;
        position = position1;

        Calendar prevEventDay = Calendar.getInstance();
        prevEventDay.setTimeInMillis(prev_event_time * 1000);
        prevEventDay.get(Calendar.DAY_OF_YEAR);

        Calendar eventDay = Calendar.getInstance();
        eventDay.setTimeInMillis(event.event_time * 1000);
        eventDay.get(Calendar.DAY_OF_YEAR);
        if (prevEventDay.get(Calendar.DAY_OF_YEAR) == eventDay.get(Calendar.DAY_OF_YEAR))
            invite_item_title_layout.setVisibility(View.GONE);
        else {
            eventDay.set(Calendar.HOUR_OF_DAY, 0);
            eventDay.set(Calendar.MINUTE, 0);
            eventDay.set(Calendar.SECOND, 0);
            invite_item_text.setText(Format.dateFormat(eventDay.getTimeInMillis() / 1000));
            invite_item_title_layout.setVisibility(View.VISIBLE);
        }

        invite_item_status_layout.setVisibility(View.GONE);
        for (Events.Event.MemberStatus memberStatus: event.members) {
            if (memberStatus.owner_id.equals(Owners.self().owner_id)){
                invite_item_status_layout.setVisibility(View.VISIBLE);
                InviteFragment.setStatus(Strings.get(R.string.your_status), memberStatus, event, invite_item_status_image, invite_item_status_text);
            }
        }

        if (event.event_title == null)
            invite_item_title.setVisibility(View.GONE);
        else
            invite_item_title.setText(event.event_title);

        invite_item_owner_name.setText(Owners.get(event.owner_id).owner_name);
        invite_item_owner_photo.setImageUrl(Links.get(Owners.get(event.owner_id).owner_avatar_link_id), App.getImageLoader());
        invite_item_address.setText(event.event_address);
        invite_item_time.setText(new SimpleDateFormat("kk:mm").format(new Date(event.event_time * 1000)));
        invite_item_date.setText(new SimpleDateFormat("dd MMM").format(new Date(event.event_time * 1000)).toUpperCase());

        if (event.event_image_link_id == null) {

            String staticMap = "https://maps.googleapis.com/maps/api/staticmap?"
                    + "&center=" + event.event_lat + "," + event.event_lon
                    + "&size=" + (int) (display.getWidth() - AndroidUtils.convertDpToPixel(16 * 2, App.getActiveActivity()))
                    + "x" + (int) AndroidUtils.convertDpToPixel(200, App.getActiveActivity())
                    + "&zoom=14&scale=1&maptype=roadmap&format=jpg&visual_refresh=true"
                    + "&key=" + Strings.get(R.string.google_maps_geocoding_api_key);
            invite_item_map.setImageUrl(staticMap, App.getImageLoader());
        } else {
            invite_item_map.setImageUrl(Links.get(event.event_image_link_id), App.getImageLoader());
            invite_item_picker.setVisibility(View.GONE);
        }

        event_item_adress_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.event_lat + "," + event.event_lon + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                App.getActiveActivity().startActivity(mapIntent);
            }
        });

        invite_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new InviteFragment(), "event", event);
            }
        });
    }


}
