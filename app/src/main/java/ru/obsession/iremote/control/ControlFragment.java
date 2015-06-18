package ru.obsession.iremote.control;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import ru.obsession.iremote.BaseFragment;
import ru.obsession.iremote.MainActivity;
import ru.obsession.iremote.R;
import ru.obsession.iremote.create_control.CreateControlActivity;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;
import ru.obsession.iremote.views.ConditionDisplay;
import ru.obsession.iremote.views.Draggable;
import ru.obsession.iremote.views.DrawerButton;
import ru.obsession.iremote.views.TrashControl;
import ru.obsession.iremote.views.ViewFactory;

public class ControlFragment extends BaseFragment implements
        ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener, View.OnClickListener {

    public final static String CONTROL_EXTRA = "control_extra";
    public final static String BUTTONS_KEY = "buttons_key";
    public static final String CONTROL_FRAGMENT = "control_fragment";
    private DatabaseApi databaseApi;
    private RelativeLayout layout;
    private Control control;
    private boolean editable;
    private ArrayList<DrawerButton> views;
    private ArrayList<ControlButton> buttons;
    private TrashControl trash;
    private ConditionDisplay display;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        databaseApi = DatabaseApi.getInstance(getActivity());
        control = (Control) getArguments().getSerializable(CONTROL_EXTRA);
        buttons = databaseApi.getButtons(control.getId());
        toolbar.setTitle(control.getCompany());
        toolbar.setSubtitle(control.getName());
        if (!databaseApi.isCustomView(control)) {
            layout = (RelativeLayout) ViewFactory.Inflate(inflater, container, control);
            addTrashControl();
            layout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        } else {
            layout = (RelativeLayout) inflater.inflate(R.layout.relative_layout, container, false);
            addTrashControl();
            initView(layout, buttons);
        }
        control.parseFile(getActivity());
        for (ControlButton btn : buttons) {
            btn.setLirc(control);
        }
        return layout;
    }

    private void addTrashControl() {
        trash = (TrashControl) LayoutInflater.from(getActivity())
                .inflate(R.layout.button_trash, layout, false);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreButtonsDialog dialog = new MoreButtonsDialog();
                Bundle bundle = getArguments();
                bundle.putParcelableArrayList(BUTTONS_KEY, buttons);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), null);
            }
        });
        layout.addView(trash);
    }

    private void initView(RelativeLayout view, ArrayList<ControlButton> buttons) {
        DrawerButton v;
        views = new ArrayList<>();
        for (ControlButton button : buttons) {
            if (button.isVisible()) {
                v = ViewFactory.GetDraggable(getActivity(), button);
                views.add(v);
                view.addView(v);
                if (button.type == ControlButton.DISPLAY) {
                    display = (ConditionDisplay) v;
                } else {
                    v.setOnClickListener(this);
                }
            } else {
                //резервируем место
                views.add(null);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (editable) {
            inflater.inflate(R.menu.control_editable, menu);
        } else {
            inflater.inflate(R.menu.control, menu);
        }
    }

    private void invalidate() {
        layout.removeAllViews();
        addTrashControl();
        initView(layout, buttons);
    }

    private void toggleDraggable() {
        editable = !editable;
        for (int i = 0; i < layout.getChildCount(); ++i) {
            View v = layout.getChildAt(i);
            if (v instanceof Draggable) {
                ((Draggable) v).toggleDraggable();
            }
        }
        ((ActionBarActivity) getActivity()).invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(getActivity(), CreateControlActivity.class);
                intent.putExtra(Control.KEY, control);
                getActivity().startActivityForResult(intent, 1);
                return true;
            case R.id.action_move:
                toggleDraggable();
                return true;
            case R.id.action_save:
                toggleDraggable();
                saveControl();
                return true;
            case R.id.action_cancel:
                toggleDraggable();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveControl() {
        ControlButton button;
        RelativeLayout.LayoutParams params;
        for (int i = 0; i < buttons.size(); ++i) {
            button = buttons.get(i);
            if (views.get(i) != null) {
                params = (RelativeLayout.LayoutParams) views.get(i).getLayoutParams();
                button.y = params.topMargin;
                button.x = params.leftMargin;
            } else {
                button.y = 10;
                button.x = 10;
            }
        }
        databaseApi.saveControl(control, buttons);
        editable = false;
        invalidate();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onGlobalLayout() {
        View view = getView();
        if (view != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
        views = new ArrayList<>();
        iterateViews(layout, 0, 0);
        saveControl();
    }

    private void iterateViews(ViewGroup group, int x, int y) {
        View view;
        RelativeLayout.LayoutParams params;
        for (int i = 0; i < group.getChildCount(); ++i) {
            view = group.getChildAt(i);
            if (view instanceof DrawerButton) {
                DrawerButton v = (DrawerButton) view;
                params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                params.leftMargin = v.getLeft() + x;
                params.topMargin = v.getTop() + y;
                params.rightMargin = -1;
                params.bottomMargin = -1;

                for (int j = 0; j < 16; ++j) { //удаление всех относительных координат-
                    params.addRule(j, 0);
                }
                // жесткая эмуляция. в жизни все кнопки будут созданы по файлу
                v.addControlButton(control, buttons, views);
                v.setLayoutParams(params);
            } else if (view instanceof ViewGroup) {
                iterateViews((ViewGroup) view, view.getLeft(), view.getTop());
            }
        }
    }

    @Override
    public boolean onTouch(@Nullable View v, MotionEvent event) {
        if (editable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    actionMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp(event);
                    break;
            }
        }
        return true;
    }

    private void actionUp(MotionEvent event) {
        int size = trash.getHeight();
        if (editable &&
                event.getX() + size / 4 > trash.getLeft() && event.getX() < trash.getLeft() + size * 4
                && event.getY() + size / 4 > trash.getTop() && event.getY() < trash.getTop() + size * 4) {
            for (int i = 0; i < views.size(); ++i) {
                if (views.get(i) != null && views.get(i).isDrawed()) {
                    layout.removeView(views.get(i));
                    views.set(i, null); // нужно быть аккуратным, чтобы не образовалось смещение на месте невидимых вьюх нулл
                    buttons.get(i).visible = 0;
                }
            }
        }
        trash.setBackgroundColor(getResources().getColor(R.color.button_background));
    }

    private void actionMove(MotionEvent event) {
        int size = trash.getHeight();
        if (editable) {
            boolean selected = false;
            for (DrawerButton button : views) {
                if (button != null) {
                    if (button.isDrawed()) {
                        selected = true;
                        break;
                    }
                }
            }
            if (selected) {
                if (event.getX() + size / 4 > trash.getLeft() && event.getX() < trash.getLeft() + size * 4
                        && event.getY() + size / 4 > trash.getTop() && event.getY() < trash.getTop() + size * 4) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(trash);
                    trash.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    trash.setBackgroundColor(getResources().getColor(R.color.button_background));
                }
            }
        }
    }

    public void onButtonClick(ControlButton button, MoreButtonsDialog dialog, int positionInList) {
        if (editable) {
            button.setVisible(true);
            DrawerButton drawerButton = ViewFactory.GetDraggable(getActivity(), button);
            drawerButton.setDraggable(true);
            views.set(positionInList, drawerButton);
            layout.addView(drawerButton);
            dialog.dismiss();
        } else {
            toControl(button);
        }
    }

    private void toControl(ControlButton button) {
        if (button != null) {
            button.control();
        }
    }

    @Override
    public void onClick(View v) {
        DrawerButton button = (DrawerButton) v;
        if (control.getType() == Control.AIR_CONDITIONAL_CLEANER && display != null) {
            parseVisualise(button);
        }
        if (button.controlButton != null) {
            toControl(button.controlButton);
        }
    }

    private void findButton() {
        //TODO:
    }

    private void parseVisualise(DrawerButton button) {
        switch (button.controlButton.icon) {
            case R.drawable.fan:
                display.changeFan();
                return;
            case R.drawable.power:
                display.toggleEnabled();
                return;
            case R.drawable.mode:
                display.changeMode();
                return;
            case R.drawable.joustic2_temperature:
                display.upTemp();
                return;
            case R.drawable.vertical_wind:
                display.changeWind();
                return;
            case R.drawable.cooling:
                display.setCoolMode();
                return;
            case R.drawable.heating:
                display.setHeatMode();
                //  return;
        }
    }
}
