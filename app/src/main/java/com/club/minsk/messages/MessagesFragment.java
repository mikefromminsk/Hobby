package com.club.minsk.messages;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.club.minsk.R;
import com.club.minsk.chats.ChatHolder;
import com.club.minsk.db.Events;
import com.club.minsk.db.Members;
import com.club.minsk.db.Messages;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.MembersTable;
import com.club.minsk.db.tables.MessagesTable;
import com.club.minsk.db.tables.PushTable;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.App;
import com.club.minsk.invite.InviteFragment;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.Format;

import java.util.Date;
import java.util.Map;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class MessagesFragment extends AppFragment implements
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    RecyclerView messages_list;
    MessagesAdapter messagesAdapter;
    EmojiconEditText message_text;
    ImageView send_message;
    ImageView messages_smile_add;
    RelativeLayout message_layout;
    FrameLayout messages_smiles_list;

    public Long attach_type;
    public Long attach_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messages, container, false);

        Bundle args = getArguments();
        if (args != null) {
            attach_type = args.getLong("attach_type");
            attach_id = args.getLong("attach_id");
        }

        messages_list = (RecyclerView) view.findViewById(R.id.messages_list);
        message_text = (EmojiconEditText) view.findViewById(R.id.message_text);
        send_message = (ImageView) view.findViewById(R.id.send_message);
        messages_smile_add = (ImageView) view.findViewById(R.id.messages_smile_add);
        message_layout = (RelativeLayout) view.findViewById(R.id.message_layout);
        messages_smiles_list = (FrameLayout) view.findViewById(R.id.emojicons);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        messages_list.setLayoutManager(linearLayoutManager);

        new Messages().selectChatMessages(attach_type, attach_id, new Messages.ListListener() {
            @Override
            public void run(Messages.MessageList response) {
                setTitle(ChatHolder.getOwnersCollageUrls(response.members),
                        attach_type == Members.ATTACH_TYPE_INVITE ?
                                Format.dateFormat(Events.get(attach_id).event_time) + " " + Events.get(attach_id).event_address:
                                Format.chatName(response.members),
                        attach_type == Members.ATTACH_TYPE_DIALOG ?
                                Format.onlineFormat(Owners.get(attach_id).owner_login_time):
                                response.members.size() + " " + Strings.get(R.string.member) ,
                        null,
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (attach_type.equals(MembersTable.ATTACH_TYPE_DIALOG))
                                    App.openFragment(new OwnerFragment(), "owner_id", attach_id);
                                if (attach_type.equals(MembersTable.ATTACH_TYPE_INVITE))
                                    App.openFragment(new InviteFragment(), "event_id", attach_id);
                            }
                        });

                messagesAdapter = new MessagesAdapter(response.message_id_list);
                messages_list.setAdapter(messagesAdapter);
                messages_list.post(new Runnable() {
                    @Override
                    public void run() {
                        messages_list.smoothScrollToPosition(messagesAdapter.getItemCount());
                    }
                });
            }
        });

        messages_smile_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messages_smiles_list.getVisibility() == View.INVISIBLE) {
                    setEmojiconFragment(false);
                    messages_smiles_list.setVisibility(View.VISIBLE);
                    hideSmilesBar();
                } else {
                    messages_smiles_list.setVisibility(View.INVISIBLE);
                }
            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages_smiles_list.setVisibility(View.INVISIBLE);
                sendMessage(message_text.getText().toString());
            }
        });

        message_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (messagesAdapter != null) {
                                messages_list.smoothScrollToPosition(messagesAdapter.getItemCount());
                            }
                        }
                    }, 300);
                }
            }
        });
        return view;
    }


    public void sendMessage(String message_text_str) {
        if (attach_type == null || attach_id == null)
            return;
        new Messages().insertDialogMessage(attach_type, attach_id,
                message_text_str,
                new Messages.Listener() {
                    @Override
                    public void run(Messages.Response response) {
                        messagesAdapter.addMessage(Messages.get(response.message_id));
                        messages_list.smoothScrollToPosition(messagesAdapter.getItemCount());
                    }
                }, new ApiRequest.ErrorListener() {
                    @Override
                    public void run(ApiRequest.Error error) {
                        App.user_show(R.string.message_send_error);
                    }
                });
        message_text.setText("");
        messages_smiles_list.setVisibility(View.INVISIBLE);
    }



    public void insertMessage(Map<String, String> data) {
        MessagesTable.Message message = Messages.getInstance().newMessageInstance();
        message.owner_id = AndroidUtils.toLong(data.get("owner_id"));
        message.message_id = AndroidUtils.toLong(data.get("message_id"));
        message.attach_type = AndroidUtils.toLong(data.get("attach_type"));
        message.attach_id = AndroidUtils.toLong(data.get("attach_id"));
        message.message_text = data.get(PushTable.PUSH_TEXT);
        message.message_send_time = AndroidUtils.toLong(data.get("message_send_time"));
        messagesAdapter.addMessage(message);
        messages_list.smoothScrollToPosition(messagesAdapter.getItemCount());
        new Messages().chatRead(message.attach_type, message.attach_id);
    }

    public void readMessage(Map<String, String> data) {
        try {
            if (attach_type.equals(AndroidUtils.toLong(data.get("attach_type")))
                    && attach_id.equals(AndroidUtils.toLong(data.get("attach_id"))))
                messagesAdapter.setReadMessages();
            messages_list.smoothScrollToPosition(messagesAdapter.getItemCount());
        } catch (NumberFormatException ignored) {
        }
    }

    long closeSmilesBarTime = 0;

    private void hideSmilesBar() {
        closeSmilesBarTime = new Date().getTime() + 2900;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new Date().getTime() - closeSmilesBarTime > 0) {
                    messages_smiles_list.setVisibility(View.INVISIBLE);
                }
            }
        }, 3000);
    }


    private void setEmojiconFragment(boolean useSystemDefault) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(message_text, emojicon);
        hideSmilesBar();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(message_text);
    }


    @Override
    public void onBack() {
        if (messagesAdapter != null)
            messages_list.setAdapter(messagesAdapter);
    }
}
