package com.club.minsk.invites;

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
import com.club.minsk.utils.AppFragment;
import com.github.clans.fab.FloatingActionButton;

public class InvitesHelpFragment extends AppFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invites_help, container, false);

        TextView invites_help_new_invite_text = (TextView) view.findViewById(R.id.invites_help_new_invite_text);
        TextView invites_help_menu = (TextView) view.findViewById(R.id.invites_help_menu);
        TextView invites_help_scanner = (TextView) view.findViewById(R.id.invites_help_scanner);
        Button invites_help_close = (Button) view.findViewById(R.id.invites_help_close);

        invites_help_new_invite_text.setText(Strings.get(R.string.new_invite));
        invites_help_menu.setText(Strings.get(R.string.menu));
        invites_help_scanner.setText(Strings.get(R.string.qr_scanner));

        invites_help_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.backFragment();
            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }
}
