package com.club.minsk.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.club.minsk.db.Strings;
import com.club.minsk.db.fb.FBUser;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.vk.VKUsers;
import com.club.minsk.login.reg.PhoneFragment;
import com.club.minsk.login.reg.SexFragment;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestGetFriendsCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.utils.AppFragment;
import com.vk.sdk.VKScope;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocialFragment extends AppFragment implements OnLoginCompleteListener,
        SocialNetworkManager.OnInitializationCompleteListener {

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";


    SocialFragment instance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        instance = this;

        RelativeLayout social_reg_layout = (RelativeLayout) view.findViewById(R.id.social_reg_layout);
        RelativeLayout social_vk_layout = (RelativeLayout) view.findViewById(R.id.social_vk_layout);
        RelativeLayout social_fb_layout = (RelativeLayout) view.findViewById(R.id.social_fb_layout);

        TextView social_reg_text = (TextView) view.findViewById(R.id.social_reg_text);
        TextView social_vk_text = (TextView) view.findViewById(R.id.social_vk_text);
        TextView social_fb_text = (TextView) view.findViewById(R.id.social_fb_text);
        TextView social_login_with = (TextView) view.findViewById(R.id.social_login_with);
        TextView social_or = (TextView) view.findViewById(R.id.social_or);

        social_reg_text.setText(Strings.get(R.string.registration));
        social_vk_text.setText(Strings.get(R.string.vkontakte));
        social_fb_text.setText(Strings.get(R.string.facebook));
        social_login_with.setText(Strings.get(R.string.login_with));
        social_or.setText(Strings.get(R.string.social_or));

        social_reg_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.nextRegistrationFragment();
            }
        });
        social_vk_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocialNetworkManager.getSocialNetwork(SocialNetwork.VK).requestLogin(instance);
            }
        });
        social_fb_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocialNetworkManager.getSocialNetwork(SocialNetwork.FB).requestLogin(instance);
            }
        });

        return view;
    }

    SocialNetworkManager mSocialNetworkManager;

    @Override
    public void onStart() {
        super.onStart();

        ArrayList<String> fbScope = new ArrayList<>();
        fbScope.addAll(Arrays.asList("user_friends, email, user_photos, user_birthday"));

        String[] vkScope = new String[]{
                VKScope.FRIENDS,
                VKScope.PHOTOS,
                VKScope.OFFLINE,
        };

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, "" + App.getIntResByName("com_vk_sdk_AppId"), vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();

            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            onSocialNetworkManagerInitialized();
        }
    }


    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks())
            if (socialNetwork.isConnected())
                socialNetwork.logout();
    }


    @Override
    public void onBack() {

    }

    void getFriends(int id){

        mSocialNetworkManager.getSocialNetwork(id).requestGetFriends(new OnRequestGetFriendsCompleteListener() {
            @Override
            public void onGetFriendsIdComplete(int socialNetworkID, String[] friendsID) {
                if (socialNetworkID == SocialNetwork.FB)
                    App.owner.social_name = "facebook";
                if (socialNetworkID == SocialNetwork.VK)
                    App.owner.social_name = "vkontakte";
                App.owner.social_members = friendsID;
            }

            @Override
            public void onGetFriendsComplete(int socialNetworkID, List<SocialPerson> socialFriends) {

            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

            }
        });

        App.nextRegistrationFragment();
    }

    @Override
    public void onLoginSuccess(final int id) {
        if (id == SocialNetwork.VK) {
            VKRequest getOwner = VKApi.users().get(VKParameters.from("fields",
                    "photo_50,photo_max,crop_photo,sex,bdate"));
            getOwner.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    VKUsers VKUserVkRequest = ApiRequest.json.fromJson(response.json.toString(), VKUsers.class);
                    VKUsers.VKUser user = VKUserVkRequest.response.get(0);
                    if (user != null) {
                        App.owner.first_name = user.first_name;
                        App.owner.last_name = user.last_name;
                        App.owner.sex = user.sex == 2 ? "M" : "W";
                        App.owner.bdate = user.getUnixBirthday();
                        App.owner.avatar = user.photo_max;
                        App.owner.photo = user.crop_photo.photo.photo_807;
                        App.owner.vk_id = "" + user.id;
                        getFriends(id);
                    }
                }

                @Override
                public void onError(VKError error) {

                }
            });
        }
        if (id == SocialNetwork.FB) {
            GraphRequest request = GraphRequest.newMeRequest(
                    com.facebook.AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            if (response.getError() != null) {
                                return;
                            }
                            FBUser user = ApiRequest.json.fromJson(me.toString(), FBUser.class);

                            App.owner.first_name = user.first_name;
                            App.owner.last_name = user.last_name;
                            App.owner.sex = user.gender.equals("male") ? "M" : "W";
                            App.owner.bdate = user.getUnixBirthday();
                            App.owner.photo = user.picture.data.url;
                            App.owner.fb_id = user.id;
                            App.owner.email = user.email;
                            getFriends(id);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields",
                    "id,name,email,birthday,first_name,last_name,gender,picture.height(800).width(800)");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    @Override
    public void onResume() {
        super.onResume();/*
        if (App.getSocialNetwork() != null)
            App.openFragment(new StartFragment());*/
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        App.user_show(R.string.login_error);
    }
}
