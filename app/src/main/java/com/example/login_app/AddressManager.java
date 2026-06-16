package com.example.login_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddressManager {
    private static AddressManager instance;
    private List<Address> addresses = new ArrayList<>();
    private AtomicInteger idGen = new AtomicInteger(1000);

    public static synchronized AddressManager getInstance() {
        if (instance == null) instance = new AddressManager();
        return instance;
    }

    public List<Address> getAddresses() { return new ArrayList<>(addresses); }

    public Address getById(int id) {
        for (Address a : addresses) if (a.getId() == id) return a;
        return null;
    }

    public void add(Address addr) { addresses.add(addr); }

    public void update(int id, String name, String phone, String province,
                       String city, String district, String detail) {
        Address a = getById(id);
        if (a != null) {
            a.setName(name); a.setPhone(phone);
            a.setProvince(province); a.setCity(city);
            a.setDistrict(district); a.setDetail(detail);
        }
    }

    public void remove(int id) {
        Address target = null;
        for (Address a : addresses) if (a.getId() == id) { target = a; break; }
        if (target != null) addresses.remove(target);
    }

    public void setDefault(int id) {
        for (Address a : addresses) a.setDefault(a.getId() == id);
    }

    public int nextId() { return idGen.getAndIncrement(); }

    // ============== SQLite 持久化 ==============

    public void saveToDb(Context context) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ADDRESS, null, null);
        for (Address a : addresses) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COL_ADDR_ID, a.getId());
            cv.put(DatabaseHelper.COL_ADDR_NAME, a.getName());
            cv.put(DatabaseHelper.COL_ADDR_PHONE, a.getPhone());
            cv.put(DatabaseHelper.COL_ADDR_PROVINCE, a.getProvince());
            cv.put(DatabaseHelper.COL_ADDR_CITY, a.getCity());
            cv.put(DatabaseHelper.COL_ADDR_DISTRICT, a.getDistrict());
            cv.put(DatabaseHelper.COL_ADDR_DETAIL, a.getDetail());
            cv.put(DatabaseHelper.COL_ADDR_DEFAULT, a.isDefault() ? 1 : 0);
            db.insert(DatabaseHelper.TABLE_ADDRESS, null, cv);
        }
    }

    public void loadFromDb(Context context) {
        addresses.clear();
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ADDRESS, null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_PHONE));
            String province = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_PROVINCE));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_CITY));
            String district = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_DISTRICT));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_DETAIL));
            boolean isDefault = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ADDR_DEFAULT)) == 1;
            addresses.add(new Address(id, name, phone, province, city, district, detail, isDefault));
            if (id >= idGen.get()) idGen.set(id + 1);
        }
        if (cursor != null) cursor.close();
        // 首次加载时添加示例地址
        if (addresses.isEmpty()) {
            addresses.add(new Address(idGen.getAndIncrement(), "张三", "13800138000",
                    "浙江省", "杭州市", "余杭区", "文一西路969号", true));
            saveToDb(context);
        }
    }
}
