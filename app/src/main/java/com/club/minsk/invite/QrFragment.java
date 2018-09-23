package com.club.minsk.invite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.utils.FadeInNetworkImageView;

import java.net.URLEncoder;

public class QrFragment extends AppFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);


        FadeInNetworkImageView qr_image = (FadeInNetworkImageView) view.findViewById(R.id.qr_image);
        Bundle args = getArguments();
        if (args != null)
            qr_image.setImageUrl("https://chart.googleapis.com/chart?chs=300x300&cht=qr&choe=UTF-8&chl=" + URLEncoder.encode(args.getString("url", "")), App.getImageLoader());

        return view;
    }

    @Override
    public void onBack() {

    }
}
