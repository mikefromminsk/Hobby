package com.club.minsk.likes;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.club.minsk.R;
import com.club.minsk.db.Members;
import com.club.minsk.db.Owners;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.Date;
import java.util.List;

public class LikesFragment extends AppFragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like_new, container, false);
        setTitle(R.string.menu_likes);

        mSwipeView = (SwipePlaceHolderView) view.findViewById(R.id.swipeView);
        mContext = getActivity();

        Point windowSize = AndroidUtils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - AndroidUtils.dpToPx(160))
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeMaxChangeAngle(2f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        view.findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.undoLastSwipe();
            }
        });

        next();

        return view;
    }

    Long page_offset = 0L;
    Long page_size = 20L;
    int view_count = 1;

    void addProfiles(List<Long> owners) {
        if (owners != null)
            for (Long owner_id : owners)
                if (!owner_id.equals(Owners.self().owner_id) && Owners.get(owner_id).is_like_request == 0) {
                    mSwipeView.addView(new TinderCard(mContext, Owners.get(owner_id)));
                    view_count++;
                }
    }

    void next() {
        view_count--;
        if (view_count < 5) {
            long diffYears = (Math.abs(Owners.self().owner_birthdate - new Date().getTime() / 1000) / (60 * 60 * 24 * 365));
            new Members().membersBeside(Owners.self().owner_sex.equals("M") ? "W" : "M",
                    diffYears - 5,
                    page_offset,
                    page_size,
                    new Members.ListListener() {
                        @Override
                        public void run(Members.ListResponse response) {
                            addProfiles(response.likes);
                            addProfiles(response.friends);
                            addProfiles(response.friends_of_friends);
                            addProfiles(response.beside);
                        }
                    });
            page_offset += page_size;
        }
    }

    @Override
    public void onBack() {

    }
}
