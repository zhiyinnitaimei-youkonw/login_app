package com.example.login_app;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String desc;
    private double price;
    private int imageResId;

    public Product(int id, String name, String desc, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
