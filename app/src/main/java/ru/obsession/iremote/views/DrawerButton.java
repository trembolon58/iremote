package ru.obsession.iremote.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.ButtonFloat;

import java.util.ArrayList;

import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class DrawerButton extends ButtonFloat implements Draggable {
    private int _xDelta;
    private int _yDelta;
    protected boolean movable = false;
    protected boolean selected;
    protected boolean needRipple;
    public ControlButton controlButton;


    public DrawerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickAfterRipple(false);
        needRipple = true;
    }

    public void setControlButton(ControlButton controlButton) {
        this.controlButton = controlButton;
    }

    public void addControlButton(Control control, ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        //TODO:
        controlButton = new ControlButton(control, 0, getType(), params.leftMargin, params.topMargin);
        controlButton.visible = 1;
        String tag = (String) getTag();
        controlButton.name = tag;
        controlButton.icon = getResources().getIdentifier(tag, "drawable", getContext().getPackageName());
        controlButtons.add(controlButton);
        drawerButtons.add(this);
    }

    public int getType() {
        return ControlButton.BUTTON;
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (movable) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    selected = true;
                    RelativeLayout.LayoutParams layoutParamsOld = (RelativeLayout.LayoutParams) getLayoutParams();
                    if (layoutParamsOld.leftMargin >= 0) {
                        _xDelta = X - layoutParamsOld.leftMargin;
                        _yDelta = Y - layoutParamsOld.topMargin;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();

                    RelativeLayout frameLayout = (RelativeLayout) getParent();
                    if (X - _xDelta >= 0 && (X - _xDelta) <= (frameLayout.getWidth() - getWidth())) {
                        layoutParams.leftMargin = X - _xDelta;
                    } else {
                        if (X - _xDelta < 0) {
                            layoutParams.leftMargin = 0;
                        } else {
                            layoutParams.leftMargin = frameLayout.getWidth() - getWidth();
                        }
                    }
                    if (Y - _yDelta >= 0 && (Y - _yDelta) <= (frameLayout.getHeight() - getHeight())) {
                        layoutParams.topMargin = Y - _yDelta;
                    } else {
                        if (Y - _yDelta < 0) {
                            layoutParams.topMargin = 0;
                        } else {
                            layoutParams.topMargin = frameLayout.getHeight() - getHeight();
                        }
                    }
                    setLayoutParams(layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    selected = false;
                    break;
            }
            invalidate();
            return true;
        }
        return needRipple && super.onTouchEvent(event);
    }

    @Override
    public boolean isDrawed() {
        return selected;
    }

    @Override
    public void setDraggable(boolean draggable) {
        movable = draggable;

    }

    public boolean isMovable() {
        return movable;
    }

    @Override
    public void toggleDraggable() {
        movable = !movable;
    }
}
