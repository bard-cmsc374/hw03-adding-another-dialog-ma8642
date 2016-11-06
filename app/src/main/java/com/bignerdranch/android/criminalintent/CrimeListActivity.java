package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by MarleyAlford on 10/13/16.
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
        protected Fragment createFragment () {
        return new CrimeListFragment();
    }

        @Override
        protected int getLayoutResId () {
        //return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;  //using resource ID in place of R.layout.activity_fragment
    }

    @Override
    public void onCrimeSelected(Crime crime) { //if using a phone this will start a new CrimePagerActivity.  If using a tablet put a CrimeFragment in detail_fragment_container
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    public void onCrimeUpdated(Crime crime) {  //implements CrimeFragment.Callbacks to reload the list when in tablet interface
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
