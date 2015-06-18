package ru.obsession.iremote.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class Joystick extends DrawerButton {

    protected int defaultRipple;
    protected Bitmap bitmap;

    private ControlButton bOk;
    private ControlButton bLeft;
    private ControlButton bRight;
    private ControlButton bTop;
    private ControlButton bBottom;

    private final static int cOk = 0;
    private final static int cLeft = -12627531;
    private final static int cRight = -11751600;
    private final static int cTop = -21760;
    private final static int cBottom = -769226;

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageViewProp();
        defaultRipple = getRippleColor();
        setBackgroundResource(R.drawable.joustick_background);
    }

    protected void setImageViewProp() {
        ImageView imageView = getIcon();
        imageView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setPadding(0,0,0,0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public void addControlButton(Control control, ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
        bOk = addSubButton(control, controlButtons, drawerButtons);
        bLeft = addSubButton(control, controlButtons, drawerButtons);
        bRight = addSubButton(control, controlButtons, drawerButtons);
        bTop = addSubButton(control, controlButtons, drawerButtons);
        bBottom = addSubButton(control, controlButtons, drawerButtons);
    }

    private ControlButton addSubButton( Control control,
            ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        //TODO:
        ControlButton button = new ControlButton(control, 0, getType(), params.leftMargin, params.topMargin);
        button.visible = 1;
        String tag = (String) getTag();
        button.name = tag;
        button.icon = getResources().getIdentifier(tag, "drawable", getContext().getPackageName());
        controlButtons.add(button);
        drawerButtons.add(this);
        return button;
    }

    @Override
    public int getType() {
        return ControlButton.JOYSTICK;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!movable && event.getX() > 0 && event.getY() > 0) {
            if (bitmap == null) {
                getBitmap();
            }
            float eventX = event.getX();
            float eventY = event.getY();
            float[] eventXY = new float[]{eventX, eventY};

            Matrix invertMatrix = new Matrix();
            getIcon().getImageMatrix().invert(invertMatrix);

            invertMatrix.mapPoints(eventXY);
            int x = (int) eventXY[0];
            int y = (int) eventXY[1];

            //Limit x, y range within bitmap
            if (x < 0) {
                x = 0;
            } else if (x > bitmap.getWidth() - 1) {
                x = bitmap.getWidth() - 1;
            }

            if (y < 0) {
                y = 0;
            } else if (y > bitmap.getHeight() - 1) {
                y = bitmap.getHeight() - 1;
            }
            int touchedRGB;
            do {
                touchedRGB = bitmap.getPixel(x, y);
                if (touchedRGB == 0) {
                    setRippleColor(defaultRipple);
                } else {
                    setRippleColor(touchedRGB);
                }
                --x;

            } while ( isWhite(touchedRGB) && event.getX() > 0 );
            checkButton(touchedRGB);
        }
        return super.onTouchEvent(event);
    }

    public void checkButton(int color) {
        switch (color) {
            case cOk:
                controlButton = bOk;
                return;
            case cLeft:
                controlButton = bLeft;
                return;
            case cRight:
                controlButton = bRight;
                return;
            case cTop:
                controlButton = bTop;
                return;
            case cBottom:
                controlButton = bBottom;
        }
    }

    private boolean isWhite(int argb) {
        int lum = (   77  * ((argb>>16)&255)
                + 150 * ((argb>>8)&255)
                + 29  * ((argb)&255))>>8;
        return lum > 200;
    }

    protected void getBitmap() {
        Drawable imgDrawable = getIcon().getDrawable();
        bitmap = ((BitmapDrawable) imgDrawable).getBitmap();
    }
}
