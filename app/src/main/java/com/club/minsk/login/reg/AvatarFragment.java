package com.club.minsk.login.reg;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.club.minsk.utils.CircleImageView;
import com.club.minsk.utils.ImagePikerFragment;

public class AvatarFragment extends ImagePikerFragment {

    CircleImageView avatar_image;
    ImageView avatar_upload_success;
    RelativeLayout upload_layout;
    TextView upload_text;
    ProgressBar upload_avatar_progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);

        upload_layout = (RelativeLayout) view.findViewById(R.id.upload_layout);
        upload_text = (TextView) view.findViewById(R.id.upload_text);
        avatar_image = (CircleImageView) view.findViewById(R.id.avatar_image);
        avatar_upload_success = (ImageView) view.findViewById(R.id.avatar_upload_success);
        upload_avatar_progress = (ProgressBar) view.findViewById(R.id.upload_avatar_progress);

        upload_text.setText(Strings.get(R.string.avatar_text));
        upload_avatar_progress.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openPickImageDialog();
            }
        }, 1500);

        upload_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploading_bitmap == null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openPickImageDialog();
                        }
                    }, 500);
                }

            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }

    Bitmap uploading_bitmap = null;
    @Override
    public void pickImage(String bitmap) {

        uploading_bitmap = convertBitmap(bitmap, 800, 70);

        avatar_image.setImageBitmap(uploading_bitmap);

        if (uploading_bitmap != null) {
            upload_avatar_progress.setVisibility(View.VISIBLE);
            new ImageUpload(uploading_bitmap, new ImageUpload.Listener() {
                @Override
                public void run(ImageUpload.Response response) {
                    upload_avatar_progress.setVisibility(View.GONE);
                    App.owner.photo = Links.get(response.link_id);
                    avatar_upload_success.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            App.nextRegistrationFragment();
                        }
                    }, 1000);
                }
            }, new ApiRequest.ErrorListener() {
                @Override
                public void run(ApiRequest.Error error) {
                    App.user_show(R.string.image_error);
                    uploading_bitmap = null;
                }
            });
        }
        else
            App.user_show(R.string.image_error);
    }

}
