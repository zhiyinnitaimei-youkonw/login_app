package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        listView = findViewById(R.id.list_cart);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        adapter = new CartAdapter(this, CartManager.getInstance().getItems());
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(int productId, int newQty) {
                CartManager.getInstance().updateQuantity(productId, newQty);
                CartManager.getInstance().saveToDb(CartActivity.this);
                adapter.refresh();
                updateTotal();
            }

            @Override
            public void onItemRemoved(int productId) {
                CartManager.getInstance().remove(productId);
                CartManager.getInstance().saveToDb(CartActivity.this);
                adapter.refresh();
                updateTotal();
            }
        });
        listView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                intent.putExtra("product_name", "购物车结算");
                intent.putExtra("quantity", CartManager.getInstance().getCount());
                intent.putExtra("total", CartManager.getInstance().getTotal());
                startActivity(intent);
                CartManager.getInstance().clear();
                CartManager.getInstance().saveToDb(CartActivity.this);
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
}
