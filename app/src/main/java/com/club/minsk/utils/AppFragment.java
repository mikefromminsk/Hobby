package com.club.minsk.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.MainActivity;
import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.lopei.collageview.CollageView;

import java.util.List;

public abstract class AppFragment extends Fragment {

    public void hideTitle() {
        if (App.getActiveActivity() instanceof MainActivity && App.getActiveActivity().getSupportActionBar() != null) {
            Toolbar toolbar = (Toolbar) App.getActiveActivity().findViewById(R.id.toolbar);
            if (toolbar.getVisibility() == View.VISIBLE)
                toolbar.setVisibility(View.GONE);
        }
    }

    public void setTitle(final List<String> urls, final String title, final String subtitle,
                         final Integer right_image,
                         final View.OnClickListener rightClick, final View.OnClickListener titleClick) {
        if (App.getActiveActivity() instanceof MainActivity && App.getActiveActivity().getSupportActionBar() != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    ActionBar actionBar = App.getActiveActivity().getSupportActionBar();
                    assert actionBar != null;
                    actionBar.setDisplayShowCustomEnabled(true);
                    actionBar.setBackgroundDrawable(new ColorDrawable(App.app_color));
                    LayoutInflater inflator = (LayoutInflater) App.getActiveActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View toolbar_item = inflator.inflate(R.layout.title, null);
                    toolbar_item.setBackgroundColor(App.app_color);
                    CollageView title_collage = (CollageView) toolbar_item.findViewById(R.id.title_collage);
                    TextView toolbar_item_text = (TextView) toolbar_item.findViewById(R.id.title_text);
                    TextView title_subtitle_text = (TextView) toolbar_item.findViewById(R.id.title_subtitle_text);
                    ImageView toolbar_right_button = (ImageView) toolbar_item.findViewById(R.id.toolbar_right_button);
                    if (urls != null)
                        title_collage.loadPhotos(urls);
                    else
                        title_collage.setVisibility(View.GONE);
                    if (subtitle != null)
                        title_subtitle_text.setText(subtitle);
                    else
                        title_subtitle_text.setVisibility(View.GONE);
                    title_subtitle_text.setSelected(true);
                    if (rightClick != null) {
                        if (right_image == null)
                            toolbar_right_button.setImageResource(R.drawable.more);
                        else
                            toolbar_right_button.setImageResource(right_image);
                        toolbar_right_button.setOnClickListener(rightClick);
                        toolbar_right_button.setVisibility(View.VISIBLE);
                    } else
                        toolbar_right_button.setVisibility(View.GONE);
                    if (titleClick != null)
                        toolbar_item.setOnClickListener(titleClick);

                    toolbar_item_text.setText(title);
                    toolbar_item_text.setSelected(true);
                    actionBar.setCustomView(toolbar_item);
                }
            });


            Toolbar toolbar = (Toolbar) App.getActiveActivity().findViewById(R.id.toolbar);
            if (toolbar.getVisibility() == View.GONE)
                toolbar.setVisibility(View.VISIBLE);
        }
    }


    public void setTitle(String title, View.OnClickListener rightClick) {
        setTitle(null, title, null, null, rightClick, null);
    }

    public void setTitle(String title) {
        setTitle(title, null);
    }

    public void setTitle(int title_string_res) {
        setTitle(Strings.get(title_string_res));
    }


    public void setTitle(int title_string_res,
                         View.OnClickListener click) {
        setTitle(Strings.get(title_string_res), click);
    }

    public void setTitle(int title_string_res, int right_image,
                         View.OnClickListener click) {
        setTitle(null, Strings.get(title_string_res),null, right_image, click, null);
    }


    public abstract void onBack();
}
