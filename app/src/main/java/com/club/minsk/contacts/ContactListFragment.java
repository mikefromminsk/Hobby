package com.club.minsk.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.club.minsk.R;
import com.club.minsk.db.Strings;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class ContactListFragment extends Fragment {

    List<Long>owner_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, null);

        Bundle args = getArguments();
        if (args != null) {
            long[] input = args.getLongArray("owner_list");
            if (input != null) {
                Long[] inputBoxed = ArrayUtils.toObject(input);
                owner_list = Arrays.asList(inputBoxed);
            }
        }
        ListView listView = (ListView) view.findViewById(R.id.friends_list);
        TextView friends_empty_text = (TextView) view.findViewById(R.id.friends_empty_text);

        if (owner_list == null || owner_list.size() == 0){
            friends_empty_text.setText(Strings.get(R.string.owner_list_is_empty));
        }else{
            friends_empty_text.setVisibility(View.GONE);
        }

        ContactListAdapter adapter = new ContactListAdapter(owner_list);
        listView.setAdapter(adapter);

        return view;
    }
}
