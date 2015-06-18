package ru.obsession.iremote.models;

import android.os.Parcel;
import android.os.Parcelable;

import ru.obsession.iremote.R;

public class ControlButton implements Parcelable {


    private Lirc lirc;

    public static final String KEY = "control_button_key";

    public static final int VISIBLE = 1;
    public static final int IN_VISIBLE = 0;


    public static final int BUTTON = 1;
    public static final int DISPLAY = 2;
    public static final int TWICE = 3;
    public static final int JOYSTICK = 4;
    public static final int COLORED = 5;
    public static final int MEDIA = 6;
    public int id;
    public int type;
    public int x;
    public int y;
    public int visible;
    public int icon;
    public String name;

    public static final Creator<ControlButton> CREATOR;

    static {
        CREATOR = new Creator<ControlButton>() {

            @Override
            public ControlButton createFromParcel(Parcel source) {
                return new ControlButton(source);
            }

            @Override
            public ControlButton[] newArray(int size) {
                return new ControlButton[size];
            }
        };
    }

    public static final Integer[] BUTTON_TYPES_ICONS = new Integer[]
            {
                 //   R.drawable.air_flash_cond,
                 //   R.drawable.anion,
                //    R.drawable.aqua_wind,
                    R.drawable.auto,
                    R.drawable.back,
                    R.drawable.bottom,
                    R.drawable.brightness,
                 //   R.drawable.cam,
                    R.drawable.ch,
                //    R.drawable.clock,
               //     R.drawable.comfort,
               //     R.drawable.cool_mode,
                    R.drawable.digit_0,
                    R.drawable.digit_1,
                    R.drawable.digit_2,
                    R.drawable.digit_3,
                    R.drawable.digit_4,
                    R.drawable.digit_5,
                    R.drawable.digit_6,
                    R.drawable.digit_7,
                    R.drawable.digit_8,
                    R.drawable.digit_9,
                    R.drawable.display,
                    R.drawable.down,
                    R.drawable.dual_screen,
                    R.drawable.enter,
                    R.drawable.exit,
             //       R.drawable.fan_button,
                    R.drawable.favorites,
                    R.drawable.forward,
              //      R.drawable.freeze,
                    R.drawable.home,
             //       R.drawable.horisontal_wind,
             //       R.drawable.hot_cond,
             //       R.drawable.hot_mode,
                    R.drawable.info,
                    R.drawable.language,
                    R.drawable.left,
                    R.drawable.light,
              //      R.drawable.light_econom,
                    R.drawable.refresh,
                    R.drawable.menu,
                    R.drawable.mode,
                    R.drawable.mute,
                    R.drawable.next,
                    R.drawable.ok,
                    R.drawable.open,
                    R.drawable.other,
                    R.drawable.page,
                    R.drawable.pause,
                    R.drawable.pc,
                    R.drawable.play,
                    R.drawable.pop_menu,
                    R.drawable.power,
                    R.drawable.power_red,
                    R.drawable.prev,
                    R.drawable.reset,
            //        R.drawable.rewind,
                    R.drawable.right,
                    R.drawable.settings,
                    R.drawable.signal,
                    R.drawable.sleep,
                    R.drawable.sound_channel,
                    R.drawable.step,
                    R.drawable.stop,
                    R.drawable.subtitle,
                    R.drawable.super_button,
                    R.drawable.sway,
                    R.drawable.system,
            //        R.drawable.temp,
                    R.drawable.tv_av,
                    R.drawable.up,
            //        R.drawable.usb,
                    R.drawable.vertical_wind,
                    R.drawable.video,
                    R.drawable.volume,
            //        R.drawable.wind_speed,
            //        R.drawable.wind_type,
                    R.drawable.other_button
            };

    public ControlButton(Lirc lirc, int code, int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.lirc = lirc;
    }

    public ControlButton(Lirc lirc, String name) {
        this.lirc = lirc;
        this.name = name;
    }

    public void control() {
        if (lirc != null) {
            lirc.sendSignal(name);
        }
    }

    public boolean isVisible() {
        return visible == 1;
    }

    public void setVisible(boolean b) {
        visible = b ? 1 : 0;
    }

    public ControlButton(Parcel in) {
        readFromParcel(in);
    }

    public void setLirc(Lirc lirc) {
        this.lirc = lirc;
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        x = in.readInt();
        y = in.readInt();
        visible = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(visible);
        dest.writeString(name);
    }
}

