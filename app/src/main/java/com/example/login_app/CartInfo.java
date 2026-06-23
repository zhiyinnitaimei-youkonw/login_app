package com.example.login_app;

import java.io.Serializable;

/**
 * 购物车商品实体类 — 缓存购物车里的商品数据
 *
 * 从CartManager.CartItem内部类提取为独立实体
 * 购物车增删改查的Java端数据载体
 */
public class CartInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private int imageResId;

    public CartInfo() {}

    public CartInfo(int productId, String productName, double productPrice, int quantity, int imageResId) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    // 从Product构造（默认数量1）
    public CartInfo(Product product) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.quantity = 1;
        this.imageResId = product.getImageResId();
    }

    // ==================== Getters & Setters ====================

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    /** 计算小计金额 */
    public double getSubtotal() {
        return productPrice * quantity;
    }
}
