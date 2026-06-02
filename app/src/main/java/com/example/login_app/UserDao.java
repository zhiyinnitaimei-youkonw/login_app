package com.example.login_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 用户数据访问对象 — DML（数据操作语言）
 * INSERT / UPDATE / DELETE / SELECT
 */
public class UserDao {

    private static final String TAG = "UserDao";
    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // ==================== DML: INSERT ====================

    /**
     * INSERT INTO user (phone, password, remember) VALUES (?, ?, ?);
     * 如果手机号已存在则改为 UPDATE（REPLACE 语义）
     */
    public long insertOrUpdate(String phone, String password, boolean remember) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PHONE, phone);
        values.put(DatabaseHelper.COL_PASSWORD, password);
        values.put(DatabaseHelper.COL_REMEMBER, remember ? 1 : 0);

        Log.d(TAG, "=== DML: INSERT OR UPDATE ===");
        Log.d(TAG, "INSERT INTO " + DatabaseHelper.TABLE_USER
                + " (phone, password, remember) VALUES ('" + phone + "', '****', " + (remember ? 1 : 0) + ")");
        Log.d(TAG, "ON CONFLICT(phone) DO UPDATE SET password=excluded.password");

        // 先查是否存在
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_ID},
                DatabaseHelper.COL_PHONE + "=?",
                new String[]{phone}, null, null, null);

        long result;
        if (cursor != null && cursor.moveToFirst()) {
            // DML: UPDATE — 手机号已存在，更新密码
            cursor.close();
            result = db.update(DatabaseHelper.TABLE_USER, values,
                    DatabaseHelper.COL_PHONE + "=?", new String[]{phone});
            Log.d(TAG, "UPDATE 影响行数: " + result);
        } else {
            // DML: INSERT — 新增用户
            if (cursor != null) cursor.close();
            result = db.insert(DatabaseHelper.TABLE_USER, null, values);
            Log.d(TAG, "INSERT 新行ID: " + result);
        }
        return result;
    }

    // ==================== DML: UPDATE ====================

    /**
     * UPDATE user SET remember = ? WHERE phone = ?;
     */
    public int updateRemember(String phone, boolean remember) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_REMEMBER, remember ? 1 : 0);

        Log.d(TAG, "=== DML: UPDATE ===");
        Log.d(TAG, "UPDATE " + DatabaseHelper.TABLE_USER
                + " SET remember=" + (remember ? 1 : 0)
                + " WHERE phone='" + phone + "';");

        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_PHONE + "=?", new String[]{phone});
    }

    /**
     * UPDATE user SET password = ? WHERE phone = ?;
     */
    public int updatePassword(String phone, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PASSWORD, newPassword);

        Log.d(TAG, "=== DML: UPDATE PASSWORD ===");
        Log.d(TAG, "UPDATE " + DatabaseHelper.TABLE_USER
                + " SET password='****' WHERE phone='" + phone + "';");

        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_PHONE + "=?", new String[]{phone});
    }

    // ==================== DML: DELETE ====================

    /**
     * DELETE FROM user WHERE phone = ?;
     */
    public int deleteByPhone(String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(TAG, "=== DML: DELETE ===");
        Log.d(TAG, "DELETE FROM " + DatabaseHelper.TABLE_USER
                + " WHERE phone='" + phone + "';");

        return db.delete(DatabaseHelper.TABLE_USER,
                DatabaseHelper.COL_PHONE + "=?", new String[]{phone});
    }

    /**
     * DELETE FROM user; （清空表）
     */
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "=== DML: DELETE ALL ===");
        Log.d(TAG, "DELETE FROM " + DatabaseHelper.TABLE_USER + ";");
        db.delete(DatabaseHelper.TABLE_USER, null, null);
    }

    // ==================== DML: SELECT / QUERY ====================

    /**
     * SELECT * FROM user WHERE remember = 1 LIMIT 1;
     * 获取记住密码的用户
     */
    public UserInfo getRememberedUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Log.d(TAG, "=== DML: SELECT ===");
        Log.d(TAG, "SELECT * FROM " + DatabaseHelper.TABLE_USER
                + " WHERE remember=1 LIMIT 1;");

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, null,
                DatabaseHelper.COL_REMEMBER + "=?",
                new String[]{"1"}, null, null,
                DatabaseHelper.COL_UPDATED_AT + " DESC", "1");

        UserInfo user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            Log.d(TAG, "查询结果: phone=" + user.phone + ", remember=" + user.remember);
            cursor.close();
        }
        return user;
    }

    /**
     * SELECT * FROM user WHERE phone = ?;
     */
    public UserInfo getByPhone(String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Log.d(TAG, "=== DML: SELECT BY PHONE ===");
        Log.d(TAG, "SELECT * FROM " + DatabaseHelper.TABLE_USER
                + " WHERE phone='" + phone + "';");

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, null,
                DatabaseHelper.COL_PHONE + "=?",
                new String[]{phone}, null, null, null);

        UserInfo user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    /**
     * SELECT COUNT(*) FROM user;
     */
    public int getUserCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USER;
        Log.d(TAG, "=== DML: COUNT ===");
        Log.d(TAG, sql + ";");

        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        Log.d(TAG, "用户总数: " + count);
        return count;
    }

    // ==================== Cursor → 对象映射 ====================

    private UserInfo cursorToUser(Cursor cursor) {
        UserInfo user = new UserInfo();
        user.id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        user.phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE));
        user.password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD));
        user.remember = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REMEMBER)) == 1;
        user.updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UPDATED_AT));
        return user;
    }

    /**
     * 用户信息实体类（数据持久化对象）
     */
    public static class UserInfo {
        public int id;
        public String phone;
        public String password;
        public boolean remember;
        public String updatedAt;
    }
}
