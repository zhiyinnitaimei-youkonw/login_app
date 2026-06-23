package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 购物车页面 — LinearLayout垂直排列 + SQLite持久化 + 全局内存计数
 *
 * 页面跳转（关键代码分析-1）: 购物车活动跳转指定 FLAG_ACTIVITY_CLEAR_TOP
 */
public class CartActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        // ★ 公共标题栏（include引入）— 标题设为"购物车"
        View titleBar = findViewById(R.id.title_bar_include);
        titleBar.findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ((TextView) titleBar.findViewById(R.id.tv_title_bar)).setText("购物车");
        // 购物车页面角标隐藏（已在页面内展示全部商品）
        titleBar.findViewById(R.id.tv_cart_badge).setVisibility(View.GONE);

        listView = findViewById(R.id.list_cart);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        // 从SQLite恢复购物车数据
        CartManager.getInstance().loadFromDb(this);

        adapter = new CartAdapter(this, CartManager.getInstance().getItems());
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(int productId, int newQty) {
                CartManager.getInstance().updateQuantity(productId, newQty);
                CartManager.getInstance().saveToDb(CartActivity.this);
                adapter.refresh();
                updateTotal();
                syncCartCount();
            }

            @Override
            public void onItemRemoved(int productId) {
                CartManager.getInstance().remove(productId);
                CartManager.getInstance().saveToDb(CartActivity.this);
                adapter.refresh();
                updateTotal();
                syncCartCount();
            }
        });
        listView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            } else {
                // ★ 页面跳转: FLAG_ACTIVITY_CLEAR_TOP（关键代码分析-1）
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("product_name", "购物车结算");
                intent.putExtra("quantity", CartManager.getInstance().getCount());
                intent.putExtra("total", CartManager.getInstance().getTotal());
                startActivity(intent);

                // 清空购物车 → 更新SQLite + 全局内存
                CartManager.getInstance().clear();
                CartManager.getInstance().saveToDb(CartActivity.this);
                MainApplication app = (MainApplication) getApplication();
                app.resetCartCount();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getTotal();
        tvTotal.setText("合计: ¥" + String.format("%.2f", total));
    }

    /** 同步购物车数量到全局内存 */
    private void syncCartCount() {
        MainApplication app = (MainApplication) getApplication();
        app.setCartCount(CartManager.getInstance().getCount());
    }
}
