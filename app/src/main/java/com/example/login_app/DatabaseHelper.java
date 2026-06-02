package com.example.login_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite数据库助手 — DDL（数据定义语言）
 * 负责建库、建表、版本升级
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "login_app.db";
    private static final int DB_VERSION = 1;

    // DDL: 表名与列名常量
    public static final String TABLE_USER = "user";
    public static final String COL_ID = "_id";
    public static final String COL_PHONE = "phone";
    public static final String COL_PASSWORD = "password";
    public static final String COL_REMEMBER = "remember";
    public static final String COL_UPDATED_AT = "updated_at";

    // DDL: CREATE TABLE 语句
    private static final String SQL_CREATE_USER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PHONE + " TEXT NOT NULL UNIQUE, "
                    + COL_PASSWORD + " TEXT NOT NULL, "
                    + COL_REMEMBER + " INTEGER DEFAULT 0, "
                    + COL_UPDATED_AT + " TEXT DEFAULT (datetime('now','localtime'))"
                    + ");";

    // DDL: DROP TABLE（版本升级时使用）
    private static final String SQL_DROP_USER =
            "DROP TABLE IF EXISTS " + TABLE_USER + ";";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "=== DDL: 创建数据库表 ===");
        Log.d(TAG, SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "=== DDL: 升级数据库 v" + oldVersion + " -> v" + newVersion + " ===");
        db.execSQL(SQL_DROP_USER);
        onCreate(db);
    }
}
