package com.example.login_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据库帮助器 — 处理购物车表的增删改查
 *
 * 与DatabaseHelper配合：DatabaseHelper负责DDL建表，ShoppingDBHelper负责购物车DML
 * 教学要点：DAO层分离 → DDL与DML职责分开
 */
public class ShoppingDBHelper {

    private static final String TAG = "ShoppingDBHelper";
    private static ShoppingDBHelper instance;
    private DatabaseHelper dbHelper;

    private ShoppingDBHelper(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public static synchronized ShoppingDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ShoppingDBHelper(context);
        }
        return instance;
    }

    // ==================== 增 ====================

    /** 向购物车添加商品（如已存在则数量+1） */
    public long addToCart(CartInfo cartInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 先查是否已存在
        CartInfo existing = queryByProductId(cartInfo.getProductId());
        if (existing != null) {
            // 已存在 → 数量+1
            return updateQuantity(cartInfo.getProductId(), existing.getQuantity() + 1);
        }
        // 不存在 → 插入新记录
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CART_PRODUCT_ID, cartInfo.getProductId());
        cv.put(DatabaseHelper.COL_CART_NAME, cartInfo.getProductName());
        cv.put(DatabaseHelper.COL_CART_PRICE, cartInfo.getProductPrice());
        cv.put(DatabaseHelper.COL_CART_QTY, cartInfo.getQuantity());
        long result = db.insert(DatabaseHelper.TABLE_CART, null, cv);
        Log.d(TAG, "addToCart: productId=" + cartInfo.getProductId() + " result=" + result);
        return result;
    }

    // ==================== 删 ====================

    /** 根据商品ID从购物车删除 */
    public int deleteByProductId(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_CART,
                DatabaseHelper.COL_CART_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)});
        Log.d(TAG, "deleteByProductId: " + productId + " rows=" + rows);
        return rows;
    }

    /** 清空购物车 */
    public int clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_CART, null, null);
        Log.d(TAG, "clearCart: rows=" + rows);
        return rows;
    }

    // ==================== 改 ====================

    /** 更新商品数量 */
    public int updateQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CART_QTY, newQuantity);
        int rows = db.update(DatabaseHelper.TABLE_CART, cv,
                DatabaseHelper.COL_CART_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)});
        Log.d(TAG, "updateQuantity: productId=" + productId + " qty=" + newQuantity);
        return rows;
    }

    // ==================== 查 ====================

    /** 查询单个购物车商品 */
    public CartInfo queryByProductId(int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART, null,
                DatabaseHelper.COL_CART_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)},
                null, null, null);
        CartInfo info = null;
        if (cursor != null && cursor.moveToFirst()) {
            info = cursorToCartInfo(cursor);
            cursor.close();
        }
        return info;
    }

    /** 查询所有购物车商品 */
    public List<CartInfo> queryAll() {
        List<CartInfo> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART,
                null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            list.add(cursorToCartInfo(cursor));
        }
        if (cursor != null) cursor.close();
        Log.d(TAG, "queryAll: " + list.size() + " items");
        return list;
    }

    /** 获取购物车商品总数量 */
    public int getTotalCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_CART_QTY + ") FROM " + DatabaseHelper.TABLE_CART,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    /** 获取购物车商品总价 */
    public double getTotalPrice() {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_CART_PRICE + " * " + DatabaseHelper.COL_CART_QTY
                        + ") FROM " + DatabaseHelper.TABLE_CART,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // ==================== 辅助 ====================

    private CartInfo cursorToCartInfo(Cursor cursor) {
        CartInfo info = new CartInfo();
        info.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_PRODUCT_ID)));
        info.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_NAME)));
        info.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_PRICE)));
        info.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_QTY)));
        return info;
    }
}
