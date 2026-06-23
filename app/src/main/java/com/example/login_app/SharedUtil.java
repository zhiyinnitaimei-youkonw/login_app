package com.example.login_app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类 — 用于持久化少量标志位
 *
 * 核心用途: 记录"是否首次访问网络图片"标志位
 * 教学要点: SP适合存储单个或少量键值对，与SQLite互为补充
 *
 * 使用方式: SharedUtil.getInstance(context).isFirstVisit()
 *           SharedUtil.getInstance(context).setFirstVisit(false)
 */
public class SharedUtil {

    private static final String SP_NAME = "shopping_config";
    private static final String KEY_FIRST_VISIT_IMAGE = "first_visit_image";

    private static SharedUtil instance;
    private SharedPreferences sp;

    private SharedUtil(Context context) {
        sp = context.getApplicationContext()
                .getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /** 单例获取实例 */
    public static synchronized SharedUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedUtil(context);
        }
        return instance;
    }

    // ==================== 首次访问标志 ====================

    /** 判断是否首次访问网络图片（默认true） */
    public boolean isFirstVisitImage() {
        return sp.getBoolean(KEY_FIRST_VISIT_IMAGE, true);
    }

    /** 设置首次访问网络图片标志 */
    public void setFirstVisitImage(boolean firstVisit) {
        sp.edit().putBoolean(KEY_FIRST_VISIT_IMAGE, firstVisit).apply();
    }

    // ==================== 通用读写方法 ====================

    /** 写入字符串 */
    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    /** 读取字符串 */
    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    /** 写入布尔值 */
    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    /** 读取布尔值 */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    /** 写入整型 */
    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    /** 读取整型 */
    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    /** 清除所有数据 */
    public void clear() {
        sp.edit().clear().apply();
    }
}
