package com.manit.amit.bloodbank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by LENOVO on 06-12-2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                RequestbloodFragment requestbloodFragment = new RequestbloodFragment();
                return requestbloodFragment;

            case 1:
                AcceptFragment acceptFragment = new AcceptFragment();
                return acceptFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch (position){
            case 0:
                return "REQUEST DONOR";

            case 1:
                return "ACCEPT REQUEST";

            default:
                return null;
        }

    }
}
