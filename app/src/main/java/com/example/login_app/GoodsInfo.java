package com.example.login_app;

import java.io.Serializable;

/**
 * 商品信息实体类 — 用于商品页的缓存与展示数据
 *
 * 扩展了Product类，增加了图片缓存相关字段（imageUrl, localImagePath）
 * 用于"存储卡→网络下载"二级图片缓存机制
 */
public class GoodsInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String desc;
    private double price;
    private int imageResId;
    /** 网络图片URL（用于图片下载缓存） */
    private String imageUrl;
    /** 图片在本地存储卡的路径 */
    private String localImagePath;

    public GoodsInfo() {}

    public GoodsInfo(int id, String name, String desc, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageResId = imageResId;
    }

    // 兼容从Product构造
    public GoodsInfo(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.desc = product.getDesc();
        this.price = product.getPrice();
        this.imageResId = product.getImageResId();
    }

    // ==================== Getters & Setters ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocalImagePath() { return localImagePath; }
    public void setLocalImagePath(String localImagePath) { this.localImagePath = localImagePath; }

    /** 是否已缓存本地图片 */
    public boolean hasLocalImage() {
        return localImagePath != null && !localImagePath.isEmpty();
    }
}
