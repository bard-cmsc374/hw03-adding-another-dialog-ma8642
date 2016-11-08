package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

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
//        Log.d(TAG, "About to make ACTION_CALL intent");
//        final Intent callContact = new Intent(Intent.ACTION_CALL,
//                ContactsContract.Contacts.CONTENT_URI);
        mCallButton = (Button) v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.d(TAG, "1.  clicking button..");
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.READ_CONTACTS)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Should we show an explanation?
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.READ_CONTACTS)) {
//
//                        // Show an expanation to the user *asynchronously* -- don't block
//                        // this thread waiting for the user's response! After the user
//                        // sees the explanation, try again to request the permission.
//                        Log.d(TAG, "2.  Showing explanation");
//
//                    } else {

                        // No explanation needed, we can request the permission.
                        Log.d(TAG, "2.  calling requestPermissions");

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_NUMBER);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    //}
                //}
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
        Log.d(TAG, "6.  called onActivityResult");
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
                    ContactsContract.Contacts._ID,
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
                mCrime.setSuspectId(c.getString(0));
                Log.d(TAG, "got Suspect id: " + c.getString(0));
                String suspect = c.getString(1);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_NUMBER && data != null) {  //MODIFY THIS TO GET ID OF SUSPECT AND THEN WE WILL USE THAT TO GET THEIR NUMBER
            //uriContact = data.getData();

            //getSuspectNumber();
        }
    }

    //Developer's version
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "3.  called onRequestPermissionResult");
        switch (requestCode) {
            case REQUEST_NUMBER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "4.  Gained access to contacts!");
                    //getSuspectNumber();  //CALL THIS HERE IF IT WORKS
                    Log.d(TAG, "5.  get phone number");

                    String number = getSuspectNumber();


//                    final Intent callContact = new Intent(Intent.ACTION_CALL,
//                            ContactsContract.Contacts.CONTENT_URI);

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "4.  Cannot get access to contacts");

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

    private String getSuspectNumber() {
        String contactNumber = null;
        //getting contacts ID
        Uri uri = ContactsContract.Data.CONTENT_URI;
        Log.d(TAG, "URI is not null");
//
//        String[] idList = new String[]{ContactsContract.Contacts._ID};
//        Log.d(TAG, "idList is not null");
//
//        Cursor cursorID = getActivity().getContentResolver().query(uri, idList, null, null, null);
//
//        if (cursorID.moveToFirst()) {
//            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
//        }
//
//        cursorID.close();
        Log.d(TAG, "Contact ID is: " + mCrime.getSuspectID());

        //Now we use ID to get contact phone number


        //SVEN
        String where =
                ContactsContract.Data.CONTACT_ID + " = " + mCrime.getSuspectID() +
                        " AND " + ContactsContract.Data.MIMETYPE + " = '" +
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
        Log.d(TAG, where);

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor c = getActivity()
                .getContentResolver()
                .query(ContactsContract.Data.CONTENT_URI, projection, where, null,
                        null);

        try {
            //Double-check that you atually got results
            if (c.getCount() == 0) {
                //Log.d(TAG, c.toString());
            }

            //Pull out the PhoneNumber column of the first row of data -
            //that is your suspect's number.
            c.moveToFirst();
            contactNumber = c.getString(0);
        } finally {
            c.close();
        }

        Log.d(TAG, "Contact Phone Number: " + contactNumber);

        return contactNumber;
    }

    public void toast(String msg)
    {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
