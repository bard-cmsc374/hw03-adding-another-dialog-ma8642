package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by cs374 on 10/24/16.
 */
public class DatePickerFragment extends DialogFragment {  //class for the datepicker dialog that will appear when we want to change the date of a crime

    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "time";  //we are adding a time variable

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    public static DatePickerFragment newInstance(Date date, long time) {  //putting the date as parameter in DatePickerFragments argument bundle allows us to access the data later
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_TIME, time); //adds a time as another value to the bundle args.

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        long time = (long) getArguments().getSerializable(ARG_TIME);

        //adding year, month day variables ensures that the calendar date-picker widget correctly displays the current date of the crime
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //adding TimePicker
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        //int sec = calendar.get(Calendar.SECOND);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null); //retrieves the date-picker view object

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_date_time_picker);

        if ( android.os.Build.VERSION.SDK_INT >= 23 ) //backward compatibility
        {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(min);
        }
        else
        {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(min);
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v) //sets view to dialog_date
                .setTitle(R.string.date_picker_title)
                //.setPositiveButton(android.R.string.ok, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                int hour = mTimePicker.getHour();  //to set hours and minutes we add this
                                int minute = mTimePicker.getMinute();
                                Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                                sendResult(Activity.RESULT_OK, date, date.getTime());
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date, long time) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
