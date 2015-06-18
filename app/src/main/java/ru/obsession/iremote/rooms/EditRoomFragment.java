package ru.obsession.iremote.rooms;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;

import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.Room;
import ru.obsession.iremote.select_control.SelectControlActivity;

public class EditRoomFragment extends Fragment implements View.OnClickListener{

    public static final String ROOM_PARAM = "room_param";
    private RecyclerView recyclerView;
    private Drawable mActionBarBackgroundDrawable;
    private Room room;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_edit_room, container, false);
        mActionBarBackgroundDrawable = getResources().getDrawable(R.color.primary);
        mActionBarBackgroundDrawable.setAlpha(0);

        room = (Room) getArguments().getSerializable(ROOM_PARAM);
        getSupportActionBar().setTitle(room.name);
        recyclerView = (RecyclerView) root.findViewById(R.id.listView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new AdapterControls(getActivity(), room, this));
        getSupportActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int y;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                y += dy;
                final int headerHeight = (int) (getResources().getDimension(R.dimen.room_card_height) - getSupportActionBar().getHeight());
                final float ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
                final int newAlpha = (int) (ratio * 255);
                mActionBarBackgroundDrawable.setAlpha(newAlpha);
            }
        });

        ButtonFloat button = (ButtonFloat) root.findViewById(R.id.bAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), SelectControlActivity.class), 1);
            }
        });

        return root;
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    ActionBar getSupportActionBar() {
        return ((RoomsActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Control control = (Control) data.getSerializableExtra(SelectControlActivity.CONTROL_EXTRA);
            if (control != null) {
                DatabaseApi.getInstance(getActivity()).addControl(room, control);
                AdapterControls adapter = ((AdapterControls) recyclerView.getAdapter());
                adapter.controls.add(control);
                adapter.notifyItemInserted(1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new NewRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ROOM_PARAM, room);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(null).commit();
    }
}
