package com.example.trang.mp3online.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.trang.mp3online.view.fragment.fragmentalbum.HotFragment;
import com.example.trang.mp3online.view.fragment.fragmentalbum.ReleaseDateFragment;
import com.example.trang.mp3online.view.fragment.fragmentalbum.TotalPlayFragment;

/**
 * Created by Trang on 5/20/2017.
 */

public class ShowFragmentPager extends FragmentStatePagerAdapter {
    public ShowFragmentPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new TotalPlayFragment();
                break;
            case 1:
                fragment = new ReleaseDateFragment();
                break;
            case 2:
                fragment = new HotFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String titel = "";
        switch (position) {
            case 0:
                titel = "Nổi Bật";
                break;
            case 1:
                titel = "Nghe Nhiều";
                break;
            case 2:
                titel = "Mới";
                break;

        }

        return titel;
    }
}
