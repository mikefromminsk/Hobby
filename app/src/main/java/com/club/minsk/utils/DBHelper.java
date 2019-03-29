package com.club.minsk.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table strings ("
                + "id integer primary key autoincrement,"
                + "strkey text,"
                + "value text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    List<Map<String, String>> select(String table, String[] columns, String selection, String[] selectionArgs) {
        List<Map<String, String>> result = new ArrayList<>();

        Cursor c = getWritableDatabase().query(table, columns, selection, selectionArgs, null, null, null);

        if (c.moveToFirst()) {
            do {
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < c.getColumnCount(); i++)
                    row.put(c.getColumnName(i), c.getString(i));
                result.add(row);
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    String scalar(String table, String column, String selection, String[] selectionArgs) {
        List<Map<String, String>> result = select(table, new String[]{column}, selection, selectionArgs);
        if (result.size() > 0)
            return result.get(0).get(column);
        return null;
    }

    public void insert(String key, String value) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        cv.put("strkey", key);
        cv.put("value", value);
        db.insert("strings", null, cv);
    }

    public void update(String key, String value) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        cv.put("value", value);
        db.update("strings", cv, "strkey = ?", new String[]{key});
    }

    public void clear() {
        getWritableDatabase().delete("strings", null, null);
    }

    public void put(String key, String value){
        String keyExist = scalar("strings", "strkey", "strkey = ?", new String[]{key});
        if (keyExist != null){
            update(key, value);
        }else{
            insert(key, value);
        }
    }

    public Map<String, String> all() {
        Map<String, String> result = new HashMap<>();
        List<Map<String, String>> select = select("strings", null, null, null);
        for (Map<String, String> row :select)
          result.put(row.get("strkey"), row.get("value"));
        return result;
    }

    public void delete(String key) {
        getWritableDatabase().delete("strings", "strkey = ?", new String[]{key});
    }

    public String get(String key) {
        return scalar("strings", "value", "strkey = ?", new String[]{key});
    }
}
