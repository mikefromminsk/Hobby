package com.club.minsk.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.BuildConfig;
import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.db.tables.PushTable;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.Cookies;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends AppFragment {

    View view;

    @Override
    public void onBack() {

    }

    void setNotifySettingView(View view, String title, Long push_type) {
        TextView setting_notify_item_label = (TextView) view.findViewById(R.id.setting_notify_item_label);
        CheckBox setting_notify_item_check_box = (CheckBox) view.findViewById(R.id.setting_notify_item_check_box);

        setting_notify_item_label.setText(title);
        setting_notify_item_check_box.setChecked(!"0".equals(Cookies.get(PushTable.PUSH_TYPE_SETTING_VALUE_PREFIX + push_type)));

        setting_notify_item_check_box.setTag(push_type);
        setting_notify_item_check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox setting_notify_item_check_box1 = (CheckBox) view;
                Long push_type = (Long) view.getTag();
                Cookies.set(PushTable.PUSH_TYPE_SETTING_VALUE_PREFIX + push_type,
                        setting_notify_item_check_box1.isChecked() ? "1" : "0");
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);


        setTitle(R.string.menu_settings);


        TextView setting_notify_label = (TextView) view.findViewById(R.id.setting_notify_label);
        View setting_notify_1 = view.findViewById(R.id.setting_notify_1);
        View setting_notify_2 = view.findViewById(R.id.setting_notify_2);
        View setting_notify_5 = view.findViewById(R.id.setting_notify_5);
        View setting_notify_6 = view.findViewById(R.id.setting_notify_6);



        RelativeLayout setting_exit_layout = (RelativeLayout) view.findViewById(R.id.setting_exit_layout);
        TextView setting_exit_label = (TextView) view.findViewById(R.id.setting_exit_label);

        RelativeLayout setting_about_layout = (RelativeLayout) view.findViewById(R.id.setting_about_layout);
        TextView setting_about_label = (TextView) view.findViewById(R.id.setting_about_label);

        setting_about_label.setText(Strings.get(R.string.setting_about));

        setting_notify_label.setText(Strings.get(R.string.setting_notify));
        setNotifySettingView(setting_notify_1, Strings.get(R.string.new_invites), PushTable.PUSH_TYPE_INVITE_CREATE);
        setNotifySettingView(setting_notify_2, Strings.get(R.string.new_invite_from_friend), PushTable.PUSH_TYPE_INVITE_FRIEND_CREATE);
        setNotifySettingView(setting_notify_6, Strings.get(R.string.notify_likes), PushTable.PUSH_TYPE_OWNER_LIKE);
        setNotifySettingView(setting_notify_5, Strings.get(R.string.message_inserting), PushTable.PUSH_TYPE_MESSAGE_INSERT);

        setting_exit_label.setText(Strings.get(R.string.setting_logout));



        List<String> notify_setting_names = new ArrayList<>(Strings.getMapWithPrefix(Strings.STRING_LANG_PREFIX).values());
        Integer selected = notify_setting_names.indexOf(AndroidUtils.getLang(App.getActiveActivity()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(App.getActiveActivity(),
                android.R.layout.simple_spinner_item, notify_setting_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        setting_about_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Version " +  BuildConfig.VERSION_CODE);
                String about = "user id: " + Owners.self().owner_id  + "\n";
                about += "api host: " + new ApiRequest().getHost() + "\n";
                about += "api id: " + Cookies.get("app_id") + "\n";
                builder.setMessage(about);
                builder.show();
            }
        });


        setting_exit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.logout();
            }
        });

        return view;
    }

}
