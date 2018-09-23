package com.club.minsk.login.reg;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.start.StartFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.Cookies;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.commons.lang.StringUtils;

import br.com.sapereaude.maskedEditText.MaskedEditText;


public class PhoneFragment extends AppFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_phone, container, false);


        final TextView phone_title = (TextView) view.findViewById(R.id.phone_title);
        final Button phone_ready = (Button) view.findViewById(R.id.phone_ready);
        final MaskedEditText phone_text = (MaskedEditText) view.findViewById(R.id.phone_text);

        phone_ready.setEnabled(false);
        phone_ready.setText(Strings.get(R.string.next));
        phone_title.setText(Strings.get(R.string.phone_for_your_friends));

        phone_text.requestFocus();

        TelephonyManager tm = (TelephonyManager) App.getActiveActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String phone_mask = Strings.get("phone_mask_" + tm.getSimCountryIso().toLowerCase());
        if (phone_mask.indexOf(Strings.unset) != -1)
            phone_mask = "+#(###)###-##-##";
        phone_text.setMask(phone_mask);
        phone_text.setHint(StringUtils.repeat("5", StringUtils.countMatches(phone_mask, "#")));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            TelephonyManager tMgr = (TelephonyManager) App.getActiveActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String phone_number = tMgr.getLine1Number();
            if (phone_number != null) {
                phone_text.setText(phone_number);
                App.owner.phone = phone_text.getText().toString();
                onClick(view);
            }

        } else
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Dexter.checkPermission(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            TelephonyManager tMgr = (TelephonyManager) App.getActiveActivity().getSystemService(Context.TELEPHONY_SERVICE);
                            String phone_number = tMgr.getLine1Number();

                            if (phone_number != null && !phone_number.isEmpty()) {
                                String phone_mask = phone_text.getMask();
                                int count_numbers = StringUtils.countMatches(phone_mask, "#");
                                String phone_number_without_code = phone_number.substring(phone_number.length() - count_numbers);
                                phone_text.setText(phone_number_without_code);
                                App.owner.phone = phone_text.getText().toString();
                                //onClick(view);
                            } else
                                AndroidUtils.showKeyboard(App.getActiveActivity());
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            AndroidUtils.showKeyboard(App.getActiveActivity());
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }, Manifest.permission.READ_PHONE_STATE);
                }
            }, 1500);

        phone_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone_ready.setEnabled(phone_text.getText().length() == phone_text.getMask().length());
                App.owner.phone = phone_text.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phone_ready.setOnClickListener(this);

        return view;
    }


    @Override
    public void onBack() {

    }

    @Override
    public void onClick(View view) {
        new Owners().reg(
                App.owner.first_name,
                App.owner.last_name,
                App.owner.sex,
                App.owner.avatar,
                App.owner.photo,
                App.owner.vk_id,
                App.owner.fb_id,
                App.owner.bdate,
                App.owner.email,
                App.owner.phone,
                App.owner.social_name,
                App.owner.social_members,
                new Owners.Listener() {
                    @Override
                    public void run(Owners.LoginData loginData) {
                        Cookies.set("owner_id", "" + loginData.owner_id);
                        App.nextRegistrationFragment();
                    }
                }, new ApiRequest.ErrorListener() {
                    @Override
                    public void run(ApiRequest.Error error) {
                        App.user_show(R.string.registration_error);
                        AndroidUtils.showKeyboard(App.getActiveActivity());
                    }
                }
        );
    }
}
