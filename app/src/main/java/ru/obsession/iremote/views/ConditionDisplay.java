package ru.obsession.iremote.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.ControlButton;

public class ConditionDisplay extends DrawerButton {

    public static final int ONE_FAN = 0;
    public static final int THO_FAN = 1;
    public static final int THREE_FAN = 2;
    public static final int AUTO_FAN = 3;

    public static final int HORIZONTAL_WIND = 4;
    public static final int VERTICAL_WIND = 5;
    public static final int DIAGONAL_WIND = 6;
    public static final int AUTO_WIND = 7;

    public static final int COOL_MODE = 8;
    public static final int HEAT_MODE = 9;
    public static final int DEHUMANISATION_MODE = 10;
    public static final int VENTILATION_MODE = 11;

    private static final int UPDATE_WIND = 54126;
    private static final int UPDATE_FAN = 985623;

    private TextView tTemp;
    private ImageView iFan;
    private ImageView iWind;
    private ImageView iDegrees;
    private ImageView iMode;
    private boolean enabled;
    private int temperature;
    private int fan;
    private int wind;
    private Timer timer;
    private int mode;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == UPDATE_FAN) {
                iFan.setImageResource(getDynamicIcon(msg.arg1));
                return true;
            } else if (msg.what == UPDATE_WIND) {
                iWind.setImageResource(getDynamicIcon(msg.arg1));
                return true;
            }
            return false;
        }
    });


    public ConditionDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.condition_display);
        needRipple = false;
        temperature = 22;
        fan = THREE_FAN;
        wind = DIAGONAL_WIND;
    }

    public void toggleEnabled() {
        enabled = !enabled;
        if (enabled) {
            for (int i = 0; i < getChildCount(); ++i) {
                getChildAt(i).setVisibility(VISIBLE);
            }
        } else {
            for (int i = 0; i < getChildCount(); ++i) {
                getChildAt(i).setVisibility(GONE);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        tTemp = (TextView) findViewById(R.id.textTemp);
        iFan = (ImageView) findViewById(R.id.fan);
        iWind = (ImageView) findViewById(R.id.wind);
        iDegrees = (ImageView) findViewById(R.id.degres);
        iMode = (ImageView) findViewById(R.id.mode);
    }

    public void upTemp() {
        if (enabled && temperature < 30) {
            ++temperature;
            tTemp.setText(String.valueOf(temperature));
        }
    }

    public void downTemp() {
        if (enabled && temperature > 15) {
            --temperature;
            tTemp.setText(String.valueOf(temperature));
        }
    }

    public void changeFan() {
        if (enabled) {
            fan = (++fan) % 4;
            stopTimer();
            if (fan == AUTO_FAN) {
                startTimer();
            } else {
                iFan.setImageResource(getDynamicIcon(fan));
            }
        }
    }

    public void changeWind() {
        if (enabled) {
            wind = (++wind) % 4 + 4;
            stopTimer();
            if (wind == AUTO_WIND) {
                startTimer();
            } else {
                iWind.setImageResource(getDynamicIcon(wind));
            }
        }
    }

    private void stopTimer() {
        if (fan != AUTO_FAN && wind != AUTO_WIND && timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void startTimer() {
        if (timer == null && (fan == AUTO_FAN || wind == AUTO_WIND)) {
            timer = new Timer();
            timer.schedule(new UpdateTask(), 100, 500);
        }
    }

    public void changeMode() {
        if (enabled) {
            mode = (++mode) % 4 + 8;
            switch (mode) {
                case COOL_MODE:
                case HEAT_MODE:
                    tTemp.setVisibility(VISIBLE);
                    iDegrees.setVisibility(VISIBLE);
                    break;
                case DEHUMANISATION_MODE:
                case VENTILATION_MODE:
                    tTemp.setVisibility(GONE);
                    iDegrees.setVisibility(GONE);
                    break;
            }
            iMode.setImageResource(getDynamicIcon(mode));
        }
    }

    public void setHeatMode() {
        mode = HEAT_MODE;
        iMode.setImageResource(getDynamicIcon(mode));
        tTemp.setVisibility(VISIBLE);
        iDegrees.setVisibility(VISIBLE);
    }

    public void setCoolMode() {
        mode = COOL_MODE;
        iMode.setImageResource(getDynamicIcon(mode));
        tTemp.setVisibility(VISIBLE);
        iDegrees.setVisibility(VISIBLE);
    }



    private int getDynamicIcon(int type) {
        switch (type) {
            case ONE_FAN:
                return R.drawable.fan_1;
            case THO_FAN:
                return R.drawable.fan_2;
            case THREE_FAN:
                return R.drawable.fan_3;
            case HORIZONTAL_WIND:
                return R.drawable.horisontal_wind;
            case VERTICAL_WIND:
                return R.drawable.vertical_wind;
            case DIAGONAL_WIND:
                return R.drawable.diagonal_wind;
            case COOL_MODE:
                return R.drawable.cooling;
            case HEAT_MODE:
                return R.drawable.heating;
            case DEHUMANISATION_MODE:
                return R.drawable.dehumidification;
            default: //case VENTILATION_MODE:
                return R.drawable.ventilation;
        }
    }

    @Override
    public int getType() {
        return ControlButton.DISPLAY;
    }

   private class UpdateTask extends TimerTask {
       private int lastWind = THREE_FAN;
       private int lastFan = DIAGONAL_WIND;

       @Override
       public void run() {
           Message message;
           if (fan == AUTO_FAN) {
               lastFan = (++lastFan) % 3;
               message = new Message();
               message.what = UPDATE_FAN;
               message.arg1 = lastFan;
               handler.sendMessage(message);
           }
           if (wind == AUTO_WIND) {
               message = new Message();
               lastWind = (++lastWind) % 3 + 4;
               message.what = UPDATE_WIND;
               message.arg1 = lastWind;
               handler.sendMessage(message);
           }
       }
   }

}