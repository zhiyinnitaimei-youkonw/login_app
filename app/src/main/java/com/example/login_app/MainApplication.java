package com.example.login_app;

import android.app.Application;

/**
 * 全局Application — 保存购物车商品数量等全局内存变量
 * 避免每次更新角标都查询数据库（内存读取远快于SQLite）
 *
 * 使用方式: MainApplication app = (MainApplication) getApplication();
 *           int count = app.getCartCount();
 */
public class MainApplication extends Application {

    /** 购物车商品总数量 — 全局内存变量 */
    private int cartCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // Application初始化时从SQLite恢复购物车数据
        CartManager.getInstance().loadFromDb(this);
        cartCount = CartManager.getInstance().getCount();
    }

    /** 获取购物车商品总数（全局内存读取，无需查库） */
    public int getCartCount() {
        return cartCount;
    }

    /** 设置购物车商品总数 */
    public void setCartCount(int count) {
        this.cartCount = count;
    }

    /** 添加商品后增量更新（比setCartCount性能更好） */
    public void incrementCartCount(int delta) {
        this.cartCount += delta;
        if (this.cartCount < 0) this.cartCount = 0;
    }

    /** 清空购物车计数 */
    public void resetCartCount() {
        this.cartCount = 0;
    }

    /** 从内存刷新计数（同步SQLite数据后调用） */
    public void refreshCartCount() {
        CartManager.getInstance().loadFromDb(this);
        this.cartCount = CartManager.getInstance().getCount();
    }
}
