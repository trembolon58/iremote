package ru.obsession.iremote.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class ViewFactory {
    public static DrawerButton GetDraggable(Context context, ControlButton button) {
        DrawerButton view;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (button.type) {
            case ControlButton.DISPLAY:
                view = setCoordinates(
                        (ConditionDisplay) inflater.inflate(R.layout.button_condition, null, false),
                        button);
                break;
            case ControlButton.TWICE:
                view = setCoordinates(
                        (Twice) inflater.inflate(R.layout.button_twice, null, false),
                        button);
                break;
            case ControlButton.JOYSTICK:
                view = setCoordinates(
                        (Joystick) inflater.inflate(R.layout.button_joystick, null, false),
                        button);
                break;
            case ControlButton.COLORED:
                view = setCoordinates(
                        (ColoredButton) inflater.inflate(R.layout.button_colored, null, false),
                        button);
                break;
            case ControlButton.MEDIA:
                view = setCoordinates(
                        (MediaButtons) inflater.inflate(R.layout.button_media, null, false),
                        button);
                break;
            default:
            //case ControlButton.BUTTON:
                view = (DrawerButton) inflater.inflate(R.layout.button_drawer, null, false);
                view = setCoordinates(view, button);
                break;
        }
        if (view != null) {
            view.controlButton = button;
        }
        view.setControlButton(button);
        return view;
    }

    public static View Inflate(LayoutInflater inflater, @Nullable ViewGroup container, Control control) {
        int layoutId;
        switch (control.getType()) {
            case Control.AIR_CONDITIONAL_CLEANER:
                layoutId = R.layout.skin_conditional;
                break;
            case Control.AQUA:
                layoutId = R.layout.skin_aqua;
                break;
            case Control.AUDIO:
                layoutId = R.layout.skin_audio;
                break;
            case Control.CLEANER:
                layoutId = R.layout.skin_cleaner;
                break;
            case Control.DVD:
                layoutId = R.layout.skin_dvd;
                break;
            case Control.FAN:
                layoutId = R.layout.skin_fan;
                break;
            case Control.LIGHTING:
                layoutId = R.layout.skin_lighting;
                break;
            case Control.METEO:
                layoutId = R.layout.skin_meteo;
                break;
            case Control.PHOTO:
                layoutId = R.layout.skin_photo;
                break;
            case Control.PROJECTOR:
                layoutId = R.layout.skin_projector;
                break;
            case Control.SATELLITE:
                layoutId = R.layout.skin_sattelite;
                break;
            case Control.TV:
                layoutId = R.layout.skin_tv;
                break;
            default:
                layoutId = R.layout.custom_skin;
                break;
        }
        return inflater.inflate(layoutId, container, false);
    }

    private static DrawerButton setCoordinates(DrawerButton view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.drawer_button_size),
                (int) view.getResources().getDimension(R.dimen.drawer_button_size));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        view.getIcon().setImageResource(button.icon);
        return view;
    }

    private static DrawerButton setCoordinates(Twice view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.twice_width),
                (int) view.getResources().getDimension(R.dimen.twice_height));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        view.getIcon().setImageResource(button.icon);
        return view;
    }

    private static DrawerButton setCoordinates(MediaButtons view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.media_width),
                (int) view.getResources().getDimension(R.dimen.media_height));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        return view;
    }

    private static DrawerButton setCoordinates(Joystick view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.joystick_height),
                (int) view.getResources().getDimension(R.dimen.joystick_height));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        view.getIcon().setImageResource(button.icon);
        return view;
    }

    private static DrawerButton setCoordinates(ColoredButton view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.drawer_button_size),
                (int) view.getResources().getDimension(R.dimen.drawer_button_size));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        view.setBackgroundResource(button.icon);
        return view;
    }

    private static DrawerButton setCoordinates(ConditionDisplay view, ControlButton button) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) view.getResources().getDimension(R.dimen.condinion_width),
                (int) view.getResources().getDimension(R.dimen.condition_height));
        params.leftMargin = button.x;
        params.topMargin = button.y;
        view.setLayoutParams(params);
        return view;
    }
}
