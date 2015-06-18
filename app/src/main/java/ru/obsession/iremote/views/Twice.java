package ru.obsession.iremote.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class Twice extends Joystick {

    private ControlButton bTop;
    private ControlButton bBottom;

    private final static int cTop = -21760;
    private final static int cBottom = -769226;

    public Twice(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultRipple = 0;
    }

    @Override
    public void addControlButton(Control control, ArrayList<ControlButton> controlButtons, ArrayList<DrawerButton> drawerButtons) {
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
    protected void setImageViewProp() {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView imageView = getIcon();
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public void checkButton(int color) {
        switch (color) {
            case cTop:
                controlButton = bTop;
                return;
            case cBottom:
                controlButton = bBottom;
        }
    }

    @Override
    public int getType() {
        return ControlButton.TWICE;
    }


}
