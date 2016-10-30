package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by MarleyAlford on 9/19/16.
 */
public class CrimeFragment extends Fragment {

    private static final String TAG = "cf_test";

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_NUMBER = 2;
    private Uri uriContact;
    private String contactID;  //contact's unique ID
    Context context;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);  //getting ID from arguments now that it's a private method in CrimeActivity
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This one too
            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);  //wire up date button to it's string value
        updateDate();
        //mDateButton.setText(mCrime.getDate());  //for fancy button
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {  //to show the date-picker dialog
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button Pressed");
                FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = new DatePickerFragment().newInstance(mCrime.getDate(), mCrime.getTime()); //reference to the new constructor we created that passes date as a parameter
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE); //creates relationship between data of DatePickerFragment and CrimeFragment
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);  //implicit intent that gets sent to startActivity(Intent)
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));  //creates a chooser option so user can decide which app to use (if you have more than one messaging app)
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);  //test to see if button disable code works
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        //Not sure how to do this part, because it tries to call, but it doesn't have the info yet..
        final Intent callContact = new Intent(Intent.ACTION_CALL,
                ContactsContract.Contacts.CONTENT_URI);
        mCallButton = (Button) v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(callContact, REQUEST_NUMBER);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();  //if device has no contacts app, we need a way to respond to click
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);  //if there is no contacts app, button will be disabled
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //sets crime date to selected date
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //Specify which fields you want your query to return
            //values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Perform your query = the contactUri is like a "where"
            //clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                //Double-check that you atually got results
                if (c.getCount() == 0) {
                    return;
                }

                //Pull out the first column of the first row of data -
                //that is your suspect's name.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_NUMBER && data != null) {  //MODIFY THIS TO GET ID OF SUSPECT AND THEN WE WILL USE THAT TO GET THEIR NUMBER
            //uriContact = data.getData();
            getSuspectNumber();
        }
//            String contactNumber = null;
//            Uri numberUri = data.getData();
//            //getting contacts ID
//            Cursor cursorID = context.getContentResolver().query(numberUri, new String[]{ContactsContract.Contacts._ID}, null, null, null);
//
//            if (cursorID.moveToFirst()) {
//                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
//            }
//
//            cursorID.close();
//            Log.d(TAG, "Contact ID is: " + contactID);
//
//            //Now we use ID to get contact phone number
//
//            Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
//
//            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
//            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
//            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
//
//            new String[]{contactID},
//            null);
//
//            if (cursorPhone.moveToFirst()) {
//                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            }
//
//            cursorPhone.close();
//
//            Log.d(TAG, "Contact Phone Number: " + contactNumber);

//            String where = ContactsContract.Data.CONTACT_ID + " = " + mCrime.getSuspectID() +
//                            " AND " + ContactsContract.Data.MIMETYPE + " = '" +
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
//            String[] projection = new String[]{
//                    ContactsContract.CommonDataKinds.Phone.NUMBER
//            };
//            Cursor dataCursor = getActivity()
//                    .getContentResolver()
//                    .query(ContactsContract.Data.CONTENT_URI, projection, where, null,
//                            null);
//        }
        }
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() { //method that creates four strings and then pieces them together and returns a complete report.
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void getSuspectNumber() {
        String contactNumber = null;
        //getting contacts ID
        Cursor cursorID = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();
        Log.d(TAG, "Contact ID is: " + contactID);

        mCrime.setSuspectId(contactID);

        //Now we use ID to get contact phone number

        Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }
}
