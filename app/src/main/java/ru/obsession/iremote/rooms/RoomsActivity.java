package ru.obsession.iremote.rooms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import java.io.Serializable;

import ru.obsession.iremote.R;

public class RoomsActivity extends ActionBarActivity {

    public static final String EDIT_ROOM = "edit_room";
    public static final String SELECTED_ROOM = "edit_room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        Fragment fragment;
        if (getIntent().getBooleanExtra(EDIT_ROOM, false)) {
            Bundle bundle = new Bundle();
            Serializable room = getIntent().getSerializableExtra(EditRoomFragment.ROOM_PARAM);
            bundle.putSerializable(EditRoomFragment.ROOM_PARAM, room);
            fragment = new EditRoomFragment();
            fragment.setArguments(bundle);
        } else {
            fragment = new RoomsListFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
