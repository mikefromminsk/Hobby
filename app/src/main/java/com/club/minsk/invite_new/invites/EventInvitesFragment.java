package com.club.minsk.invite_new.invites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Members;
import com.club.minsk.db.Strings;
import com.club.minsk.db.tables.OwnersTable;
import com.club.minsk.invite.InviteFragment;
import com.club.minsk.utils.AppFragment;
import com.github.clans.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class EventInvitesFragment extends AppFragment {

    ExpListAdapter adapter;
    ExpandableListView event_invites_list;

    MaterialEditText event_invites_search;
    RelativeLayout event_invites_filter_layout;
    RelativeLayout event_invites_search_layout;
    FrameLayout event_invites_ready_layout;
    TextView event_invites_ready_text;
    TextView event_invites_girls_text;
    Switch event_invites_girls_switch;
    TextView event_invites_boys_text;
    Switch event_invites_boys_switch;
    Switch event_invites_date_switch;
    TextView event_invites_years_from_text;
    TextView event_invites_years_text;
    SeekBar event_invites_years_seek;
    TextView event_invites_date_text;
    TextView event_invites_party_text;
    ImageView event_invites_arrow;
    FloatingActionButton event_invites_search_button;

    Integer start_year = 18;
    Integer end_year = 40;


    void update() {
        String sex = App.event.event_filter_sex;
        Long min_years = App.event.event_filter_min_year;
        String search_name = event_invites_search.getText().toString();

        new Members().memberList(sex, min_years, search_name, new Members.ListListener() {
            @Override
            public void run(Members.ListResponse response) {
                ArrayList<Group> groups = new ArrayList<>();
                if (App.event.invite_list == null)
                    App.event.invite_list = new ArrayList<>();
                if (App.event.members != null)
                    for (Events.Event.MemberStatus memberStatus : App.event.members)
                        if (App.event.invite_list.indexOf(memberStatus.owner_id) == -1)
                            App.event.invite_list.add(memberStatus.owner_id);
                if (App.event.invite_list.size() > 0)
                    groups.add(new Group(Strings.get(R.string.invited), App.event.invite_list));
                if (response.search != null)
                    groups.add(new Group(Strings.get(R.string.search), response.search));
                if (response.beside != null)
                    groups.add(new Group(Strings.get(R.string.beside), response.beside));
                if (response.friends != null)
                    groups.add(new Group(Strings.get(R.string.friends), response.friends));
                if (response.friends_of_friends != null)
                    groups.add(new Group(Strings.get(R.string.friends_of_friends), response.friends_of_friends));
                adapter = new ExpListAdapter(groups, App.event.invite_list);
                event_invites_list.setAdapter(adapter);
                event_invites_list.collapseGroup(0);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_invites, container, false);
        setTitle(R.string.menu_invites);

        event_invites_list = (ExpandableListView) view.findViewById(R.id.event_invites_list);
        event_invites_search = (MaterialEditText) view.findViewById(R.id.event_invites_search);
        event_invites_filter_layout = (RelativeLayout) view.findViewById(R.id.event_invites_filter_layout);
        event_invites_search_layout = (RelativeLayout) view.findViewById(R.id.event_invites_search_layout);
        event_invites_ready_layout = (FrameLayout) view.findViewById(R.id.event_invites_ready_layout);
        event_invites_ready_text = (TextView) view.findViewById(R.id.event_invites_ready_text);
        event_invites_girls_text = (TextView) view.findViewById(R.id.event_invites_girls_text);
        event_invites_girls_switch = (Switch) view.findViewById(R.id.event_invites_girls_switch);
        event_invites_boys_text = (TextView) view.findViewById(R.id.event_invites_boys_text);
        event_invites_boys_switch = (Switch) view.findViewById(R.id.event_invites_boys_switch);
        event_invites_date_switch = (Switch) view.findViewById(R.id.event_invites_date_switch);
        event_invites_years_from_text = (TextView) view.findViewById(R.id.event_invites_years_from_text);
        event_invites_years_text = (TextView) view.findViewById(R.id.event_invites_years_text);
        event_invites_years_seek = (SeekBar) view.findViewById(R.id.event_invites_years_seek);
        event_invites_date_text = (TextView) view.findViewById(R.id.event_invites_date_text);
        event_invites_party_text = (TextView) view.findViewById(R.id.event_invites_party_text);
        event_invites_arrow = (ImageView) view.findViewById(R.id.event_invites_arrow);
        event_invites_search_button = (FloatingActionButton) view.findViewById(R.id.event_invites_search_button);
        event_invites_search_button.requestFocus();

        event_invites_girls_text.setText(Strings.get(R.string.event_filter_girls));
        event_invites_boys_text.setText(Strings.get(R.string.event_filter_boys));
        event_invites_years_from_text.setText(Strings.get(R.string.event_filter_years_from));
        event_invites_party_text.setText(Strings.get(R.string.event_filter_party));
        event_invites_date_text.setText(Strings.get(R.string.event_filter_date));

        event_invites_search.setText("");
        event_invites_search.setHint(Strings.get(R.string.search));
        event_invites_search.setUnderlineColor(App.app_color);
        event_invites_search.setBaseColor(App.app_color);
        event_invites_ready_layout.setBackgroundColor(App.app_color);
        event_invites_ready_text.setText(Strings.get(R.string.ready));

        event_invites_boys_switch.setChecked(App.event.event_filter_sex == null || App.event.event_filter_sex.equals(OwnersTable.OWNER_SEX_MAN));
        event_invites_girls_switch.setChecked(App.event.event_filter_sex == null || App.event.event_filter_sex.equals(OwnersTable.OWNER_SEX_WOMAN));
        event_invites_years_seek.setMax(end_year - start_year);
        event_invites_years_seek.setProgress(App.event.event_filter_min_year == null ? 0 : (int) (App.event.event_filter_min_year - start_year));
        event_invites_years_text.setText((event_invites_years_seek.getProgress() + start_year) + " " + Strings.get(R.string.event_filter_years));
        event_invites_date_switch.setChecked(!App.event.event_filter_max_members.equals(2L));

        event_invites_filter_layout.setVisibility(View.GONE);
        event_invites_search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event_invites_filter_layout.getVisibility() == View.VISIBLE) {
                    event_invites_filter_layout.setVisibility(View.GONE);
                    event_invites_arrow.setImageResource(R.drawable.arrow_bottom);
                } else {
                    event_invites_filter_layout.setVisibility(View.VISIBLE);
                    event_invites_arrow.setImageResource(R.drawable.arrow_top);
                }
            }
        });

        event_invites_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
                if (event_invites_filter_layout.getVisibility() == View.VISIBLE) {
                    event_invites_filter_layout.setVisibility(View.GONE);
                    event_invites_arrow.setImageResource(R.drawable.arrow_bottom);
                }
            }
        });

        event_invites_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                update();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        event_invites_years_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                App.event.event_filter_min_year = (long) (event_invites_years_seek.getProgress() + start_year);
                event_invites_years_text.setText((event_invites_years_seek.getProgress() + start_year)
                        + " " + Strings.get(R.string.event_filter_years));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                update();
            }
        });


        event_invites_boys_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSex();
            }
        });
        event_invites_girls_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSex();
            }
        });

        event_invites_date_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                App.event.event_filter_max_members = (b ? 100L : 2L);
            }
        });

        event_invites_ready_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.insertEvent(adapter.selected, new Events.InsertUpdateEventListener() {
                    @Override
                    public void run(Events.InsertUpdateEventResponse response) {
                        if (response != null)
                            App.openFragment(new InviteFragment(), "event_id", response.event_id);
                    }
                });
            }
        });

        update();

        return view;
    }


    void setSex() {
        if (event_invites_boys_switch.isChecked() && event_invites_girls_switch.isChecked())
            App.event.event_filter_sex = null;
        if (!event_invites_boys_switch.isChecked() && event_invites_girls_switch.isChecked())
            App.event.event_filter_sex = OwnersTable.OWNER_SEX_WOMAN;
        if (event_invites_boys_switch.isChecked() && !event_invites_girls_switch.isChecked())
            App.event.event_filter_sex = OwnersTable.OWNER_SEX_MAN;
        if (!event_invites_boys_switch.isChecked() && !event_invites_girls_switch.isChecked())
            App.event.event_filter_sex = null;
        update();
    }

    @Override
    public void onBack() {
    }
}
