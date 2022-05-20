package com.droidoxy.easymoneyrewards.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.droidoxy.easymoneyrewards.fragments.HomeFragment;
import com.droidoxy.easymoneyrewards.fragments.RedeemFragment;
import com.droidoxy.easymoneyrewards.fragments.TransactionsFragment;

/**
 * Developed by DroidOXY
 */

public class TransactionPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[];
    private int NumbOfTabs;

    // Constructor
    public TransactionPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

            return new TransactionsFragment();
    }

    @Override
    public CharSequence getPageTitle(int position){ return Titles[position]; }

    @Override
    public int getCount(){ return NumbOfTabs; }

}