package com.club.minsk.invite;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.MainActivity;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Links;
import com.club.minsk.db.Members;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.invite_new.place.PlaceFragment;
import com.club.minsk.messages.MessagesFragment;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.CircularNetworkImageView;
import com.club.minsk.utils.FadeInNetworkImageView;
import com.github.clans.fab.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InviteFragment extends AppFragment {

    FloatingActionButton event_menu;
    FloatingActionButton event_more;
    FloatingActionButton event_update;
    FadeInNetworkImageView event_image;
    RelativeLayout event_small_owner_layout;
    RelativeLayout event_big_owner_layout;
    RelativeLayout event_address_layout;
    CircularNetworkImageView event_small_owner_avatar;
    CircularNetworkImageView event_big_owner_avatar;
    TextView event_small_owner_comment;
    TextView event_time;
    TextView event_date;
    TextView event_adress_text;
    TextView event_invite_text;
    FadeInNetworkImageView event_map_image;
    RelativeLayout event_chat_layout;
    RelativeLayout event_chat_button;
    TextView event_chat_title;
    RelativeLayout event_phone_layout;
    RelativeLayout event_phone_button;
    RelativeLayout event_uber_layout;
    RelativeLayout event_uber_button;
    LinearLayout event_members_layout;
    TextView event_uber_title;
    TextView event_phone_owner_name;
    TextView event_phone_number;
    TextView event_push_problem;
    SwipeRefreshLayout swipe_container;
    RelativeLayout event_subscibe_layout;
    RelativeLayout event_subscribe_shadow;
    TextView event_subscibe_text;
    TextView event_big_owner_name;
    TextView event_small_owner_name;

    Events.Event event;
    String action;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        hideTitle();

        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        event_menu = (FloatingActionButton) view.findViewById(R.id.event_menu);
        event_more = (FloatingActionButton) view.findViewById(R.id.event_more);
        event_update = (FloatingActionButton) view.findViewById(R.id.event_update);
        event_image = (FadeInNetworkImageView) view.findViewById(R.id.event_image);
        event_small_owner_layout = (RelativeLayout) view.findViewById(R.id.event_small_owner_layout);
        event_big_owner_layout = (RelativeLayout) view.findViewById(R.id.event_big_owner_layout);
        event_address_layout = (RelativeLayout) view.findViewById(R.id.event_address_layout);
        event_small_owner_avatar = (CircularNetworkImageView) view.findViewById(R.id.event_small_owner_photo);
        event_big_owner_avatar = (CircularNetworkImageView) view.findViewById(R.id.event_big_owner_photo);
        event_small_owner_comment = (TextView) view.findViewById(R.id.event_small_owner_comment);
        event_time = (TextView) view.findViewById(R.id.event_time);
        event_date = (TextView) view.findViewById(R.id.event_date);
        event_adress_text = (TextView) view.findViewById(R.id.event_adress_text);
        event_invite_text = (TextView) view.findViewById(R.id.event_invite_text);
        event_map_image = (FadeInNetworkImageView) view.findViewById(R.id.event_map_image);
        event_chat_layout = (RelativeLayout) view.findViewById(R.id.owner_chat_layout);
        event_chat_button = (RelativeLayout) view.findViewById(R.id.event_chat_button);
        event_chat_title = (TextView) view.findViewById(R.id.event_chat_title);
        event_phone_layout = (RelativeLayout) view.findViewById(R.id.event_phone_layout);
        event_phone_button = (RelativeLayout) view.findViewById(R.id.event_phone_button);
        event_uber_layout = (RelativeLayout) view.findViewById(R.id.event_uber_layout);
        event_uber_button = (RelativeLayout) view.findViewById(R.id.event_uber_button);
        event_members_layout = (LinearLayout) view.findViewById(R.id.event_members_layout);
        event_uber_title = (TextView) view.findViewById(R.id.event_uber_title);
        event_phone_owner_name = (TextView) view.findViewById(R.id.event_phone_owner_name);
        event_phone_number = (TextView) view.findViewById(R.id.event_phone_number);
        event_push_problem = (TextView) view.findViewById(R.id.event_push_problem);
        event_subscibe_layout = (RelativeLayout) view.findViewById(R.id.event_subscibe_layout);
        event_subscribe_shadow = (RelativeLayout) view.findViewById(R.id.event_subscribe_shadow);
        event_subscibe_text = (TextView) view.findViewById(R.id.event_subscibe_text);
        event_big_owner_name = (TextView) view.findViewById(R.id.event_big_owner_name);
        event_small_owner_name = (TextView) view.findViewById(R.id.event_small_owner_name);

        Bundle args = getArguments();
        if (args != null) {
            action = args.getString("action", "");
            if (args.getSerializable("event") != null) {
                setEvent((Events.Event) args.getSerializable("event"));
            } else {
                update(args.getLong("event_id", 0));
            }
        }

        event_subscibe_text.setText(Strings.get(R.string.subscribe));
        event_chat_title.setText(Strings.get(R.string.chat));
        event_uber_title.setText(Strings.get(R.string.uber));
        event_subscibe_layout.setBackgroundColor(App.app_color);

        event_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) App.getActiveActivity()).drawer.openDrawer(GravityCompat.START);
            }
        });

        event_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> chooseList = new ArrayList<>();
                chooseList.add(Strings.get(R.string.share));
                chooseList.add(Strings.get(R.string.show_qr_code));
                if (event.owner_id.equals(Owners.self().owner_id))
                    chooseList.add(Strings.get(R.string.delete));
                else if (event.members.indexOf(Owners.self().owner_id) != -1)
                    chooseList.add(Strings.get(R.string.event_unjoin));

                AlertDialog.Builder builder = new AlertDialog.Builder(App.getActiveActivity());
                builder.setItems(chooseList.toArray(new CharSequence[chooseList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choose) {
                        if (choose == 0) {
                            String shareText = "http://" + App.getHost() + "/invite?id=" + event.event_id;
                            AndroidUtils.shareText(App.getActiveActivity(), shareText);
                        }
                        if (choose == 1) {
                            App.addFragment(new QrFragment(), "url", "http://" + App.getHost() + "/invite?id=" + event.event_id);
                        }
                        if (choose == 2) {
                            if (event.owner_id.equals(Owners.self().owner_id)) {
                                event.event_visible = 0L;

                                final ProgressDialog waitDialog = new ProgressDialog(App.getActiveActivity());
                                waitDialog.setMessage(Strings.get(R.string.event_insert_loading_dialog));
                                waitDialog.setCancelable(false);
                                waitDialog.show();
                                new Events().insertUpdateEvent(event, null, new Events.InsertUpdateEventListener() {
                                    @Override
                                    public void run(Events.InsertUpdateEventResponse response) {
                                        App.user_show(R.string.invite_deleted);
                                        waitDialog.dismiss();
                                    }
                                }, new ApiRequest.ErrorListener() {
                                    @Override
                                    public void run(ApiRequest.Error error) {
                                        App.user_show(R.string.invite_deleted_error);
                                        waitDialog.dismiss();
                                    }
                                });
                            } else {
                                new Members().insert(Members.ATTACH_TYPE_INVITE, event.event_id, new Members.MemberInsertListener() {
                                    @Override
                                    public void run(Members.MemberInsertResponse response) {
                                        event.members.remove(event.members.indexOf(Owners.self().owner_id));
                                        setEvent(event);
                                    }
                                });
                            }
                        }
                    }
                });
                builder.show();

            }
        });

        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update(event.event_id);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_container.setRefreshing(false);
                    }
                }, 10000);
            }
        });

        event_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.event = event;
                App.openFragment(new PlaceFragment());
            }
        });


        event_small_owner_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new OwnerFragment(), "owner_id", event.owner_id);
            }
        });

        event_big_owner_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new OwnerFragment(), "owner_id", event.owner_id);
            }
        });

        View.OnClickListener openGoogleMaps = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.event_lat + "," + event.event_lon + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        };
        event_map_image.setOnClickListener(openGoogleMaps);
        event_address_layout.setOnClickListener(openGoogleMaps);


        event_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new MessagesFragment(), "attach_type", Members.ATTACH_TYPE_INVITE, "attach_id", event.event_id);
            }
        });


        event_phone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                            return;
                        App.getActiveActivity().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Owners.get(event.owner_id).owner_phone)));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }, Manifest.permission.CALL_PHONE);

            }
        });

        event_uber_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = App.getActiveActivity().getPackageManager();
                try {
                    pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                    String uri = "uber://?action=setPickup&pickup=" + event.event_lat + "," + event.event_lon;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ubercab")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.ubercab")));
                    }
                }
            }
        });

        event_subscibe_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Members().subscribe(event.event_id, new Members.MemberInsertListener() {
                    @Override
                    public void run(Members.MemberInsertResponse response) {
                        setEvent(Events.get(event.event_id));
                    }
                });
            }
        });

        event_push_problem.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        event_image.setDefaultImageResId(R.drawable.invite_image_placeholder);
    }

    public static void setStatus(String prefix, Events.Event.MemberStatus memberStatus, Events.Event event, ImageView image, TextView text){
        if (prefix != null)
            prefix += " ";
        else
            prefix = "";
        switch ("" + memberStatus.member_visible) {
            case ("" + Events.INVITE_ACCEPTED):
                if (memberStatus.owner_id.equals(event.owner_id)) {
                    text.setText(prefix + Strings.get(R.string.event_owner));
                    image.setImageResource(R.drawable.invite_owner);
                } else {
                    text.setText(prefix + Strings.get(R.string.event_member));
                    image.setImageResource(R.drawable.invite_accepted);
                }
                break;
            case ("" + Events.INVITE_REJECTED):
                text.setText(prefix + Strings.get(R.string.event_member_rejected));
                image.setImageResource(R.drawable.invite_rejected);

                break;
            case ("" + Events.INVITE_DELIVERED):
                text.setText(prefix + Strings.get(R.string.invite_delivered));
                image.setImageResource(R.drawable.invite_delivered);

                break;
            case ("" + Events.INVITE_SEND):
                text.setText(prefix + Strings.get(R.string.invite_sended));
                image.setImageResource(R.drawable.invite_sended);
                break;
        }

    }

    void setEvent(Events.Event event1) {

        if (event1 == null)
            return;

        this.event = event1;

        event_image.setDefaultImageResId(R.drawable.invite_image_placeholder);
        if (event.event_image_link_id != null)
            event_image.setImageUrl(Links.get(event.event_image_link_id), App.getImageLoader());
        else
            event_image.getLayoutParams().height = (int) AndroidUtils.convertDpToPixel(200f, App.getActiveActivity());

        if (event.event_title != null) {
            event_small_owner_layout.setVisibility(View.VISIBLE);
            event_big_owner_layout.setVisibility(View.GONE);
            event_small_owner_avatar.setImageUrl(Links.get(Owners.get(event.owner_id).owner_avatar_link_id), App.getImageLoader());
            event_small_owner_comment.setText(event.event_title);
            event_small_owner_name.setText(Owners.get(event.owner_id).owner_first_name);
        } else {
            event_small_owner_layout.setVisibility(View.GONE);
            event_big_owner_layout.setVisibility(View.VISIBLE);
            event_big_owner_avatar.setImageUrl(Links.get(Owners.get(event.owner_id).owner_avatar_link_id), App.getImageLoader());
            event_big_owner_name.setText(Owners.get(event.owner_id).owner_first_name);
        }

        event_time.setText(new SimpleDateFormat("kk:mm").format(new Date(event.event_time * 1000)));
        event_date.setText(new SimpleDateFormat("dd MMM").format(new Date(event.event_time * 1000)).toUpperCase());
        event_adress_text.setText(event.event_address);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String staticMap = "https://maps.googleapis.com/maps/api/staticmap?"
                        + "&center=" + event.event_lat + "," + event.event_lon
                        + "&size=" + (int) App.getActiveActivity().getWindowManager().getDefaultDisplay().getWidth()
                        + "x" + (int) AndroidUtils.convertDpToPixel(260, App.getActiveActivity())
                        + "&zoom=14&scale=1&maptype=roadmap&format=jpg&visual_refresh=true";
                event_map_image.setImageUrl(staticMap, App.getImageLoader());
            }
        });

        event_members_layout.removeAllViews();
        if (event.members != null) {
            LayoutInflater inflater = (LayoutInflater) App.getActiveActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            event_invite_text.setText(Strings.get(R.string.invited));
            for (Events.Event.MemberStatus member_status : event.members) {
                Owners.Owner owner = Owners.get(member_status.owner_id);
                View view = inflater.inflate(R.layout.event_invite_item, null);

                CircularNetworkImageView event_invite_owner_photo = (CircularNetworkImageView) view.findViewById(R.id.event_invite_owner_photo);
                TextView event_invite_owner_name = (TextView) view.findViewById(R.id.event_invite_owner_name);
                TextView event_invite_status_text = (TextView) view.findViewById(R.id.event_invite_status_text);
                ImageView event_invite_status_image = (ImageView) view.findViewById(R.id.event_invite_status_image);

                event_invite_owner_photo.setImageUrl(Links.get(owner.owner_avatar_link_id), App.getImageLoader());
                event_invite_owner_name.setText(owner.owner_name);

                setStatus(null, member_status, event, event_invite_status_image, event_invite_status_text);

                view.setTag(member_status.owner_id);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Long owner_id = (Long) view.getTag();
                        App.openFragment(new OwnerFragment(), "owner_id", owner_id);
                    }
                });
                event_members_layout.addView(view);

            }
        }


        event_phone_owner_name.setText(Owners.get(event.owner_id).owner_first_name);
        event_phone_number.setText(Owners.get(event.owner_id).owner_phone);

        if (event.members != null) {
            event_subscribe_shadow.setVisibility(View.GONE);
            event_subscibe_layout.setVisibility(View.GONE);
            for (Events.Event.MemberStatus memberStatus : event.members)
                if (memberStatus.owner_id.equals(Owners.self().owner_id) && memberStatus.member_visible != 1) {
                    event_subscribe_shadow.setVisibility(View.VISIBLE);
                    event_subscibe_layout.setVisibility(View.VISIBLE);
                }
        }

    }

    public void update(Long event_id) {
        new Events().selectEvent(event_id, action, new Events.EventListener() {
            @Override
            public void run(Events.EventResponse response) {
                swipe_container.setRefreshing(false);
                setEvent(Events.get(response.event_id));
            }
        });
    }


    @Override
    public void onBack() {

    }
}