package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView tvCartBadge;
    private ProductAdapter adapter;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);

        gridView = findViewById(R.id.grid_products);
        tvCartBadge = findViewById(R.id.tv_cart_badge);

        // 从SQLite恢复购物车
        CartManager.getInstance().loadFromDb(this);

        initProducts();
        adapter = new ProductAdapter(this, products);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Product p = products.get(position);
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product", p);
            startActivity(intent);
        });

        findViewById(R.id.btn_cart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        findViewById(R.id.btn_user).setOnClickListener(v ->
                startActivity(new Intent(this, UserProfileActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadge();
    }

    private void updateBadge() {
        int count = CartManager.getInstance().getCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void initProducts() {
        products.add(new Product(1, "无线蓝牙耳机", "降噪长续航 高品质音质", 199.00, R.drawable.ic_product));
        products.add(new Product(2, "运动跑鞋", "透气减震 轻便舒适", 299.00, R.drawable.ic_product));
        products.add(new Product(3, "双肩背包", "大容量 防水耐磨", 159.00, R.drawable.ic_product));
        products.add(new Product(4, "保温杯", "316不锈钢 500ml", 89.00, R.drawable.ic_product));
        products.add(new Product(5, "机械键盘", "青轴 RGB背光 87键", 349.00, R.drawable.ic_product));
        products.add(new Product(6, "充电宝", "20000mAh 快充", 129.00, R.drawable.ic_product));
        products.add(new Product(7, "遮阳帽", "防晒透气 可折叠", 49.00, R.drawable.ic_product));
        products.add(new Product(8, "T恤", "纯棉 宽松版型 多色可选", 79.00, R.drawable.ic_product));
    }
}
