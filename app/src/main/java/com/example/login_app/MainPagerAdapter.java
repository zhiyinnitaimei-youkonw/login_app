package com.example.login_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * ViewPager2适配器 — 管理底部导航的3个Tab页面
 * 商城 | 购物车 | 我的
 */
public class MainPagerAdapter extends FragmentStateAdapter {

    private final ShopFragment shopFragment;
    private final CartFragment cartFragment;
    private final ProfileFragment profileFragment;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        shopFragment = new ShopFragment();
        cartFragment = new CartFragment();
        profileFragment = new ProfileFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return shopFragment;
            case 1: return cartFragment;
            case 2: return profileFragment;
            default: return new ShopFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public CartFragment getCartFragment() {
        return cartFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }
}
