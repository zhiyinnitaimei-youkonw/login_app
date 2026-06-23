package com.example.login_app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 主页面 — 底部导航 + ViewPager2滑动切换
 * 3个Tab: 商城 | 购物车 | 我的
 * 支持点击Tab切换和左右滑动切换
 */
public class ProductListActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView tvCartBadge;
    private MainPagerAdapter adapter;

    private static final String[] TAB_TITLES = {"商城", "购物车", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);

        tvCartBadge = findViewById(R.id.tv_cart_badge);

        // 从SQLite恢复购物车
        CartManager.getInstance().loadFromDb(this);

        // 配置ViewPager2
        viewPager = findViewById(R.id.view_pager);
        adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);
        // 保留3个页面在内存中，避免频繁重建
        viewPager.setOffscreenPageLimit(2);

        // 配置TabLayout（底部导航）
        tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            // 使用系统图标作为tab图标
            switch (position) {
                case 0:
                    tab.setIcon(android.R.drawable.ic_menu_gallery);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_cart);
                    break;
                case 2:
                    tab.setIcon(android.R.drawable.ic_menu_myplaces);
                    break;
            }
        }).attach();

        // ViewPager页面切换监听 — 切换到购物车时刷新数据
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1 && adapter.getCartFragment() != null) {
                    adapter.getCartFragment().refreshCart();
                }
            }
        });

        // 顶栏购物车图标点击 → 跳转到购物车Tab
        findViewById(R.id.btn_top_cart).setOnClickListener(v -> viewPager.setCurrentItem(1, true));

        updateBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadge();
        // 刷新购物车Tab数据
        if (adapter != null && adapter.getCartFragment() != null) {
            adapter.getCartFragment().refreshCart();
        }
    }

    /** 更新购物车角标 */
    private void updateBadge() {
        int count = CartManager.getInstance().getCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }
}
