package com.example.login_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite数据库助手 — DDL（数据定义语言）
 * 四个本地持久化表: user / cart / profile / address
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "login_app.db";
    private static final int DB_VERSION = 2;

    // ========== 用户表 ==========
    public static final String TABLE_USER = "user";
    public static final String COL_USER_ID = "_id";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_REMEMBER = "remember";
    public static final String COL_USER_UPDATED = "updated_at";

    // ========== 购物车表 ==========
    public static final String TABLE_CART = "cart";
    public static final String COL_CART_ID = "_id";
    public static final String COL_CART_PRODUCT_ID = "product_id";
    public static final String COL_CART_NAME = "product_name";
    public static final String COL_CART_PRICE = "product_price";
    public static final String COL_CART_QTY = "quantity";

    // ========== 用户资料表 ==========
    public static final String TABLE_PROFILE = "profile";
    public static final String COL_PROFILE_ID = "_id";
    public static final String COL_PROFILE_NICKNAME = "nickname";
    public static final String COL_PROFILE_AVATAR = "avatar_res_id";

    // ========== 地址表 ==========
    public static final String TABLE_ADDRESS = "address";
    public static final String COL_ADDR_ID = "_id";
    public static final String COL_ADDR_NAME = "name";
    public static final String COL_ADDR_PHONE = "phone";
    public static final String COL_ADDR_PROVINCE = "province";
    public static final String COL_ADDR_CITY = "city";
    public static final String COL_ADDR_DISTRICT = "district";
    public static final String COL_ADDR_DETAIL = "detail";
    public static final String COL_ADDR_DEFAULT = "is_default";

    // DDL: CREATE TABLE
    private static final String SQL_CREATE_USER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
                    + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
                    + COL_USER_PASSWORD + " TEXT NOT NULL, "
                    + COL_USER_REMEMBER + " INTEGER DEFAULT 0, "
                    + COL_USER_UPDATED + " TEXT DEFAULT (datetime('now','localtime'))"
                    + ");";

    private static final String SQL_CREATE_CART =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " ("
                    + COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_CART_PRODUCT_ID + " INTEGER NOT NULL UNIQUE, "
                    + COL_CART_NAME + " TEXT, "
                    + COL_CART_PRICE + " REAL, "
                    + COL_CART_QTY + " INTEGER DEFAULT 1"
                    + ");";

    private static final String SQL_CREATE_PROFILE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + " ("
                    + COL_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PROFILE_NICKNAME + " TEXT, "
                    + COL_PROFILE_AVATAR + " INTEGER DEFAULT 0"
                    + ");";

    private static final String SQL_CREATE_ADDRESS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESS + " ("
                    + COL_ADDR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ADDR_NAME + " TEXT, "
                    + COL_ADDR_PHONE + " TEXT, "
                    + COL_ADDR_PROVINCE + " TEXT, "
                    + COL_ADDR_CITY + " TEXT, "
                    + COL_ADDR_DISTRICT + " TEXT, "
                    + COL_ADDR_DETAIL + " TEXT, "
                    + COL_ADDR_DEFAULT + " INTEGER DEFAULT 0"
                    + ");";

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
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_CART);
        db.execSQL(SQL_CREATE_PROFILE);
        db.execSQL(SQL_CREATE_ADDRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "=== DDL: 升级 v" + oldVersion + " → v" + newVersion + " ===");
        if (oldVersion < 2) {
            db.execSQL(SQL_CREATE_CART);
            db.execSQL(SQL_CREATE_PROFILE);
            db.execSQL(SQL_CREATE_ADDRESS);
        }
    }
}
