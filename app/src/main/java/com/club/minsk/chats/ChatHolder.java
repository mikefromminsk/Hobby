package com.club.minsk.chats;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Events;
import com.club.minsk.db.Members;
import com.club.minsk.db.Messages;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.MembersTable;
import com.club.minsk.messages.MessagesFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.CircularNetworkImageView;
import com.club.minsk.db.Links;
import com.club.minsk.utils.EmojiReplacements;
import com.club.minsk.utils.Format;
import com.lopei.collageview.CollageView;

import java.util.ArrayList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

public class ChatHolder extends RecyclerView.ViewHolder {
    View view;

    CollageView chat_members_collage;
    RelativeLayout chat_layout;
    RelativeLayout chat_unread_messages_layout;
    TextView chat_title;
    EmojiconTextView chat_last_message_text;
    TextView chat_unread_messages_text;
    CircularNetworkImageView chat_last_message_owner_avatar;

    public ChatHolder(View view) {
        super(view);
        this.view = view;

        chat_members_collage = (CollageView) view.findViewById(R.id.chat_members_collage);
        chat_layout = (RelativeLayout) view.findViewById(R.id.chat_layout);
        chat_unread_messages_layout = (RelativeLayout) view.findViewById(R.id.chat_unread_messages_layout);
        chat_title = (TextView) view.findViewById(R.id.chat_title);
        chat_last_message_text = (EmojiconTextView) view.findViewById(R.id.chat_last_message_text);
        chat_unread_messages_text = (TextView) view.findViewById(R.id.chat_unread_messages_text);
        chat_last_message_owner_avatar = (CircularNetworkImageView) view.findViewById(R.id.chat_last_message_owner_avatar);

    }

    Messages.ChatsList.Chat chat;


    public static List<String> getOwnersCollageUrls(List<Long> owners) {
        List<String> urls = new ArrayList<>();
        for (Long owner_id : owners)
            if (!owner_id.equals(Owners.self().owner_id))
                if (owners.size() <= 2)
                    urls.add(Links.get(Owners.get(owner_id).owner_avatar_link_id));
                else if (urls.size() < 4)
                    urls.add(Links.get(Owners.get(owner_id).owner_photo_link_id));
        if (urls.size() == 0)
            urls.add(Links.get(Owners.self().owner_avatar_link_id));
        return urls;
    }

    void setData(Messages.ChatsList.Chat chat1) {
        chat = chat1;

        chat_members_collage.loadPhotos(getOwnersCollageUrls(chat.members));

        if (chat.unread_messages > 0) {
            chat_last_message_text.setText("+" + chat.unread_messages);
        } else {
            chat_unread_messages_layout.setVisibility(View.GONE);
        }
        if (chat.attach_type == Members.ATTACH_TYPE_INVITE)
            chat_title.setText(Format.dateFormat(Events.get(chat.attach_id).event_time) + " " + Events.get(chat.attach_id).event_address);
        else
            chat_title.setText(Format.chatName(chat.members));
        chat_title.setSelected(true);
        chat_last_message_owner_avatar.setImageUrl(Links.get(Owners.get(Messages.get(chat.message_id).owner_id).owner_avatar_link_id), App.getImageLoader());
        chat_last_message_text.setText(EmojiReplacements.replaceInText(AndroidUtils.base64Decode(Messages.get(chat.message_id).message_text)));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chat.attach_type.equals(MembersTable.ATTACH_TYPE_DIALOG))
                    App.openFragment(new MessagesFragment(), "attach_type", chat.attach_type,
                            "attach_id", chat.members.get(0).equals(Owners.self().owner_id) ? chat.members.get(1) : chat.members.get(0));
                else
                    App.openFragment(new MessagesFragment(), "attach_type", chat.attach_type, "attach_id", chat.attach_id);
            }
        });
    }
}
