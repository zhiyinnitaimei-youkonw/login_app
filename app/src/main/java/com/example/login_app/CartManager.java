package com.example.login_app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Integer, CartItem> items = new LinkedHashMap<>();

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void add(Product product) {
        CartItem item = items.get(product.getId());
        if (item != null) {
            item.quantity++;
        } else {
            items.put(product.getId(), new CartItem(product, 1));
        }
    }

    public void remove(int productId) {
        items.remove(productId);
    }

    public void updateQuantity(int productId, int qty) {
        CartItem item = items.get(productId);
        if (item != null) {
            item.quantity = qty;
            if (item.quantity <= 0) {
                items.remove(productId);
            }
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public int getCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.quantity;
        }
        return count;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.product.getPrice() * item.quantity;
        }
        return total;
    }

    public void clear() {
        items.clear();
    }

    public static class CartItem {
        public Product product;
        public int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}
