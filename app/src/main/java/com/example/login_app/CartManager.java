package com.example.login_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Integer, CartItem> items = new LinkedHashMap<>();

    public static synchronized CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void add(Product product) {
        CartItem item = items.get(product.getId());
        if (item != null) item.quantity++;
        else items.put(product.getId(), new CartItem(product, 1));
    }

    public void remove(int productId) { items.remove(productId); }

    public void updateQuantity(int productId, int qty) {
        CartItem item = items.get(productId);
        if (item != null) {
            item.quantity = qty;
            if (item.quantity <= 0) items.remove(productId);
        }
    }

    public List<CartItem> getItems() { return new ArrayList<>(items.values()); }

    public int getCount() {
        int count = 0;
        for (CartItem item : items.values()) count += item.quantity;
        return count;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items.values()) total += item.product.getPrice() * item.quantity;
        return total;
    }

    public void clear() { items.clear(); }

    // ============== SQLite 持久化 ==============

    public void saveToDb(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CART, null, null);
        for (CartItem item : items.values()) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COL_CART_PRODUCT_ID, item.product.getId());
            cv.put(DatabaseHelper.COL_CART_NAME, item.product.getName());
            cv.put(DatabaseHelper.COL_CART_PRICE, item.product.getPrice());
            cv.put(DatabaseHelper.COL_CART_QTY, item.quantity);
            db.insert(DatabaseHelper.TABLE_CART, null, cv);
        }
    }

    public void loadFromDb(Context context) {
        items.clear();
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CART, null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            int pid = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_PRODUCT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_PRICE));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CART_QTY));
            Product p = new Product(pid, name, "", price, R.drawable.ic_product);
            items.put(pid, new CartItem(p, qty));
        }
        if (cursor != null) cursor.close();
    }

    public static class CartItem {
        public Product product;
        public int quantity;
        CartItem(Product product, int quantity) { this.product = product; this.quantity = quantity; }
    }
}
