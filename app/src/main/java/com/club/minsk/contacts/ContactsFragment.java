package com.club.minsk.contacts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Members;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.AppFragment;

import org.apache.commons.lang.ArrayUtils;

import java.util.List;

public class ContactsFragment extends AppFragment {

    ViewPager likes_pager;
    TabLayout sliding_tabs;
    ContactsPagerAdapter adapter;

    ContactListFragment setOwners(List<Long> owners) {
        ContactListFragment fragment = new ContactListFragment();
        if (owners != null) {
            Bundle args = new Bundle();
            Long[] arr = new Long[owners.size()];
            arr = owners.toArray(arr);
            args.putLongArray("owner_list", ArrayUtils.toPrimitive(arr));
            fragment.setArguments(args);
        }
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        setTitle(Strings.get(R.string.menu_contacts));

        likes_pager = (ViewPager) view.findViewById(R.id.likes_pager);
        sliding_tabs = (TabLayout) view.findViewById(R.id.sliding_tabs);
        sliding_tabs.setBackgroundColor(App.app_color);
        sliding_tabs.setTabTextColors(Color.WHITE, Color.WHITE);
        sliding_tabs.setSelectedTabIndicatorColor(Color.WHITE);

        new Members().memberList(null, null, null,
                    new Members.ListListener() {
                        @Override
                        public void run(Members.ListResponse response) {
                            adapter = new ContactsPagerAdapter(getChildFragmentManager());
                            adapter.addFragment(setOwners(response.friends), Strings.get(R.string.friends));
                            adapter.addFragment(setOwners(response.friends_of_friends), Strings.get(R.string.friends_of_friends));
                            adapter.addFragment(setOwners(response.beside), Strings.get(R.string.beside));
                            adapter.addFragment(setOwners(response.likes), Strings.get(R.string.likes));
                            likes_pager.setAdapter(adapter);
                            sliding_tabs.setupWithViewPager(likes_pager);
                        }
                    });

        return view;
    }

    @Override
    public void onBack() {
    }
}
