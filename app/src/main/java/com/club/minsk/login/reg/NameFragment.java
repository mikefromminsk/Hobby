package com.club.minsk.login.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.AndroidUtils;
import com.club.minsk.utils.AppFragment;

public class NameFragment extends AppFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name, container, false);


        final Button name_next_button = (Button) view.findViewById(R.id.name_next_button);
        final EditText name_edit = (EditText) view.findViewById(R.id.name_edit);

        name_next_button.setText(Strings.get(R.string.next));

        name_edit.setHint(Strings.get(R.string.set_name).toUpperCase());
        name_edit.requestFocus();
        AndroidUtils.showKeyboard(App.getActiveActivity());

        name_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name_next_button.setEnabled(name_edit.getText().length() >= 2);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        name_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] names = name_edit.getText().toString().split(" ");
                App.owner.first_name = names[0];
                if (names.length > 1)
                    App.owner.last_name = names[1];
                App.nextRegistrationFragment();
            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }
}
