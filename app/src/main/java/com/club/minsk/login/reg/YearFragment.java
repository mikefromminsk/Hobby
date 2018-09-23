package com.club.minsk.login.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.club.minsk.App;
import com.club.minsk.R;
import com.club.minsk.db.Strings;
import com.club.minsk.utils.AppFragment;

import java.util.Calendar;
import java.util.Date;

public class YearFragment extends AppFragment {

    int min_year = 18;
    int max_year = 40;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);

        final Button year_next_button = (Button) view.findViewById(R.id.year_next_button);
        final SeekBar year_seek = (SeekBar) view.findViewById(R.id.year_seek);
        final TextView year_count = (TextView) view.findViewById(R.id.year_count);
        TextView year_text = (TextView) view.findViewById(R.id.year_text);


        year_text.setText(Strings.get(R.string.year_text));
        year_next_button.setText(Strings.get(R.string.next));
        year_seek.setMax(max_year - min_year);
        year_count.setText(max_year + "+");

        year_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int year = i + min_year;
                year_next_button.setEnabled(year != max_year);
                year_count.setText("" + year + (year == max_year ? "+" : ""));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        year_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = year_seek.getProgress() + min_year;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, year);
                App.owner.bdate = calendar.getTime().getTime() / 1000;
                App.nextRegistrationFragment();
            }
        });

        return view;
    }

    @Override
    public void onBack() {

    }
}
