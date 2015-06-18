package ru.obsession.iremote.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbOpenHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "database.db";
    private static final String DB_SCHEMA_SQL = "db_schema.sql";
    private static final int SCHEMA = 1;
    Context contextAsset;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        contextAsset = context;
    }

    private String getDbSchema() throws IOException {
        AssetManager assetManager = contextAsset.getAssets();
        InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open(DB_SCHEMA_SQL));
        BufferedReader in = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        in.close();
        return stringBuilder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] sqlSend = new String[0];
        try {
            sqlSend = getDbSchema().split(";");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sqlSend.length; i++) {
            sqlSend[i] = sqlSend[i].trim();
            if (!sqlSend[i].equals("")) {
                db.execSQL(sqlSend[i]);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'rooms'");
        db.execSQL("DROP TABLE IF EXISTS 'controls'");
        db.execSQL("DROP TABLE IF EXISTS 'buttons'");
    }
}
