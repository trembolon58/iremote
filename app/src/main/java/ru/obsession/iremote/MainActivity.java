package ru.obsession.iremote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.obsession.iremote.control.ControlFragment;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.drawer.NavigationDrawerFragment;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.Lirc;
import ru.obsession.iremote.models.Room;
import ru.obsession.iremote.rooms.RoomsActivity;
import ru.obsession.iremote.select_control.SelectControlActivity;
import ru.obsession.iremote.views.OnDrawerClickListener;


public class MainActivity extends ActionBarActivity implements OnDrawerClickListener {

    public static final String PREFERENCES_NAME = "iRemote_preferences";
    public static final String DEFAULT_ROOM = "default_room";
    public static final String DEFAULT_CONTROL = "default_control";
    private FrameLayout frameLayout;
    private Toolbar toolbar;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ControlFragment controlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout)findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        int id = preferences.getInt(MainActivity.DEFAULT_CONTROL, -1);
        if (id != -1) {
            Lirc control = DatabaseApi.getInstance(this).getControl(id);
            if (control != null) {
                startControlFragment(control);
            } else {
                initAddButton();
            }
        } else {
            initAddButton();
        }
        //recordStart(null);
    }

    private void initAddButton(){
        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)getResources().getDimension(R.dimen.joystick_height),
                (int)getResources().getDimension(R.dimen.joystick_height));
        params.gravity = Gravity.CENTER;
        params.setMargins(128,128,128,128);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.add_remote);
        frameLayout.addView(imageView);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClick(null);
            }
        });

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (controlFragment != null && controlFragment.isAdded() && !controlFragment.isDetached()) {
            controlFragment.onTouch(null, event);
        }
        return super.dispatchTouchEvent(event);
    }

    public static Room GetCurrentRoom(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        int roomId = preferences.getInt(MainActivity.DEFAULT_ROOM, -1);
        DatabaseApi api = DatabaseApi.getInstance(context);
        Room room = api.getRoom(roomId);
        if (room != null) {
            return room;
        }
        room = new Room();
        api.addRoom(room);
        preferences.edit().putInt(MainActivity.DEFAULT_ROOM, room.getId()).commit();
        return room;
    }

    public static void SetCurrentRoom(Context context, Room room) {
        SharedPreferences preferences = context
                .getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(MainActivity.DEFAULT_ROOM, room.getId()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationDrawerFragment.setAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Room room = (Room) data.getSerializableExtra(RoomsActivity.SELECTED_ROOM);
            Control control = (Control) data.getSerializableExtra(SelectControlActivity.CONTROL_EXTRA);
            if (room != null && control != null) {
                SetCurrentRoom(this, room);
                startControlFragment(control);
                mNavigationDrawerFragment.setAdapter();
            } else if (control != null) {
                DatabaseApi.getInstance(this).addControl(GetCurrentRoom(this), control);
                startControlFragment(control);
                mNavigationDrawerFragment.setAdapter();
            }
        }
    }

    @Override
    public void OnClick(Control control) {
        mNavigationDrawerFragment.mDrawerLayout.closeDrawers();
        if (control == null)
        {
            startActivityForResult(new Intent(this,SelectControlActivity.class), 1);
            return;
        }
        setDefaultControl(control);
        startControlFragment(control);
    }

    public void setDefaultControl(Control control) {
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(DEFAULT_CONTROL, control.getId()).commit();
    }

    private void startControlFragment(Lirc control) {
        frameLayout.removeAllViews();
        frameLayout.setOnClickListener(null);
        controlFragment = new ControlFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ControlFragment.CONTROL_EXTRA, control);
        controlFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, controlFragment, ControlFragment.CONTROL_FRAGMENT).commit();
    }
}
