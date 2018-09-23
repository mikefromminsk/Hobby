package com.club.minsk.owner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.MainActivity;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Members;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.utils.ImageUpload;
import com.club.minsk.invite_new.place.PlaceFragment;
import com.club.minsk.messages.MessagesFragment;
import com.club.minsk.utils.FadeInNetworkImageView;
import com.club.minsk.utils.Format;
import com.club.minsk.utils.ImagePikerFragment;
import com.github.clans.fab.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class OwnerFragment extends ImagePikerFragment {

    FloatingActionButton owner_menu;
    FloatingActionButton owner_more;
    FloatingActionButton owner_call_button;
    FloatingActionButton owner_like_button;
    TextView owner_name;
    TextView owner_owner_year_and_city;
    TextView owner_online;
    TextView owner_message;
    RelativeLayout owner_chat_layout;
    RelativeLayout owner_chat_button;
    RelativeLayout owner_chat_color;
    TextView owner_chat_text;
    RelativeLayout owner_inviting_layout;
    RelativeLayout owner_inviting_color;
    RelativeLayout owner_inviting_button;
    TextView owner_inviting_text;
    RelativeLayout owner_phone_layout;
    RelativeLayout owner_phone_color;
    RelativeLayout owner_phone_button;
    TextView owner_phone_number;
    TextView owner_phone_name;
    TextView owner_add_to_friends_text;
    RelativeLayout owner_add_to_friends_shadow;
    RelativeLayout owner_add_to_friends_layout;
    FloatingActionButton owner_add_photo;
    SwipeRefreshLayout swipe_container;
    ProgressBar image_upload_progress_bar;
    FadeInNetworkImageView owner_photo;
    RelativeLayout owner_name_layout;
    RelativeLayout owner_contact_layout_shadow;
    RelativeLayout owner_contact_layout;
    RelativeLayout owner_contact_buttons_layout;
    RelativeLayout owner_vkontakte_layout;
    RelativeLayout owner_vkontakte_button;
    RelativeLayout owner_vkontakte_color;
    ImageView owner_vkontakte_image;
    TextView owner_vkontakte_text;
    RelativeLayout owner_facebook_layout;
    RelativeLayout owner_facebook_button;
    RelativeLayout owner_facebook_color;
    ImageView owner_facebook_image;
    TextView owner_facebook_text;


    Long owner_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner, container, false);

        hideTitle();

        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        owner_menu = (FloatingActionButton) view.findViewById(R.id.owner_menu);
        owner_more = (FloatingActionButton) view.findViewById(R.id.owner_more);
        owner_photo = (FadeInNetworkImageView) view.findViewById(R.id.owner_photo);
        owner_call_button = (FloatingActionButton) view.findViewById(R.id.owner_call_button);
        owner_like_button = (FloatingActionButton) view.findViewById(R.id.owner_like_button);
        owner_name = (TextView) view.findViewById(R.id.owner_name);
        owner_owner_year_and_city = (TextView) view.findViewById(R.id.owner_owner_year_and_city);
        owner_online = (TextView) view.findViewById(R.id.owner_online);
        owner_message = (TextView) view.findViewById(R.id.owner_message);
        owner_chat_layout = (RelativeLayout) view.findViewById(R.id.owner_chat_layout);
        owner_chat_button = (RelativeLayout) view.findViewById(R.id.owner_chat_button);
        owner_chat_text = (TextView) view.findViewById(R.id.owner_chat_text);
        owner_inviting_layout = (RelativeLayout) view.findViewById(R.id.owner_inviting_layout);
        owner_inviting_button = (RelativeLayout) view.findViewById(R.id.owner_inviting_button);
        owner_inviting_text = (TextView) view.findViewById(R.id.owner_inviting_text);
        owner_phone_layout = (RelativeLayout) view.findViewById(R.id.owner_phone_layout);
        owner_phone_button = (RelativeLayout) view.findViewById(R.id.owner_phone_button);
        owner_phone_number = (TextView) view.findViewById(R.id.owner_phone_number);
        owner_phone_name = (TextView) view.findViewById(R.id.owner_phone_name);
        owner_add_to_friends_text = (TextView) view.findViewById(R.id.owner_add_to_friends_text);
        owner_add_to_friends_shadow = (RelativeLayout) view.findViewById(R.id.owner_add_to_friends_shadow);
        owner_add_to_friends_layout = (RelativeLayout) view.findViewById(R.id.owner_add_to_friends_layout);
        owner_name_layout = (RelativeLayout) view.findViewById(R.id.owner_name_layout);
        owner_contact_layout = (RelativeLayout) view.findViewById(R.id.owner_contact_layout);
        owner_contact_layout_shadow = (RelativeLayout) view.findViewById(R.id.owner_contact_layout_shadow);
        owner_contact_buttons_layout = (RelativeLayout) view.findViewById(R.id.owner_contact_buttons_layout);
        owner_phone_color = (RelativeLayout) view.findViewById(R.id.owner_phone_color);
        owner_inviting_color = (RelativeLayout) view.findViewById(R.id.owner_inviting_color);
        owner_chat_color = (RelativeLayout) view.findViewById(R.id.owner_chat_color);
        owner_add_photo = (FloatingActionButton) view.findViewById(R.id.owner_add_photo);
        image_upload_progress_bar = (ProgressBar) view.findViewById(R.id.image_upload_progress_bar);
        owner_vkontakte_layout = (RelativeLayout) view.findViewById(R.id.owner_vkontakte_layout);
        owner_vkontakte_button = (RelativeLayout) view.findViewById(R.id.owner_vkontakte_button);
        owner_vkontakte_color = (RelativeLayout) view.findViewById(R.id.owner_vkontakte_color);
        owner_vkontakte_image = (ImageView) view.findViewById(R.id.owner_vkontakte_image);
        owner_vkontakte_text = (TextView) view.findViewById(R.id.owner_vkontakte_text);
        owner_facebook_layout = (RelativeLayout) view.findViewById(R.id.owner_facebook_layout);
        owner_facebook_button = (RelativeLayout) view.findViewById(R.id.owner_facebook_button);
        owner_facebook_color = (RelativeLayout) view.findViewById(R.id.owner_facebook_color);
        owner_facebook_image = (ImageView) view.findViewById(R.id.owner_facebook_image);
        owner_facebook_text = (TextView) view.findViewById(R.id.owner_facebook_text);

        owner_add_to_friends_layout.setBackgroundColor(App.app_color);
        owner_add_to_friends_text.setText(Strings.get(R.string.add_to_friends));
        owner_inviting_text.setText(Strings.get(R.string.inviting));
        owner_chat_text.setText(Strings.get(R.string.chat));
        owner_vkontakte_text.setText(Strings.get(R.string.vkontakte));

        Bundle args = getArguments();
        if (args != null) {
            owner_id = args.getLong("owner_id");
            if (Owners.get(owner_id) != null)
                setOwner(Owners.get(owner_id));
            else
                update();
        }

        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_container.setRefreshing(false);
                    }
                }, 10000);
            }
        });

        owner_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) App.getActiveActivity()).drawer.openDrawer(GravityCompat.START);
            }
        });

        owner_more.setVisibility(View.GONE);

        owner_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickImageDialog();
            }
        });

        owner_call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (ActivityCompat.checkSelfPermission(App.getActiveActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                            return;
                        App.getActiveActivity().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + owner.owner_phone)));
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

        owner_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (owner.is_like_request == 1) {
                    owner_like_button.setColorNormal(Color.WHITE);
                    owner_like_button.setImageResource(R.drawable.unlike);
                } else {
                    owner_like_button.setColorNormal(getResources().getColor(R.color.invite_like));
                    owner_like_button.setImageResource(R.drawable.menu_likes);
                }
                new Members().insert(Members.ATTACH_TYPE_LIKE_OWNER,
                        owner.owner_id, new Members.MemberInsertListener() {
                            @Override
                            public void run(Members.MemberInsertResponse response) {
                                setOwner(Owners.get(owner_id));
                            }
                        });
            }
        });

        owner_chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new MessagesFragment(), "attach_type", Members.ATTACH_TYPE_DIALOG, "attach_id", owner_id);
            }
        });
        owner_inviting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.openFragment(new PlaceFragment(), "invited", owner_id);
            }
        });

        owner_add_to_friends_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Members().insert(Members.ATTACH_TYPE_FRIENDS,
                        owner.owner_id, new Members.MemberInsertListener() {
                            @Override
                            public void run(Members.MemberInsertResponse response) {
                                setOwner(Owners.get(owner_id));
                            }
                        });
            }
        });

        owner_vkontakte_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    App.getActiveActivity().getPackageManager().getPackageInfo("com.vkontakte.android", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vkontakte://profile/" + owner.vk_owner_id)));
                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id" + owner.vk_owner_id)));
                }
            }
        });
        owner_facebook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    App.getActiveActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + owner.fb_owner_id)));
                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=" + owner.fb_owner_id)));
                }
            }
        });

        return view;
    }

    void update() {
        swipe_container.setRefreshing(true);
        new Owners().getProfile(owner_id, new Owners.ProfileListener() {
            @Override
            public void run(Owners.ProfileResponse response) {
                setOwner(Owners.get(owner_id));
                swipe_container.setRefreshing(false);
            }
        });
    }

    Owners.Owner owner;

    private void setOwner(Owners.Owner owner) {
        this.owner = owner;

        image_upload_progress_bar.setVisibility(View.GONE);
        owner_photo.setImageUrl(Links.get(owner.owner_photo_link_id), App.getImageLoader());

        if (owner.is_like_request == 0) {
            owner_like_button.setColorNormal(Color.WHITE);
            owner_like_button.setImageResource(R.drawable.unlike);
        } else {
            owner_like_button.setColorNormal(getResources().getColor(R.color.invite_like));
            owner_like_button.setImageResource(R.drawable.menu_likes);
        }

        owner_name.setText(owner.owner_name);
        owner_phone_name.setText(owner.owner_name);
        owner_owner_year_and_city.setText(Format.bdateFormat(owner.owner_birthdate) + " " + owner.owner_city);
        owner_online.setText(Format.onlineFormat(owner.owner_login_time));


        if (owner_id.equals(Owners.self().owner_id)) {
            owner_add_to_friends_layout.setVisibility(View.GONE);
            owner_add_to_friends_shadow.setVisibility(View.GONE);
            owner_add_photo.setVisibility(View.VISIBLE);
            owner_contact_layout.setVisibility(View.GONE);
            owner_contact_layout_shadow.setVisibility(View.GONE);
            owner_call_button.setVisibility(View.GONE);
            owner_like_button.setVisibility(View.GONE);
        } else {
            owner_add_photo.setVisibility(View.GONE);

            if (owner.is_liked == 1 || owner.is_friend_request == 1) {
                owner_chat_color.setBackgroundColor(getResources().getColor(R.color.invite_chat));
                owner_chat_button.setClickable(true);
                owner_contact_layout_shadow.setVisibility(View.VISIBLE);
            } else {
                owner_chat_color.setBackgroundColor(getResources().getColor(R.color.invite_disabled));
                owner_chat_button.setClickable(false);
            }

            if (owner.is_friend == 1)
                owner_message.setText(Strings.get(R.string.is_friend));
            else if (owner.is_friend_request == 1)
                owner_message.setText(Strings.get(R.string.is_friend_request));
            else if (owner.is_liked == 1)
                owner_message.setText(Strings.get(R.string.is_liked));
            else
                owner_message.setText(Strings.get(R.string.not_in_friends));


            if (owner.is_friend_request == 1) {
                owner_add_to_friends_layout.setVisibility(View.GONE);
                owner_add_to_friends_shadow.setVisibility(View.GONE);
            } else {
                owner_add_to_friends_layout.setVisibility(View.VISIBLE);
                owner_add_to_friends_shadow.setVisibility(View.VISIBLE);
            }

            if (owner.is_friend == 1) {
                owner_call_button.setEnabled(true);
                owner_call_button.setColorNormal(getResources().getColor(R.color.invite_phone));
                owner_phone_color.setBackgroundColor(getResources().getColor(R.color.invite_phone));
                owner_facebook_color.setBackgroundColor(getResources().getColor(R.color.invite_fb));
                owner_vkontakte_color.setBackgroundColor(getResources().getColor(R.color.invite_vk));
                owner_facebook_button.setEnabled(true);
                owner_vkontakte_button.setEnabled(true);
                owner_phone_button.setClickable(true);
            } else {
                owner_call_button.setEnabled(false);
                owner_call_button.setColorNormal(getResources().getColor(R.color.invite_disabled));
                owner_phone_color.setBackgroundColor(getResources().getColor(R.color.invite_disabled));
                owner_facebook_color.setBackgroundColor(getResources().getColor(R.color.invite_disabled));
                owner_vkontakte_color.setBackgroundColor(getResources().getColor(R.color.invite_disabled));
                owner_facebook_button.setEnabled(false);
                owner_vkontakte_button.setEnabled(false);
                owner_phone_button.setClickable(false);
            }

            owner_facebook_layout.setVisibility(owner.fb_owner_id != null ? View.VISIBLE : View.GONE);
            owner_vkontakte_layout.setVisibility(owner.vk_owner_id != null ? View.VISIBLE : View.GONE);

            if (owner.owner_phone != null) {
                String phone = owner.owner_phone.substring(0,4);
                if (owner.is_friend == 0)
                    for (int i = 5; i < owner.owner_phone.length(); i++)
                        phone += "*";
                owner_phone_number.setText(phone);
            } else
                owner_phone_layout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBack() {

    }

    @Override
    public void pickImage(String selectedBitmap) {
        Bitmap uploading_bitmap = convertBitmap(selectedBitmap, 800, 70);

        image_upload_progress_bar.setVisibility(View.VISIBLE);
        if (uploading_bitmap != null)
            new ImageUpload(uploading_bitmap, new ImageUpload.Listener() {
                @Override
                public void run(ImageUpload.Response response) {
                    new Owners().changeAvatar(response.link_id, new Owners.Listener() {
                        @Override
                        public void run(Owners.LoginData loginData) {
                            update();
                            image_upload_progress_bar.setVisibility(View.GONE);
                        }
                    }, new ApiRequest.ErrorListener() {
                        @Override
                        public void run(ApiRequest.Error error) {
                            App.user_show(R.string.image_error);
                            image_upload_progress_bar.setVisibility(View.GONE);
                        }
                    });
                }
            }, new ApiRequest.ErrorListener() {
                @Override
                public void run(ApiRequest.Error error) {
                    App.user_show(R.string.image_error);
                    image_upload_progress_bar.setVisibility(View.GONE);
                }
            });
        else {
            App.user_show(R.string.image_error);
            image_upload_progress_bar.setVisibility(View.GONE);
        }
    }

}
