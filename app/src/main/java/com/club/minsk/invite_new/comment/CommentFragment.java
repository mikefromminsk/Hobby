package com.club.minsk.invite_new.comment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Strings;
import com.club.minsk.db.utils.ApiRequest;
import com.club.minsk.db.utils.ImageUpload;
import com.club.minsk.invite_new.invites.EventInvitesFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.FadeInNetworkImageView;
import com.club.minsk.utils.ImagePikerFragment;

public class CommentFragment extends ImagePikerFragment {

    RelativeLayout new_invite_comment_add_photo_layout;
    TextView new_invite_comment_add_photo_text;
    EditText new_invite_comment_text;
    FadeInNetworkImageView new_invite_comment_event_photo;
    ImageView new_invite_comment_add_photo_image;
    ProgressBar new_invite_comment_add_photo_loader;
    FrameLayout new_invite_ready_layout;
    TextView new_invite_ready_text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_invite_comment, container, false);

        setTitle(R.string.set_comment_title);

        new_invite_comment_add_photo_layout = (RelativeLayout) view.findViewById(R.id.new_invite_comment_add_photo_layout);
        new_invite_comment_add_photo_text = (TextView) view.findViewById(R.id.new_invite_comment_add_photo_text);
        new_invite_comment_text = (EditText) view.findViewById(R.id.new_invite_comment_text);
        new_invite_comment_add_photo_image = (ImageView) view.findViewById(R.id.new_invite_comment_add_photo_image);
        new_invite_comment_event_photo = (FadeInNetworkImageView) view.findViewById(R.id.new_invite_comment_event_photo);
        new_invite_ready_layout = (FrameLayout) view.findViewById(R.id.event_invites_ready_layout);
        new_invite_ready_text = (TextView) view.findViewById(R.id.new_invite_ready_text);
        new_invite_comment_add_photo_loader = (ProgressBar) view.findViewById(R.id.new_invite_comment_add_photo_loader);

        new_invite_comment_add_photo_image.setColorFilter(App.app_color);
        new_invite_ready_layout.setBackgroundColor(App.app_color);
        new_invite_ready_text.setText(Strings.get(R.string.next));
        new_invite_comment_add_photo_text.setText(Strings.get(R.string.add_photo));
        new_invite_comment_add_photo_loader.setVisibility(View.GONE);
        new_invite_comment_text.setHint(Strings.get(R.string.comment));
        new_invite_comment_text.setText(App.event.event_title != null ? App.event.event_title : "");

        new_invite_comment_add_photo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickImageDialog();
            }
        });

        setImageView();
        AndroidUtils.showKeyboard(App.getActiveActivity());

        new_invite_comment_text.requestFocus();

        new_invite_ready_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.event.event_title = new_invite_comment_text.getText().toString();
                App.openFragment(new EventInvitesFragment());
            }
        });

        return view;
    }


    @Override
    public void onBack() {

    }

    Bitmap uploading_bitmap;

    @Override
    public void pickImage(String selectedBitmap) {
        uploading_bitmap = convertBitmap(selectedBitmap, 800, 70);
        setImageView();
        if (uploading_bitmap != null)
            new ImageUpload(uploading_bitmap, new ImageUpload.Listener() {
                @Override
                public void run(ImageUpload.Response response) {
                    App.event.event_image_link_id = response.link_id;
                    uploading_bitmap = null;
                    setImageView();
                }
            }, new ApiRequest.ErrorListener() {
                @Override
                public void run(ApiRequest.Error error) {
                    uploading_bitmap = null;
                    setImageView();
                    App.user_show(R.string.image_error);
                }
            });
        else
            App.user_show(R.string.image_error);
    }

    private void setImageView() {
        if (uploading_bitmap != null && App.event.event_image_link_id == null) {
            new_invite_comment_add_photo_image.setVisibility(View.GONE);
            new_invite_comment_add_photo_text.setVisibility(View.VISIBLE);
            new_invite_comment_add_photo_loader.setVisibility(View.VISIBLE);
            new_invite_comment_event_photo.setVisibility(View.GONE);
            new_invite_comment_add_photo_text.setText(Strings.get(R.string.loading));
        }

        if (uploading_bitmap == null && App.event.event_image_link_id == null) {
            new_invite_comment_add_photo_image.setVisibility(View.VISIBLE);
            new_invite_comment_add_photo_text.setVisibility(View.VISIBLE);
            new_invite_comment_add_photo_loader.setVisibility(View.GONE);
            new_invite_comment_event_photo.setVisibility(View.GONE);
        }
        if (uploading_bitmap == null && App.event.event_image_link_id != null) {
            new_invite_comment_add_photo_image.setVisibility(View.GONE);
            new_invite_comment_add_photo_text.setVisibility(View.GONE);
            new_invite_comment_add_photo_loader.setVisibility(View.GONE);
            new_invite_comment_event_photo.setVisibility(View.VISIBLE);
            new_invite_comment_event_photo.setImageUrl(Links.get(App.event.event_image_link_id), App.getImageLoader());
        }
    }
}
