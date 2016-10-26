package com.bignerdranch.android.criminalintent.database;

/**
 * Created by cs374 on 10/25/16.
 */
public class CrimeDbSchema {
    public static final class CrimeTable { //The CrimeTable class only exists to define the String constants needed to describe the moving pieces of the table definition.
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
