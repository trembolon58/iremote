package ru.obsession.iremote.drawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;

import ru.obsession.iremote.MainActivity;
import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Room;

public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {
    private ActionBarDrawerToggle mDrawerToggle;

    public DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerRecyclerView;
    private View mFragmentContainerView;
    boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mDrawerRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mDrawerRecyclerView.setLayoutManager(mLayoutManager);
        setAdapter();
        ButtonRectangle button = (ButtonRectangle) view.findViewById(R.id.bAdd);
        button.setOnClickListener(this);
        return view;
    }

    public void setAdapter() {
        Room room = MainActivity.GetCurrentRoom(getActivity());
        mDrawerRecyclerView.setAdapter(new Adapter(getActivity(),
                room, (MainActivity) getActivity()));
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
      //  mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                ((MainActivity) getActivity()).getToolbar(),
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        ((MainActivity)getActivity()).OnClick(null);
    }
}
