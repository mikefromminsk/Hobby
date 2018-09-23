package com.club.minsk.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.App;
import com.club.minsk.db.Owners;
import com.club.minsk.R;
import com.club.minsk.chats.ChatsFragment;
import com.club.minsk.invites.InvitesFragment;
import com.club.minsk.likes.LikesFragment;
import com.club.minsk.contacts.ContactsFragment;
import com.club.minsk.on_map.EventMapFragment;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.settings.SettingsFragment;

import java.util.Arrays;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> menu;

    public MenuAdapter() {
        menu = Arrays.asList("invites,map,likes,messages,contacts,settings".split(","));
        selected = 1;
    }

    @Override
    public int getItemCount() {
        return 1/*head*/ + menu.size();
    }

    final int MENU_HEAD = 0;
    final int MENU_ITEM = 1;

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return MENU_HEAD;
        return MENU_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MENU_HEAD)
            return new HeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_head, null));
        if (viewType == MENU_ITEM)
            return new MenuItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, null));
        return null;
    }


    Integer selected = -1;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

        if (getItemViewType(position) == MENU_HEAD) {
            ((HeadHolder) holder1).setData(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.startFragment(new OwnerFragment(), "owner_id", Owners.self().owner_id);
                    App.closeMenu();
                }
            });
        }
        if (getItemViewType(position) == MENU_ITEM) {
            String menu_string_name = menu.get(position - 1);
            ((MenuItemHolder) holder1).setData(menu_string_name, position, selected,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Integer position1 = (Integer) view.getTag();
                            selected = position1;
                            select(menu.get(position1 - 1));
                            notifyDataSetChanged();
                        }
                    });
        }

    }


    public void select(String name) {
        if (name == null)
            return;
        switch (name) {
            case MenuItemHolder.messages:
                App.startFragment(new ChatsFragment());
                break;
            case MenuItemHolder.invites:
                App.startFragment(new InvitesFragment());
                break;
            case MenuItemHolder.settings:
                App.startFragment(new SettingsFragment());
                break;
            case MenuItemHolder.map:
                App.startFragment(new EventMapFragment());
                break;
            case MenuItemHolder.contacts:
                App.startFragment(new ContactsFragment());
                break;
            case MenuItemHolder.likes:
                App.startFragment(new LikesFragment());
                break;
        }
    }

}
