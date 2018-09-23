package com.club.minsk.likes;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Links;
import com.club.minsk.db.Members;
import com.club.minsk.db.Owners;
import com.club.minsk.owner.OwnerFragment;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.Format;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private Owners.Owner mProfile;
    private Context mContext;

    public TinderCard(Context context, Owners.Owner profile) {
        mContext = context;
        mProfile = profile;
    }

    @Resolve
    private void onResolved() {
        Glide.with(mContext).load(Links.get(mProfile.owner_photo_link_id))
                .bitmapTransform(new RoundedCornersTransformation(mContext, AndroidUtils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP))
                .into(profileImageView);
        nameAgeTxt.setText(mProfile.owner_name + ", " + Format.bdateFormat(mProfile.owner_birthdate));
        locationNameTxt.setText(mProfile.owner_city);
    }

    @Click(R.id.profileImageView)
    private void onClick() {
        App.openFragment(new OwnerFragment(), "owner_id", mProfile.owner_id);
    }

    @SwipeOut
    private void onSwipedOut() {
        ((LikesFragment) App.getActiveFragment()).next();
    }

    @SwipeIn
    private void onSwipeIn() {
        if (Owners.get(mProfile.owner_id).is_like_request == 0)
            new Members().insert(Members.ATTACH_TYPE_LIKE_OWNER,
                    mProfile.owner_id, new Members.MemberInsertListener() {
                        @Override
                        public void run(Members.MemberInsertResponse response) {
                        }
                    });
        ((LikesFragment) App.getActiveFragment()).next();
    }

}
