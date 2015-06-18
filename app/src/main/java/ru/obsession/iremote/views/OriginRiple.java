package ru.obsession.iremote.views;

import android.graphics.Color;
import android.view.View;

import com.gc.materialdesign.views.LayoutRipple;
import com.nineoldandroids.view.ViewHelper;

public class OriginRiple {
    public static void setOriginRiple(final LayoutRipple layoutRipple) {

        layoutRipple.post(new Runnable() {

            @Override
            public void run() {
                View v = layoutRipple.getChildAt(0);
                layoutRipple.setxRippleOrigin(ViewHelper.getX(v) + v.getWidth() / 2);
                layoutRipple.setyRippleOrigin(ViewHelper.getY(v) + v.getHeight() / 2);

                layoutRipple.setRippleColor(Color.parseColor("#1E88E5"));

                layoutRipple.setRippleSpeed(40);
            }
        });

    }
}