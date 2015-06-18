package ru.obsession.iremote.select_control;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.obsession.iremote.R;

public class SearchDevicesPagerAdapter extends FragmentStatePagerAdapter {

    private Resources res;
    private PageFragment first;
    private PageFragment second;

    public SearchDevicesPagerAdapter(FragmentManager fm, Resources res) {
        super(fm);
        this.res = res;
    }

    public PageFragment getFirst() {
        return first;
    }

    public PageFragment getSecond() {
        return second;
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment f = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PageFragment.POSITION_KEY, position);
        f.setArguments(bundle);
        if (position == 0){
            first = f;
        } else {
            second = f;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return res.getString(R.string.official);
        }
        return res.getString(R.string.users_controls);
    }
}
