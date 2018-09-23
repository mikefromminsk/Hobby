package com.club.minsk.messages;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.club.minsk.R;
import com.club.minsk.db.Strings;

public class SecurityMessageHolder extends RecyclerView.ViewHolder {
    View view;
    TextView textView29;
    public SecurityMessageHolder(View view) {
        super(view);
        this.view = view;
        textView29 = (TextView) view.findViewById(R.id.textView29);
    }

    void setData(){
        textView29.setText(Strings.get(R.string.messages_is_encripted));
    }

}
