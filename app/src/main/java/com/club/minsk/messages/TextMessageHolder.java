package com.club.minsk.messages;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Members;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.MessagesTable;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.EmojiReplacements;
import com.club.minsk.utils.FadeInNetworkImageView;
import com.club.minsk.utils.Format;
import com.club.minsk.db.Strings;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

public class TextMessageHolder extends RecyclerView.ViewHolder {

    public final View view;
    EmojiconTextView message_item_text;
    TextView message_item_about;
    RelativeLayout message_item_out_layout;
    FadeInNetworkImageView message_item_avatar;

    public TextMessageHolder(View view) {
        super(view);
        this.view = view;
        message_item_text = (EmojiconTextView) view.findViewById(R.id.message_item_text);
        message_item_about = (TextView) view.findViewById(R.id.message_item_about);
        message_item_out_layout = (RelativeLayout) view.findViewById(R.id.message_item_layout);
        message_item_avatar = (FadeInNetworkImageView) view.findViewById(R.id.message_item_avatar);
    }


    MessagesTable.Message message;
    Integer position;
    List<MessagesTable.Message> messages;

    private MessagesTable.Message self_next_message() {
        for (int i = messages.indexOf(message) + 1; i < messages.size(); i++)
            if (messages.get(i).owner_id.equals(Owners.self().owner_id))
                return messages.get(i);
        return null;
    }

    public void setData(List<MessagesTable.Message> messages1,
                        final Integer position1) {
        this.position = position1;
        this.messages = messages1;
        this.message = messages.get(position);
        if (message.owner_id.equals(Owners.self().owner_id) && message.message_read_time != null &&
                (self_next_message() == null || self_next_message().message_read_time == null)) {
            message_item_about.setText(Strings.get(R.string.message_readed));
            message_item_about.setVisibility(View.VISIBLE);
        } else
            message_item_about.setVisibility(View.GONE);

        message_item_text.setText(EmojiReplacements.replaceInText(AndroidUtils.base64Decode(message.message_text)) + " ");
        if (!message.owner_id.equals(Owners.self().owner_id) &&
                message.attach_type.equals(Members.ATTACH_TYPE_INVITE)) {
            message_item_avatar.setImageUrl(Links.get(Owners.get(message.owner_id).owner_avatar_link_id), App.getImageLoader());
            message_item_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.openFragment(new OwnerFragment(), "owner_id", message.owner_id);
                }
            });
        }

        if (Format.extractUrl(AndroidUtils.base64Decode(message.message_text)) != null)
            message_item_text.setTextColor(0xff2f6699);
        else
            message_item_text.setTextColor(0xff000000);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Format.extractUrl(AndroidUtils.base64Decode(message.message_text));
                if (url != null)
                    App.getActiveActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

    }

}
