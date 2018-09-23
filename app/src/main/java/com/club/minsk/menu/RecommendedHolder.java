package com.club.minsk.menu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Devices;
import com.club.minsk.db.Strings;

public class RecommendedHolder extends RecyclerView.ViewHolder {

    private final TextView menu_rec_caption;
    private final TextView menu_rec_title;
    private final NetworkImageView menu_rec_logo;
    private final RelativeLayout menu_rec_layout;

    View view;

    public RecommendedHolder(View view) {
        super(view);
        this.view = view;

        menu_rec_caption = (TextView) view.findViewById(R.id.menu_rec_caption);
        menu_rec_title = (TextView) view.findViewById(R.id.menu_rec_title);
        menu_rec_logo = (NetworkImageView) view.findViewById(R.id.menu_rec_logo);
        menu_rec_layout = (RelativeLayout) view.findViewById(R.id.menu_rec_layout);
    }
    Devices.Recommended recommended;
    public void setData(Devices.Recommended recommended1, int position_in_recommended){
        this.recommended = recommended1;
        menu_rec_caption.setText(Strings.get(R.string.recommended));
        if (position_in_recommended == 0)
            menu_rec_layout.setVisibility(View.VISIBLE);
        else
            menu_rec_layout.setVisibility(View.GONE);
        menu_rec_title.setText(recommended.title);
        menu_rec_logo.setImageUrl(recommended.logo, App.getImageLoader());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getActiveActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recommended.link)));
            }
        });
    }
}
