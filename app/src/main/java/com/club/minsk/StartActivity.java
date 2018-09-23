package com.club.minsk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.club.minsk.login.SocialFragment;
import com.club.minsk.start.StartFragment;
import com.club.minsk.utils.Cookies;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (App.getActiveActivity() != this) {
            App.getInstance().setActiveActivity(this);
            if (Cookies.get("owner_id") != null)
                App.startFragment(new StartFragment());
            else
                App.startFragment(new SocialFragment());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED && data != null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(SocialFragment.SOCIAL_NETWORK_TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
