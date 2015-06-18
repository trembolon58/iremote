package ru.obsession.iremote;

import android.app.Application;

import ru.obsession.iremote.database.DatabaseApi;

public class IRemote extends Application {

    public static final String WIDGET_PREFERENCES = "iRemote_widget_preferences_from_spirin.dmitrij@list.ru";

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseApi.getInstance(getApplicationContext()).closeDatabase();
    }

}