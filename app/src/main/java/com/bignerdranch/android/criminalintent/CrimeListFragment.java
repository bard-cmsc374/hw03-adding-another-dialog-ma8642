package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MarleyAlford on 10/13/16.
 *
 * I'm stuck because this is only displaying one crime for some reason.  There should be a whole long list
 * but as soon as I implemented list_item_crime.xml as the new layout for this, it wasn't showing a nice list
 * anymore.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;  //a controller object that sits between the recycler view and the data set to be displayed

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        //implements the new recycle view format
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {  //sets up CrimeListFragment's user interface
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);  //create a crime adapter
        mCrimeRecyclerView.setAdapter(mAdapter); //set it on RecyclerView
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        //private Crime mCrime;
        public TextView mTitleTextView;
//        private TextView mTitleTextView;  //we add these so that we can reference our new layout file
//        private TextView mDateTextView;
//        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
//            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
//            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
//            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

//        public void bindCrime(Crime crime) {//CrimeHolder uses this to update the info to reflect state of Crime
//            mCrime = crime;
//            mTitleTextView.setText(mCrime.getTitle());
//            mDateTextView.setText(mCrime.getDate().toString());
//            mSolvedCheckBox.setChecked(mCrime.isSolved());
//        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) { //called when recycle view needs a new view to display an item
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false); //use the layout file that we created
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false); //create a single textview
            return new CrimeHolder(view);
        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) { //binds a viewholder's view to model object
            Crime crime = mCrimes.get(position);
            holder.mTitleTextView.setText(crime.getTitle());
            //holder.bindCrime(crime);
        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
