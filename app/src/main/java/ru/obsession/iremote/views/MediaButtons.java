package ru.obsession.iremote.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class MediaButtons extends Joystick {

    private ControlButton bPlay;
    private ControlButton bStop;
    private ControlButton bLeft;
    private ControlButton bRight;
    private ControlButton bPrev;
    private ControlButton bNext;

    private final static int cPlay = -7617718;
    private final static int cStop = -16537100;
    private final static int cLeft = -769226;
    private final static int cRight = -21760;
    private final static int cPrev = -12627531;
    private final static int cNext = -11751600;

    public MediaButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.media);

    }

    @Override
    protected void setImageViewProp() {
        getIcon().setVisibility(GONE);
    }

    @Override
    public int getType() {
        return ControlButton.MEDIA;
    }

    @Override
    protected void getBitmap() {
        bitmap = getBitmapFromView();
    }

    @Override
    public void addControlButton(Control control, ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
        bPlay = addSubButton(control, controlButtons, drawerButtons);
        bLeft = addSubButton(control, controlButtons, drawerButtons);
        bRight = addSubButton(control, controlButtons, drawerButtons);
        bStop = addSubButton(control, controlButtons, drawerButtons);
        bPrev = addSubButton(control, controlButtons, drawerButtons);
        bNext = addSubButton(control, controlButtons, drawerButtons);
    }

    private ControlButton addSubButton( Control control,
                                        ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        //TODO:
        ControlButton button = new ControlButton(control, 0, getType(), params.leftMargin, params.topMargin);
        button.visible = 1;
        button.name = "media";
        controlButtons.add(button);
        drawerButtons.add(this);
        return button;
    }

    @Override
    public void checkButton(int color) {
        switch (color) {
            case cPlay:
                controlButton = bPlay;
                return;
            case cStop:
                controlButton = bStop;
                return;
            case cLeft:
                controlButton = bLeft;
                return;
            case cRight:
                controlButton = bRight;
                return;
            case cPrev:
                controlButton = bPrev;
                return;
            case cNext:
                controlButton = bNext;
        }
    }

    private Bitmap getBitmapFromView() {
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = getBackground();
        bgDrawable.draw(canvas);
        return returnedBitmap;
    }
}
