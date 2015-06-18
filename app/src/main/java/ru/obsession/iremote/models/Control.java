package ru.obsession.iremote.models;

import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import ru.obsession.iremote.R;

public class Control extends Lirc {

    public static final String KEY = "control_key";

    public static final int AIR_CONDITIONAL_CLEANER = 0;
    public static final int AQUA = 1;
    public static final int AUDIO = 2;
    public static final int CLEANER = 3;
    public static final int DVD = 4;
    public static final int FAN = 5;
    public static final int LIGHTING = 6;
    public static final int METEO = 7;
    public static final int PHOTO = 8;
    public static final int PROJECTOR = 9;
    public static final int SATELLITE = 10;
    public static final int TV = 11;
    public static final int CUSTOM = 12;

    private int id;
    private int type;
    private String company;

    public Control(String fileName){
        super(fileName);
        type = RandomInt(12);
    }

    public Control(Resources res, String fineName){
        super(fineName);
        String[] names = res.getStringArray(R.array.company_names);
        company = names[RandomInt(names.length)];
        type = RandomInt(12);
    }

    public Control(String company, String model){
        this.company = company;
        name = model;
        type = RandomInt(12);
    }

    public Control(int type, String fileName){
        super(fileName);
        this.type = type;
    }

    public static int RandomInt(int max) {
        Random random = new Random();
        return Math.abs(random.nextInt() % max);
    }

    public static Integer[] GetRemoteTypes(){
        Integer[] results = new Integer[12];
        for (int i = 0; i < 12; ++i) {
            results[i] = GetIconForType(i);
        }
        return results;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public static int GetTitleForType(int type) {
        switch (type) {
            case AIR_CONDITIONAL_CLEANER:
                return R.string.air_conditional_cleaner;
            case AQUA:
                return R.string.aqua;
            case AUDIO:
                return R.string.audio;
            case CLEANER:
                return R.string.cleaner;
            case DVD:
                return R.string.dvd;
            case FAN:
                return R.string.fan;
            case LIGHTING:
                return R.string.lighting;
            case METEO:
                return R.string.meteo;
            case PHOTO:
                return R.string.photo;
            case PROJECTOR:
                return R.string.projector;
            case SATELLITE:
                return R.string.satellite;
            case TV:
                return R.string.tv;
            default:
                return R.string.tv;
        }
    }

    public void saveControlToDisk(Context context, String response) throws IOException{
        File file = new File(context.getFilesDir(), getName());
        file.deleteOnExit();
        file.createNewFile();
        FileOutputStream stream = context.openFileOutput(file.getName(), Context.MODE_APPEND);
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.write(response);
        writer.close();
        setFileName(file.getPath());
        parseFile(context);
    }

    public static int GetIconForType(int type) {
        switch (type) {
            case AIR_CONDITIONAL_CLEANER:
                return R.drawable.air_conditional_cleaner;
            case AQUA:
                return R.drawable.aqua;
            case AUDIO:
                return R.drawable.audio;
            case CLEANER:
                return R.drawable.cleaner;
            case DVD:
                return R.drawable.dvd;
            case FAN:
                return R.drawable.fan_control;
            case LIGHTING:
                return R.drawable.lighting_control;
            case METEO:
                return R.drawable.meteo;
            case PHOTO:
                return R.drawable.photo;
            case PROJECTOR:
                return R.drawable.projector;
            case SATELLITE:
                return R.drawable.satellite;
            case TV:
                return R.drawable.tv;
            default:
                return R.drawable.tv;
        }
    }

    public static int GetColorForType(int type) {
        switch (type) {
            case AIR_CONDITIONAL_CLEANER:
                return R.color.red;
            case AQUA:
                return R.color.purple;
            case AUDIO:
                return R.color.blue;
            case CLEANER:
                return R.color.green;
            case DVD:
                return R.color.yellow;
            case FAN:
                return R.color.orange;
            case LIGHTING:
                return R.color.brown;
            case METEO:
                return R.color.blue_grey;
            case PHOTO:
                return R.color.grey;
            case PROJECTOR:
                return R.color.deep_orange;
            case SATELLITE:
                return R.color.pink;
            case TV:
                return R.color.teal;
            default:
                return R.color.lime;
        }
    }
    public int getColor(Resources r){
        return r.getColor(GetColorForType(type));
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return GetIconForType(type);
    }
}
