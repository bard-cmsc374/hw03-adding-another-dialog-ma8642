package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by cs374 on 10/24/16.
 */
public class DatePickerFragment extends DialogFragment {  //class for the datepicker dialog that will appear when we want to change the date of a crime
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null); //retrieves the date-picker view object

        return new AlertDialog.Builder(getActivity())
                .setView(v) //sets view to dialog_date
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}
