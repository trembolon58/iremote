package ru.obsession.iremote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;
import ru.obsession.iremote.models.Room;

public class DatabaseApi {
    private static volatile DatabaseApi databaseApi;
    private SQLiteDatabase db;

    private DatabaseApi(Context context) {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public static DatabaseApi getInstance(Context context) {
        if (databaseApi == null) {
            synchronized (DatabaseApi.class) {
                if (databaseApi == null) {
                    databaseApi = new DatabaseApi(context);
                }
            }
        }
        return databaseApi;
    }

    public ArrayList<Room> getRooms() {
        ArrayList<Room> rooms = new ArrayList<Room>();
        Cursor cursor = db.rawQuery("SELECT * FROM rooms", null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    Room room = new Room();
                    room.name = cursor.getString(cursor.getColumnIndex("name"));
                    room.setId(cursor.getString(cursor.getColumnIndex("_id")));
                    room.iconRes = cursor.getInt(cursor.getColumnIndex("icon_res"));
                    rooms.add(room);
                } while (cursor.moveToNext());
            }

        }
        cursor.close();
        return rooms;
    }

    public Room getRoom(int roomId) {
        Room room = null;
        Cursor cursor = db.rawQuery("SELECT * FROM rooms WHERE _id = " + String.valueOf(roomId), null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                room = new Room();
                room.name = cursor.getString(cursor.getColumnIndex("name"));
                room.setId(roomId);
                room.iconRes = cursor.getInt(cursor.getColumnIndex("icon_res"));
            }

        }
        cursor.close();
        return room;
    }

    public ArrayList<Control> getControls(Room room) {
        ArrayList<Control> controls = new ArrayList<>();
        if (room != null) {
            Cursor cursor = db.rawQuery("SELECT * FROM controls WHERE room_id = " + String.valueOf(room.getId()), null);
            if (cursor.moveToFirst()) {
                do {
                    Control control = new Control(cursor.getInt(cursor.getColumnIndex("type")),
                            cursor.getString(cursor.getColumnIndex("config_file")));
                    control.setName(cursor.getString(cursor.getColumnIndex("name")));
                    control.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    control.setCompany(cursor.getString(cursor.getColumnIndex("company")));
               /* control.iconRes = cursor.getInt(cursor.getColumnIndex("icon_res"));
                control.nameRes = cursor.getInt(cursor.getColumnIndex("name_res"));*/
                    controls.add(control);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return controls;
    }

    public void closeDatabase() {
        db.close();
        databaseApi = null;
    }

    public void removeControl(Control control) {
        String[] args = new String[]{String.valueOf(control.getId())};
        db.delete("buttons", "control_id = ?", args);
        db.delete("controls", "_id = ?", args);
    }

    public void removeRoom(Room room) {
        for (Control control : getControls(room)) {
            removeControl(control);
        }
        db.delete("rooms", "_id = ?", new String[]{String.valueOf(room.getId())});
    }

    public long addControl(Room room, Control control) {
        ContentValues values = new ContentValues();
        values.put("name", control.getName());
        values.put("room_id", room.getId());
        values.put("company", control.getCompany());
        values.put("type", control.getType());
        values.put("config_file", control.getFileName());
        long id = db.insert("controls", null, values);
        ArrayList<ControlButton> buttons = new ArrayList<>();
        for (String name : control.getCommands()) {
            ControlButton button = new ControlButton(control, name);
            button.setVisible(false);
            buttons.add(button);
        }
        control.setId(id);
        saveControl(control, buttons);
        return id;
    }

    public long addRoom(Room room) {
        ContentValues values = new ContentValues();
        values.put("icon_res", room.iconRes);
        values.put("name", room.name);
        long id = db.insert("rooms", null, values);
        room.setId(id);
        return id;
    }

    public long updateRoom(Room room) {
        ContentValues values = new ContentValues();
        values.put("icon_res", room.iconRes);
        values.put("name", room.name);
        long id = db.update("rooms", values, "_id = ?", new String[]{String.valueOf(room.getId())});
        room.setId(id);
        return id;
    }

    public void saveControl(Control control, ArrayList<ControlButton> buttons) {
        removeButtons(control);
        ContentValues values = new ContentValues();
        for (ControlButton var : buttons) {
            values.put("type", var.type);
            values.put("visible", var.visible);
            values.put("icon", var.icon);
            values.put("control_id", control.getId());
            values.put("type", var.type);
            values.put("name", var.name);
            values.put("x", var.x);
            values.put("y", var.y);
            db.insert("buttons", null, values);
            values.clear();
        }
        values.put("custom_view", 1);
        db.update("controls", values, "_id = ?", new String[]{String.valueOf(control.getId())});
    }

    private void removeButtons(Control control) {
        db.delete("buttons", "control_id = ?", new String[]{String.valueOf(control.getId())});
    }


    private void iterateButtons(Control control, ArrayList<ControlButton> buttons, Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                ControlButton controlButton = new ControlButton(control,
                        cursor.getString(cursor.getColumnIndex("name")));
                controlButton.icon = cursor.getInt(cursor.getColumnIndex("icon"));
                controlButton.id = cursor.getInt(cursor.getColumnIndex("_id"));
                controlButton.type = cursor.getInt(cursor.getColumnIndex("type"));
                controlButton.x = cursor.getInt(cursor.getColumnIndex("x"));
                controlButton.y = cursor.getInt(cursor.getColumnIndex("y"));
                controlButton.visible = cursor.getInt(cursor.getColumnIndex("visible"));
                buttons.add(controlButton);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public ArrayList<ControlButton> getButtons(int controlId) {
        ArrayList<ControlButton> buttons = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM buttons WHERE control_id = " + String.valueOf(controlId), null);
        Control control = getControl(controlId);
        iterateButtons(control, buttons, cursor);
        return buttons;
    }

    public ArrayList<ControlButton> getVisibleButtons(int controlId) {
        ArrayList<ControlButton> buttons = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM buttons WHERE control_id = " + String.valueOf(controlId)
                + " AND visible = 1", null);
        Control control = getControl(controlId);
        iterateButtons(control, buttons, cursor);
        return buttons;
    }


    public ArrayList<ControlButton> getInVisibleButtons(int controlId) {
        ArrayList<ControlButton> buttons = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM buttons WHERE control_id = " + String.valueOf(controlId)
                + " AND visible = 0", null);
        Control control = getControl(controlId);
        iterateButtons(control, buttons, cursor);
        return buttons;
    }

    public void setVisible(ControlButton controlButton) {
        ContentValues values = new ContentValues();
        values.put("visible", 1);
        db.update("rooms", values, "_id = ?", new String[]{String.valueOf(controlButton.id)});
    }

    public void setInvisible(ControlButton controlButton) {
        ContentValues values = new ContentValues();
        values.put("visible", 0);
        db.update("rooms", values, "_id = ?", new String[]{String.valueOf(controlButton.id)});
    }

    public Control getControl(int id) {
        Control control = null;
        Cursor cursor = db.rawQuery("SELECT * FROM controls WHERE _id = " + String.valueOf(id), null);
        if (cursor.moveToFirst()) {
            control = new Control(cursor.getString(cursor.getColumnIndex("config_file")));
            control.setName(cursor.getString(cursor.getColumnIndex("name")));
            control.setType(cursor.getInt(cursor.getColumnIndex("type")));
            control.setCompany(cursor.getString(cursor.getColumnIndex("company")));
            control.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            /*control.iconRes = cursor.getInt(cursor.getColumnIndex("icon_res"));
            control.nameRes = cursor.getInt(cursor.getColumnIndex("name_res"));*/
        }
        cursor.close();

        return control;
    }

    public boolean isCustomView(Control control) {
        boolean result = false;
        Cursor cursor = db.rawQuery("SELECT custom_view FROM controls WHERE _id = " + String.valueOf(control.getId()), null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("custom_view")) == 1;
        }
        cursor.close();
        return result;
    }

    public void saveDIYRemote() {
        //TODO
    }

    public ArrayList<Control> getAllControls() {
        ArrayList<Control> controls = new ArrayList<>();
        Control control;
        Cursor cursor = db.rawQuery("SELECT * FROM controls", null);
        if (cursor.moveToFirst()) {
            do {
                control = new Control(cursor.getString(cursor.getColumnIndex("config_file")));
                control.setName(cursor.getString(cursor.getColumnIndex("name")));
                control.setType(cursor.getInt(cursor.getColumnIndex("type")));
                control.setCompany(cursor.getString(cursor.getColumnIndex("company")));
                control.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            /*control.iconRes = cursor.getInt(cursor.getColumnIndex("icon_res"));
            control.nameRes = cursor.getInt(cursor.getColumnIndex("name_res"));*/
                controls.add(control);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return controls;
    }
}
