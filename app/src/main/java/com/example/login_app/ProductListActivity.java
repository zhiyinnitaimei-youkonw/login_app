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

        // ★ 公共标题栏（include引入）— 标题设为"商城"
        View titleBar = findViewById(R.id.title_bar_include);
        titleBar.findViewById(R.id.iv_back).setVisibility(View.GONE);
        tvCartBadge = titleBar.findViewById(R.id.tv_cart_badge);
        titleBar.findViewById(R.id.btn_title_cart).setOnClickListener(v ->
                viewPager.setCurrentItem(1, true));

        // 从SQLite恢复购物车 → 更新全局Application内存
        CartManager.getInstance().loadFromDb(this);
        MainApplication app = (MainApplication) getApplication();
        app.setCartCount(CartManager.getInstance().getCount());

        // 配置ViewPager2
        viewPager = findViewById(R.id.view_pager);
        adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        // 配置TabLayout（底部导航）
        tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
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

        updateBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从全局内存读取购物车数量（不查库，性能优化）
        updateBadge();
        if (adapter != null && adapter.getCartFragment() != null) {
            adapter.getCartFragment().refreshCart();
        }
    }

    /** 更新购物车角标 — 从全局内存Application读取 */
    private void updateBadge() {
        MainApplication app = (MainApplication) getApplication();
        int count = app.getCartCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }
}
