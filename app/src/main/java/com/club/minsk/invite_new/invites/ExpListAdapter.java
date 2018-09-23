package com.club.minsk.invite_new.invites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Owners;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.FadeInNetworkImageView;

import java.util.ArrayList;
import java.util.List;

public class ExpListAdapter extends BaseExpandableListAdapter {

    private List<Group> groups;
    public List<Long> selected = new ArrayList<>();

    public ExpListAdapter(ArrayList<Group> groups, List<Long> selected) {
        this.groups = groups;
        if (selected == null)
            selected = new ArrayList<>();
        this.selected = selected;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    Integer select_in_group(Group group) {
        Integer selected_in_group = 0;
        for (Long owner_id : group.items)
            if (selected.indexOf(owner_id) != -1)
                selected_in_group++;
        return selected_in_group;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view,
                             ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) App.getActiveActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_invites_item, null);
        }

        TextView event_invites_item_text = (TextView) view.findViewById(R.id.event_invites_item_text);
        FadeInNetworkImageView event_invites_item_image = (FadeInNetworkImageView) view.findViewById(R.id.event_invites_item_image);
        ImageView event_invites_item_add = (ImageView) view.findViewById(R.id.event_invites_item_add);

        Group group = (Group) getGroup(groupPosition);

        event_invites_item_image.setVisibility(View.GONE);
        event_invites_item_text.setText(group.group_name + " (" + group.items.size() +")");
        event_invites_item_add.setSelected(select_in_group(group) == group.items.size());
        event_invites_item_add.setColorFilter(App.app_color);
        event_invites_item_add.setTag(group);

        event_invites_item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group group = (Group) view.getTag();

                if (select_in_group(group) != group.items.size()) {
                    for (Long owner_id : group.items)
                        if (selected.indexOf(owner_id) == -1)
                            selected.add(owner_id);
                } else {
                    for (Long owner_id : group.items)
                        if (selected.indexOf(owner_id) != -1)
                            selected.remove(owner_id);
                }
                notifyDataSetChanged();
            }
        });


        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) App.getActiveActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_invites_item, null);
        }

        TextView event_invites_item_text = (TextView) view.findViewById(R.id.event_invites_item_text);
        FadeInNetworkImageView event_invites_item_image = (FadeInNetworkImageView) view.findViewById(R.id.event_invites_item_image);
        ImageView event_invites_item_add = (ImageView) view.findViewById(R.id.event_invites_item_add);

        Group group = (Group) getGroup(groupPosition);
        Owners.Owner owner = Owners.get(group.items.get(childPosition));

        event_invites_item_image.setImageUrl(Links.get(owner.owner_avatar_link_id), App.getImageLoader());
        event_invites_item_text.setText(owner.owner_name);
        event_invites_item_add.setSelected(selected.indexOf(owner.owner_id) != -1);
        event_invites_item_add.setColorFilter(App.app_color);
        event_invites_item_add.setTag(owner);
        view.setTag(owner);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Owners.Owner owner = (Owners.Owner) view.getTag();
                App.openFragment(new OwnerFragment(), "owner_id", owner.owner_id);
            }
        });

        event_invites_item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Owners.Owner owner = (Owners.Owner) view.getTag();
                if (selected.indexOf(owner.owner_id) == -1)
                    selected.add(owner.owner_id);
                else
                    selected.remove(owner.owner_id);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}