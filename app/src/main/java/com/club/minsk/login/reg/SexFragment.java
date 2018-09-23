package com.club.minsk.login.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.AppFragment;

public class SexFragment extends AppFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sex, container, false);

        final ImageView sex_man = (ImageView)view.findViewById(R.id.sex_man);
        final ImageView sex_woman = (ImageView)view.findViewById(R.id.sex_woman);
        TextView sex_text = (TextView)view.findViewById(R.id.sex_text);
        TextView sex_man_text = (TextView)view.findViewById(R.id.sex_man_text);
        TextView sex_woman_text = (TextView)view.findViewById(R.id.sex_woman_text);

        sex_text.setText(Strings.get(R.string.select_your_sex));
        sex_man_text.setText(Strings.get(R.string.sex_man));
        sex_woman_text.setText(Strings.get(R.string.sex_woman));

        sex_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.owner.sex = Owners.OWNER_SEX_MAN;
                sex_man.setColorFilter(App.app_color);
                App.nextRegistrationFragment();
            }
        });
        sex_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.owner.sex = Owners.OWNER_SEX_WOMAN;
                sex_woman.setColorFilter(App.app_color);
                App.nextRegistrationFragment();

            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }
}
