package com.example.eric.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.example.eric.Core.InfSetting;
import com.example.eric.Core.MainActivity;
import com.example.eric.R;

import java.util.Calendar;

/**
 * Created by Eric_ on 2018/7/20.
 */

public class MeasureRecord extends AppCompatActivity {
    ImageButton ImaBtnBack;
    NumberPicker dateYear;
    NumberPicker dateMonth;
    NumberPicker dateDay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_measure);
        ImaBtnBack = findViewById(R.id.measure_imaBtm_back);
        dateYear = findViewById(R.id.insertDateYear);
        dateMonth = findViewById(R.id.insertDateMonth);
        dateDay = findViewById(R.id.insertDateDay);
        dateYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dateMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dateDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        date();
        ImaBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                backIntent.setClass(MeasureRecord.this,MainActivity.class);
                //把返回数据存入Intent
                MeasureRecord.this.setResult(RESULT_OK, backIntent);
                finish();
            }
        });
    }
    void date() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        dateYear.setMinValue(year - 1);
        dateYear.setMaxValue(year);
        dateYear.setValue(year);
        dateMonth.setMinValue(1);
        dateMonth.setMaxValue(12);
        dateMonth.setValue(month + 1);
        dateDay.setMaxValue(31);
        dateDay.setMinValue(1);
        dateDay.setValue(day);
        dateYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                update(newVal, dateMonth.getValue());
            }
        });
        dateMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                update(dateYear.getValue(), newVal);
            }
        });
    }
    void update(int year, int month) {
        int february;
        if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) {
            february = 29;
        } else {
            february = 28;
        }
        int days[] = {31, february, 31, 30, 31, 30, 31, 31, 30, 31, 30 ,31};
        dateDay.setMaxValue(days[month - 1]);
    }
}
