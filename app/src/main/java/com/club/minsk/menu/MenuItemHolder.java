package com.club.minsk.menu;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Messages;
import com.club.minsk.db.Strings;

public class MenuItemHolder extends RecyclerView.ViewHolder {

    public final static String invites = "invites";
    public final static String map = "map";
    public final static String contacts = "contacts";
    public final static String messages = "messages";
    public final static String settings = "settings";
    public final static String likes = "likes";

    View view;

    RelativeLayout menu_item_layout;
    RelativeLayout count_layout;
    ImageView menu_item_image;
    TextView menu_item_text;
    TextView menu_count;

    public MenuItemHolder(View view) {
        super(view);
        this.view = view;

        menu_item_layout = (RelativeLayout) view.findViewById(R.id.menu_item_layout);
        count_layout = (RelativeLayout) view.findViewById(R.id.count_layout);
        menu_item_image = (ImageView) view.findViewById(R.id.menu_item_image);
        menu_item_text = (TextView) view.findViewById(R.id.menu_item_text);
        menu_count = (TextView) view.findViewById(R.id.menu_count);
    }


    void setData(String menu_string_name, Integer position, Integer selected, View.OnClickListener onClickListener) {

        Resources resources = App.getActiveActivity().getResources();
        int menu_icon = resources.getIdentifier(Strings.STRING_MENU_PREFIX + menu_string_name, "drawable",
                App.getActiveActivity().getPackageName());

        if (menu_icon != 0) {
            menu_item_image.setImageDrawable(App.getActiveActivity().getResources().getDrawable(menu_icon));
        }
        menu_item_text.setText(Strings.get(Strings.STRING_MENU_PREFIX + menu_string_name));

        if (menu_string_name.equals(messages)) {
            int unread_chats = 0;
            if (App.chats != null)
                for (Messages.ChatsList.Chat chat : App.chats)
                    if (chat.unread_messages != null && chat.unread_messages > 0)
                        unread_chats++;
            if (unread_chats > 0){
                count_layout.setVisibility(View.VISIBLE);
                menu_count.setText("" + unread_chats);
            }
            else
                count_layout.setVisibility(View.GONE);
        } else {
            count_layout.setVisibility(View.GONE);
        }

        if (position.equals(selected)) {
            menu_item_text.setTextColor(Color.WHITE);
            menu_item_image.setColorFilter(Color.WHITE);
            menu_item_layout.setBackgroundColor(App.app_color);
        } else {
            menu_item_text.setTextColor(Color.BLACK);
            menu_item_image.setColorFilter(Color.BLACK);
            menu_item_layout.setBackgroundDrawable(App.getActiveActivity().getResources().getDrawable(R.drawable.layout_selector));
        }

        menu_item_layout.setTag(position);
        menu_item_layout.setOnClickListener(onClickListener);
    }


}
