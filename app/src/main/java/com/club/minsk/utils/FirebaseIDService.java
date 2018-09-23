package com.club.minsk.utils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.club.minsk.db.Devices;

public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        Cookies.set("device_token", FirebaseInstanceId.getInstance().getToken());
        new Devices().tokenUpdate(FirebaseInstanceId.getInstance().getToken());
    }
}