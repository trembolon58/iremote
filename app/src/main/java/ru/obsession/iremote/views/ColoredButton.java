package ru.obsession.iremote.views;

import android.content.Context;
import android.util.AttributeSet;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.ControlButton;

public class ColoredButton extends DrawerButton {
    public ColoredButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setControlButton(ControlButton controlButton) {
        super.setControlButton(controlButton);
        setCircleBackground(controlButton.icon);
        switch (controlButton.icon) {
            case R.drawable.colored_red:
                setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case R.drawable.colored_blue:
                setBackgroundColor(getResources().getColor(R.color.light_blue));
                break;
            case R.drawable.colored_yellow:
                setBackgroundColor(getResources().getColor(R.color.yellow));
                break;
            case R.drawable.colored_green:
                setBackgroundColor(getResources().getColor(R.color.light_green));
                break;

        }
        setDefaultProperties();
    }

    @Override
    public int getType() {
        return ControlButton.COLORED;
    }
}
