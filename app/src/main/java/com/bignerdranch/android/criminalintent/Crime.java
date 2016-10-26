package com.bignerdranch.android.criminalintent;

//import java.util.Date;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;

import java.text.DateFormat;
import java.util.Date;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.UUID;

/**
 * Created by MarleyAlford on 9/19/16.
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;  //will hold info about suspect

    public Crime() {
        //Generate unique identifier
//        mId = UUID.randomUUID();
//        mDate = new Date();
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    //GETTER FOR mID
    public UUID getId() {
        return mId;
    }

    //GETTER AND SETTER FOR mTITLE
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String newTitle) {
        mTitle = newTitle;
    }

//    public String getDate() {  //fancy long date
//        //return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(mDate);  //with time
//        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(mDate);  //with just date
//    }

    public Date getDate() {
        return mDate;
    }

    public long getTime() {return mDate.getTime();}

    public void setTime(long time ) { mDate.setTime(time);}

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public void getSuspectID() {
        //Nothing yet
        //return ;
    }
}
