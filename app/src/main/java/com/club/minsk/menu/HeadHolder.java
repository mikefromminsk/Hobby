package com.club.minsk.menu;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.utils.BlurBitmap;
import com.club.minsk.db.Links;
import com.club.minsk.utils.FadeInNetworkImageView;

public class HeadHolder extends RecyclerView.ViewHolder {

    View view;
    RelativeLayout owner_profile_view;
    ImageView nav_header_blur;
    FadeInNetworkImageView nav_owner_image;
    TextView nav_owner_name;

    boolean profile_photo_is_loaded = false;

    public HeadHolder(View view) {
        super(view);
        this.view = view;

        owner_profile_view = (RelativeLayout) view.findViewById(R.id.nav_header);
        nav_header_blur = (ImageView) view.findViewById(R.id.nav_header_blur);
        nav_owner_image = (FadeInNetworkImageView) view.findViewById(R.id.nav_owner_image);
        nav_owner_name = (TextView) view.findViewById(R.id.nav_owner_name);
    }

    void setData(View.OnClickListener clickListener) {

        if (Owners.self() != null && !profile_photo_is_loaded) {

            nav_owner_name.setText(Owners.self().owner_name);
            nav_owner_image.setImageUrl(Links.get(Owners.self().owner_avatar_link_id), App.getImageLoader());
            App.getImageLoader().get(Links.get(Owners.self().owner_photo_link_id), new ImageLoader.ImageListener() {

                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        profile_photo_is_loaded = true;
                        float cropHeight = response.getBitmap().getHeight() / (1.5f);
                        Bitmap croppedBitmap = Bitmap.createBitmap(response.getBitmap(), 0, 0, response.getBitmap().getWidth(), (int) cropHeight);
                        Bitmap bitmap = BlurBitmap.blurRenderScript(App.getActiveActivity(), croppedBitmap, 25);
                        nav_header_blur.setImageBitmap(bitmap);
                    }
                }

                public void onErrorResponse(VolleyError arg0) {
                }
            });
        }


        owner_profile_view.setOnClickListener(clickListener);
    }
}
