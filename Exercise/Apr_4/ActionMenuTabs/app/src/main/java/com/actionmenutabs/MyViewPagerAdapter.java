package com.actionmenutabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
// Tab Captions private
    String tabCaption[] = new String[] { "TAB1", "TAB2", "TAB3" };
    public MyViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override public int getCount() {
        return tabCaption.length;
        // return 3 (numbers of tabs in the example)
     }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case 0: return new Fragment1();
            case 1: return new Fragment2();
            case 2: return new Fragment3();
        }

        return null;
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabCaption[position];
//         return tab caption
 }
}