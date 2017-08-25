package com.aalife.android;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by fengxl on 2015/5/27.
 */
public class MyDatePickerDialog extends DatePickerDialog {
    private static final String[] WEEK_ARRAY = new String[]{ "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        setButton(BUTTON_POSITIVE, context.getText(android.R.string.ok), this);
        setTitle(getDateString(year, monthOfYear, dayOfMonth));
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        setTitle(getDateString(year, month, day));
    }

    private String getDateString(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return year + "年" + (month + 1) + "月" + day + "日 " + WEEK_ARRAY[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

}
