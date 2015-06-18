package ru.obsession.iremote;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by spirindmitrij on 30-Jan-15.
 */
public class BaseFragment extends Fragment {

    protected ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
