package com.momo.apple.wzq.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * 保存游戏相关信息
 */
public class DBWzq extends SQLiteOpenHelper {
    private static final String DB_NAME = "wzq.db";
    private static final String TBL_NAME = "t_record";
    private static final String CREATE_TBL = " CREATE TABLE "+TBL_NAME+" (_id INTEGER DEFAULT '1' " +
            "NOT NULL PRIMARY KEY AUTOINCREMENT, score TEXT  NOT NULL)";

    private SQLiteDatabase db;

    public DBWzq(Context c) {
        super(c, DB_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TBL);
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_NAME, null, values);
        db.close();
    }

    public Cursor query() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);
        return c;
    }

    public void del(int id) {
        if (db == null)
            db = getWritableDatabase();
        db.delete(TBL_NAME, "_id=?", new String[]{String.valueOf(id)});
    }

    public void delall() {
        db = getWritableDatabase();
        db.delete(TBL_NAME, null, null);
    }

    @Override
    public void close() {
        if (db != null)
            db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}