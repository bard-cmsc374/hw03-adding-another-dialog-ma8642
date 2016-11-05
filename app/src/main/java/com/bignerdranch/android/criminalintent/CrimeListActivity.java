package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by MarleyAlford on 10/13/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        //return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;  //using resource ID in place of R.layout.activity_fragment
    }
}
