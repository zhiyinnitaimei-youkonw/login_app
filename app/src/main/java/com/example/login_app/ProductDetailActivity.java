package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvPrice, tvDesc, tvQty;
    private Button btnMinus, btnPlus, btnAddCart, btnBuyNow;
    private int quantity = 1;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ivImage = findViewById(R.id.iv_detail_image);
        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvDesc = findViewById(R.id.tv_detail_desc);
        tvQty = findViewById(R.id.tv_qty);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 接收商品数据
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "商品数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivImage.setImageResource(product.getImageResId());
        tvName.setText(product.getName());
        tvPrice.setText("¥" + product.getPrice());
        tvDesc.setText(product.getDesc());

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) { quantity--; tvQty.setText(String.valueOf(quantity)); }
        });
        btnPlus.setOnClickListener(v -> {
            quantity++; tvQty.setText(String.valueOf(quantity));
        });

        // 加入购物车
        btnAddCart.setOnClickListener(v -> {
            for (int i = 0; i < quantity; i++) {
                CartManager.getInstance().add(product);
            }
            CartManager.getInstance().saveToDb(this);
            Toast.makeText(this, "已加入购物车 x" + quantity, Toast.LENGTH_SHORT).show();
            finish();
        });

        // 立即购买
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            intent.putExtra("product_name", product.getName());
            intent.putExtra("quantity", quantity);
            intent.putExtra("total", product.getPrice() * quantity);
            startActivity(intent);
            finish();
        });
    }
}
