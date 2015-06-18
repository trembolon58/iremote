package ru.obsession.iremote.rooms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;

import ru.obsession.iremote.R;

public class RoomsListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rooms_list, container, false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_rooms);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.listView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AdapterRooms(getActivity(), getFragmentManager()));
        ButtonFloat button = (ButtonFloat) root.findViewById(R.id.bAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewRoomFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
        return root;
    }

}
