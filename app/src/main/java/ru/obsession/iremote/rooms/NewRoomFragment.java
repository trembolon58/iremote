package ru.obsession.iremote.rooms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import ru.obsession.iremote.BaseFragment;
import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Room;

public class NewRoomFragment extends BaseFragment implements ViewSwitcher.ViewFactory,
        GestureDetector.OnGestureListener, View.OnTouchListener, OnAdapterImageChanged {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private ImageSwitcher imgswitcher;
    private RecyclerView recyclerView;
    private EditText editText;
    private Room room;
    
    
    private GestureDetector gd;

    private int position = 0;

    private int[] mImageIds = {
            R.drawable.bathroom,
            R.drawable.bed,
            R.drawable.child,
            R.drawable.garage,
            R.drawable.kitchen,
            R.drawable.living,
            R.drawable.sport,
            R.drawable.studio,
            R.drawable.work_study
    };

    private int[] mImageIdsPrev = {
            R.drawable.bathroom_prev,
            R.drawable.bed_prev,
            R.drawable.child_prev,
            R.drawable.garage_prev,
            R.drawable.kitchen_prev,
            R.drawable.living_prev,
            R.drawable.sport_prev,
            R.drawable.studio_prev,
            R.drawable.work_study_prev
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.room_type);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));
        Bundle bundle = getArguments();
        if (bundle != null) {
            room = (Room) bundle.getSerializable(EditRoomFragment.ROOM_PARAM);
        }
        View root = inflater.inflate(R.layout.fragment_create_room, container, false);
        editText = (EditText) root.findViewById(R.id.editText);
        if (room != null) {
            editText.setText(room.name);
        }
        imgswitcher = (ImageSwitcher) root.findViewById(R.id.imageSwitcher);
        initImageSwitcher();
        recyclerView = (RecyclerView) root.findViewById(R.id.listView);
        initRecyclerView();
        return root;
    }

    private void initImageSwitcher() {
        imgswitcher.setFactory(this);
    /*    Animation inAnim = new TranslateAnimation(0, 1);
        inAnim.setDuration(100);
        Animation outAnim = new AlphaAnimation(1, 0);
        outAnim.setDuration(100);

        imgswitcher.setInAnimation(inAnim);
        imgswitcher.setOutAnimation(outAnim);*/
        int i = 0;
        if (room != null) {
            for ( ; i < mImageIds.length; ++i) {
                if (room.iconRes == mImageIds[i]) {
                    break;
                }
            }
        }
        if (i == mImageIds.length) {
            i = 0;
        }
        imgswitcher.setImageResource(mImageIds[i]);
        imgswitcher.setOnTouchListener(this);
        gd = new GestureDetector(getActivity(), this);
    }

    public void setPositionNext() {
        position++;
        if (position > mImageIds.length - 1) {
            position = 0;
        }
        imgswitcher.setImageResource(mImageIds[position]);
    }

    public void setPositionPrev() {
        position--;
        if (position < 0) {
            position = mImageIds.length - 1;
        }
        imgswitcher.setImageResource(mImageIds[position]);
    }

    private void initRecyclerView() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AdapterRoomImages(mImageIds, mImageIdsPrev, imgswitcher, this));
    }


    @Override
    public void OnChanged(int position) {
        this.position = position;
    }

    @Override
    public View makeView() {
        ImageView imgview = new ImageView(getActivity());
        imgview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgview.setAdjustViewBounds(true);
        imgview.setLayoutParams(new ImageSwitcher.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        imgview.setBackgroundColor(0xFF000000);
        return imgview;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // справа налево
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                setPositionNext();
                imgswitcher.setImageResource(mImageIds[position]);
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // слева направо
                setPositionPrev();
                imgswitcher.setImageResource(mImageIds[position]);
            }
        } catch (Exception e) {
            // nothing
            return true;
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.done, menu);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (editText.getText().length() == 0) {
                    editText.setError(getString(R.string.enter_name));
                    return true;
                }
                hideKeyboard();
                FragmentManager manager = getFragmentManager();
                DatabaseApi api = DatabaseApi.getInstance(getActivity());
                if (room == null) {
                    room = new Room();
                    room.name = editText.getText().toString();
                    room.iconRes = mImageIds[position];
                    api.addRoom(room);
                    manager.popBackStack();
                    startEditFragment(room);
                } else {
                    room.name = editText.getText().toString();
                    room.iconRes = mImageIds[position];
                    api.updateRoom(room);
                    manager.popBackStack();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startEditFragment(Room room) {
        Fragment fragment = new EditRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EditRoomFragment.ROOM_PARAM, room);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(null).commit();
    }
    @Override
    public void onLongPress(MotionEvent arg0) {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }
}
