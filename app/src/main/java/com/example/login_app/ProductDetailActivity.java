package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 商品详情页 — LinearLayout垂直排列 + 图片缓存 + 购物车角标实时更新
 *
 * 教学要点:
 *   ① 图片缓存（ImageCacheManager: 存储卡→网络下载二级缓存）
 *   ② 购物车图标+角标（RelativeLayout右上角定位）
 *   ③ 商品数量实时更新（全局内存Application）
 */
public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvPrice, tvDesc, tvQty;
    private Button btnMinus, btnPlus, btnAddCart, btnBuyNow;
    private TextView tvCartBadge;
    private int quantity = 1;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // ★ 公共标题栏（include引入）
        View titleBar = findViewById(R.id.title_bar_include);
        titleBar.findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ((TextView) titleBar.findViewById(R.id.tv_title_bar)).setText("商品详情");
        tvCartBadge = titleBar.findViewById(R.id.tv_cart_badge);
        titleBar.findViewById(R.id.btn_title_cart).setOnClickListener(v -> {
            // ★ 页面跳转: FLAG_ACTIVITY_CLEAR_TOP
            Intent intent = new Intent(this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        ivImage = findViewById(R.id.iv_detail_image);
        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvDesc = findViewById(R.id.tv_detail_desc);
        tvQty = findViewById(R.id.tv_qty);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        // 接收商品数据
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "商品数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ★ 图片加载 — 使用二级缓存（存储卡→网络下载）
        loadProductImage();

        tvName.setText(product.getName());
        tvPrice.setText("¥" + product.getPrice());
        tvDesc.setText(product.getDesc());

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) { quantity--; tvQty.setText(String.valueOf(quantity)); }
        });
        btnPlus.setOnClickListener(v -> {
            quantity++; tvQty.setText(String.valueOf(quantity));
        });

        // 加入购物车 → 更新SQLite + 全局内存
        btnAddCart.setOnClickListener(v -> {
            for (int i = 0; i < quantity; i++) {
                CartManager.getInstance().add(product);
            }
            CartManager.getInstance().saveToDb(this);

            // 更新全局内存中的购物车数量
            MainApplication app = (MainApplication) getApplication();
            app.incrementCartCount(quantity);

            updateCartBadge();
            Toast.makeText(this, "已加入购物车 x" + quantity, Toast.LENGTH_SHORT).show();
            finish();
        });

        // 立即购买
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentSuccessActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("product_name", product.getName());
            intent.putExtra("quantity", quantity);
            intent.putExtra("total", product.getPrice() * quantity);
            startActivity(intent);
            finish();
        });

        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    /** 从全局内存Application读取购物车数量，更新角标 */
    private void updateCartBadge() {
        MainApplication app = (MainApplication) getApplication();
        int count = app.getCartCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    /** ★ 商品图片加载 — 二级缓存机制 */
    private void loadProductImage() {
        // 先使用本地资源占位
        ivImage.setImageResource(product.getImageResId());

        // 如果Product有networkImageUrl，则尝试二级缓存加载
        // 此处预留接口，实际网络图片URL可从Product扩展获取
        SharedUtil sharedUtil = SharedUtil.getInstance(this);

        // 检查是否首次访问网络图片
        if (sharedUtil.isFirstVisitImage()) {
            // 首次访问 → 尝试从网络下载（教学演示）
            // ImageCacheManager会处理: 网络下载 → 存存储卡 → 更新SP标志
            // 由于当前商品使用本地drawable，此处为教学预留代码
        }
    }
}
