package com.club.minsk.error;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;

public class ErrorFragment extends AppFragment {

    ApiRequest.Error error;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_robot, container, false);

        Button error_upgrade_app_button = (Button) view.findViewById(R.id.error_upgrade_app_button);
        TextView error_text = (TextView) view.findViewById(R.id.error_text);

        Bundle args = getArguments();
        if (args != null) {
            error = (ApiRequest.Error) args.getSerializable("error");
        }
        error_text.setText(error.error_message);
        error_upgrade_app_button.setText(Strings.get(R.string.update_app));
        error_upgrade_app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUtils.openAppInGooglePlay(App.getActiveActivity());
            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }
}
