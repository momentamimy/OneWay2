package com.example.eltimmy.oneway2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class CustomViewAdapter extends FragmentPagerAdapter {


    public CustomViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        if (position==0)
        {
            fragment=new MapFragment();
        }
        if (position==1)
        {

            fragment=new SettingFragmet();
        }
return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
