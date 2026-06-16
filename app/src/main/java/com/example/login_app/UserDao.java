package com.example.login_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 用户 DAO — DML: INSERT / UPDATE / DELETE / SELECT
 */
public class UserDao {

    private static final String TAG = "UserDao";
    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // ==================== INSERT ====================
    public long insertUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_EMAIL, email);
        values.put(DatabaseHelper.COL_USER_PASSWORD, password);
        values.put(DatabaseHelper.COL_USER_REMEMBER, 0);
        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    // ==================== UPDATE ====================
    public int updatePassword(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_PASSWORD, newPassword);
        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_USER_EMAIL + "=?", new String[]{email});
    }

    public int setRemember(String email, boolean remember) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 先清除所有remember
        ContentValues clear = new ContentValues();
        clear.put(DatabaseHelper.COL_USER_REMEMBER, 0);
        db.update(DatabaseHelper.TABLE_USER, clear, null, null);
        // 设置当前用户
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_REMEMBER, remember ? 1 : 0);
        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_USER_EMAIL + "=?", new String[]{email});
    }

    // ==================== DELETE ====================
    public int deleteByEmail(String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_USER,
                DatabaseHelper.COL_USER_EMAIL + "=?", new String[]{email});
    }

    // ==================== SELECT ====================
    public UserInfo getByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, null,
                DatabaseHelper.COL_USER_EMAIL + "=?", new String[]{email},
                null, null, null);
        UserInfo user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    public UserInfo getRememberedUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, null,
                DatabaseHelper.COL_USER_REMEMBER + "=?", new String[]{"1"},
                null, null, null, "1");
        UserInfo user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    private UserInfo cursorToUser(Cursor cursor) {
        UserInfo user = new UserInfo();
        user.id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        user.email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL));
        user.password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD));
        user.remember = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_REMEMBER)) == 1;
        return user;
    }

    public static class UserInfo {
        public int id;
        public String email;
        public String password;
        public boolean remember;
    }
}
