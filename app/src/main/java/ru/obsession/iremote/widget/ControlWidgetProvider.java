package ru.obsession.iremote.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import ru.obsession.iremote.IRemote;
import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;

public class ControlWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_RECEIVER = "ButtonClick";
    public static String ACTION_CONTROL_ID = "controlID";
    public static String ACTION_BUTTON_NAME = "controlNAME";

    /**
     * Returns number of cells needed for given size of the widget.
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences sp = context.getSharedPreferences(
                IRemote.WIDGET_PREFERENCES, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetIds, buildUpdate(context, sp, id));
        }
    }

    /**
     * вызывается при изменении размера виджета
     *
     * @param context          контекст приложения
     * @param appWidgetManager менеджер виджетов
     * @param appWidgetId      идентефикатор измененного виджета
     * @param newOptions       новые настройки виджета
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Toast.makeText(context, "Size", Toast.LENGTH_SHORT).show();
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int w = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int r = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        //String s = "[" + minWidth +"-" + w + "] X [" + minHeight +"-" + r + "]";
        SharedPreferences sp = context.getSharedPreferences(
                IRemote.WIDGET_PREFERENCES, Context.MODE_PRIVATE);
        appWidgetManager.updateAppWidget(appWidgetId, buildUpdate(context, sp, appWidgetId));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Удаляем Preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(
                IRemote.WIDGET_PREFERENCES, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(context.getPackageName() + widgetID);
        }
        editor.commit();
    }

    public RemoteViews buildUpdate(Context context, SharedPreferences sp, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view);
        //views.setBundle(R.id.navigation_control, "setClickBundle", new Bundle());
        int controlId = sp.getInt(context.getPackageName() + id, -1);
        if (controlId != -1) {
            Control control = DatabaseApi.getInstance(context).getControl(controlId);
            //Подготавливаем Intent для Broadcast
            control.parseFile(context);
            Intent active = new Intent(context, ControlWidgetProvider.class);
            active.setAction(ACTION_WIDGET_RECEIVER);
            active.putExtra(ACTION_CONTROL_ID, control.getId());
            //тестовое
            active.putExtra(ACTION_BUTTON_NAME, control.getCommands()[0]);
            //создаем наше событие
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);

            //регистрируем наше событие
            views.setOnClickPendingIntent(R.id.button, actionPendingIntent);
       /* if (control != null) {
            if (control.getType() == Control.AIR_CONDITIONAL_CLEANER) {
                views.setImageViewResource(R.id.imageView, R.drawable.widget_3);
            } else if (control.getType() == Control.TV) {
                views.setImageViewResource(R.id.imageView, R.drawable.widget_1);
            } else if (control.getType() == Control.DVD) {
                views.setImageViewResource(R.id.imageView, R.drawable.widget_2);
            } else if (control.getType() == Control.AUDIO) {
                views.setImageViewResource(R.id.imageView, R.drawable.widget_2);
            } else {
                views.setImageViewResource(R.id.imageView, R.drawable.add_remote);
            }*/
            // }
        }
        return views;
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
            int controlId = intent.getIntExtra(ACTION_CONTROL_ID, -1);
            String cmd = intent.getStringExtra(ACTION_BUTTON_NAME);
            if (controlId != -1 && cmd != null) {
                Control control = DatabaseApi.getInstance(context).getControl(controlId);
                if (control != null) {
                    //пока оставить так
                    control.parseFile(context);
                    control.sendSignal(cmd);
                }
            }
        }
    }
}
