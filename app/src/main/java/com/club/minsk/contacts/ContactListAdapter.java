package com.club.minsk.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Owners;
import com.club.minsk.owner.OwnerFragment;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends BaseAdapter {

    List<Long> member_owner_id_list;

    public ContactListAdapter(List<Long> member_owner_id_list) {
        this.member_owner_id_list = member_owner_id_list;
        if (this.member_owner_id_list == null)
            this.member_owner_id_list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.member_owner_id_list.size();
    }

    @Override
    public Long getItem(int i) {
        return member_owner_id_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null)
            view = LayoutInflater.from(App.getActiveActivity()).inflate(R.layout.friend_item, null);


        RelativeLayout friend_item_title_layout = (RelativeLayout) view.findViewById(R.id.friend_item_title_layout);
        TextView friend_item_title = (TextView) view.findViewById(R.id.friend_item_title);

        RelativeLayout friend_layout = (RelativeLayout) view.findViewById(R.id.friend_layout);
        NetworkImageView friend_image = (NetworkImageView) view.findViewById(R.id.friend_image);
        TextView friend_name = (TextView) view.findViewById(R.id.friend_name);
        TextView fried_item_dist = (TextView) view.findViewById(R.id.fried_item_dist);


        Owners.Owner owner = Owners.get(getItem(position));

        friend_image.setImageUrl(Links.get(owner.owner_avatar_link_id), App.getImageLoader());
        friend_name.setText(owner.owner_name);

        friend_item_title_layout.setVisibility(View.GONE);
        fried_item_dist.setVisibility(View.GONE);


        friend_layout.setTag(owner);
        friend_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Owners.Owner friend = (Owners.Owner) view.getTag();
                App.openFragment(new OwnerFragment(), "owner_id", friend.owner_id);
            }
        });

        return view;
    }
}
