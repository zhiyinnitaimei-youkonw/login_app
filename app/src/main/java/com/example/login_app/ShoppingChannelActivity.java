package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品主页 — 使用GridLayout实现商品网格展示
 *
 * 教学要点（关键代码分析-4）:
 *   步骤一: 布局文件中的GridLayout节点
 *   步骤二: 统一的商品信息布局 item_goods.xml
 *   步骤三: 利用LayoutInflater获取item_goods.xml根视图，
 *          再从根视图中依据控件ID分别取出网格单元的各控件对象
 *
 * 与ProductListActivity的区别:
 *   - ProductListActivity: ViewPager2 + Fragment + GridView（列表模式）
 *   - ShoppingChannelActivity: GridLayout + 手动inflate（教学演示模式）
 */
public class ShoppingChannelActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingChannel";
    private static final int GRID_COLUMNS = 2; // 2列网格

    private GridLayout gridLayout;
    private TextView tvCartBadge;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_grid);

        // 初始化SQLite数据
        CartManager.getInstance().loadFromDb(this);

        // 绑定公共标题栏控件
        View titleBar = findViewById(R.id.title_bar_include);
        titleBar.findViewById(R.id.iv_back).setVisibility(View.GONE);
        ((TextView) titleBar.findViewById(R.id.tv_title_bar)).setText("手机商场");

        tvCartBadge = titleBar.findViewById(R.id.tv_cart_badge);
        titleBar.findViewById(R.id.btn_title_cart).setOnClickListener(v -> {
            // 跳转购物车页面（页面跳转 + FLAG_ACTIVITY_CLEAR_TOP）
            Intent intent = new Intent(this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // 获取GridLayout
        gridLayout = findViewById(R.id.grid_products);
        gridLayout.setColumnCount(GRID_COLUMNS);

        // 初始化商品数据
        initProducts();

        // ★ 关键代码: GridLayout手动复用item_goods.xml
        buildProductGrid();

        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    /**
     * ★ GridLayout商品网格构建（教学要点）
     * 步骤三: 利用LayoutInflater获取item_goods.xml的根视图
     *        再从根视图中依据控件ID分别取出各控件对象
     */
    private void buildProductGrid() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < products.size(); i++) {
            final Product product = products.get(i);

            // 步骤三-①: 获取布局文件item_goods.xml的根视图
            View itemView = inflater.inflate(R.layout.item_goods, gridLayout, false);

            // 步骤三-②: 从根视图中依据控件ID分别取出各控件对象
            ImageView ivImage = itemView.findViewById(R.id.iv_goods_image);
            TextView tvName = itemView.findViewById(R.id.tv_goods_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_goods_price);
            Button btnAdd = itemView.findViewById(R.id.btn_goods_add);

            // 绑定数据
            ivImage.setImageResource(product.getImageResId());
            tvName.setText(product.getName());
            tvPrice.setText("¥" + product.getPrice());

            // 添加到购物车
            btnAdd.setOnClickListener(v -> {
                CartManager.getInstance().add(product);
                CartManager.getInstance().saveToDb(ShoppingChannelActivity.this);

                // 更新全局内存中的购物车数量
                MainApplication app = (MainApplication) getApplication();
                app.incrementCartCount(1);

                updateCartBadge();
                Toast.makeText(ShoppingChannelActivity.this,
                        "已添加 " + product.getName(), Toast.LENGTH_SHORT).show();
            });

            // 点击商品 → 跳转详情页
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ShoppingChannelActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            });

            // 步骤三-③: 添加到GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % GRID_COLUMNS, 1f);
            params.rowSpec = GridLayout.spec(i / GRID_COLUMNS);
            params.setMargins(4, 4, 4, 4);
            itemView.setLayoutParams(params);

            gridLayout.addView(itemView);
        }
    }

    /** 更新购物车角标（从全局内存读取，不查库） */
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
