package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MarleyAlford on 10/8/16.
 */
public class CrimeLab {  //this is a singleton class (it only allows one instance of itself to be created
    private static final String TAG = "cl_test";
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;
    //to create an SQLite database for our crime data
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        mCrimes = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {  //we no longer need these randomly generated crimes
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setSolved(i % 2 == 0);
//            mCrimes.add(crime);
//        }
    }

    public void addCrime(Crime c) {  //allows you to add a new crime to the list
        mCrimes.add(c);
        Log.d(TAG, "Adding crime to mCrimes");
        Log.d(TAG, "There are " + mCrimes.size() + " crimes in list.");
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
