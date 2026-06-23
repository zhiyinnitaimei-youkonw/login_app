package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 购物车Tab — 购物车列表+结算
 */
public class CartFragment extends Fragment {

    private ListView listView;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        listView = view.findViewById(R.id.list_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        // 从DB加载购物车
        CartManager.getInstance().loadFromDb(requireContext());

        adapter = new CartAdapter(requireContext(), CartManager.getInstance().getItems());
        adapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(int productId, int newQty) {
                CartManager.getInstance().updateQuantity(productId, newQty);
                CartManager.getInstance().saveToDb(requireContext());
                adapter.refresh();
                updateTotal();
                syncCartCount();
            }

            @Override
            public void onItemRemoved(int productId) {
                CartManager.getInstance().remove(productId);
                CartManager.getInstance().saveToDb(requireContext());
                adapter.refresh();
                updateTotal();
                syncCartCount();
            }
        });
        listView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(getActivity(), "购物车为空", Toast.LENGTH_SHORT).show();
            } else {
                // ★ 页面跳转: FLAG_ACTIVITY_CLEAR_TOP（关键代码分析-1）
                Intent intent = new Intent(getActivity(), PaymentSuccessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("product_name", "购物车结算");
                intent.putExtra("quantity", CartManager.getInstance().getCount());
                intent.putExtra("total", CartManager.getInstance().getTotal());
                startActivity(intent);

                // 清空购物车 → 更新SQLite + 全局内存
                CartManager.getInstance().clear();
                CartManager.getInstance().saveToDb(requireContext());
                MainApplication app = (MainApplication) requireActivity().getApplication();
                app.resetCartCount();
                adapter.refresh();
                updateTotal();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.refresh();
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getTotal();
        tvTotal.setText("合计: ¥" + String.format("%.2f", total));
    }

    /** 同步购物车数量到全局内存Application */
    private void syncCartCount() {
        MainApplication app = (MainApplication) requireActivity().getApplication();
        app.setCartCount(CartManager.getInstance().getCount());
    }

    /** 刷新购物车数据（外部调用，例如切换到该tab时） */
    public void refreshCart() {
        if (adapter != null) {
            CartManager.getInstance().loadFromDb(requireContext());
            adapter.refresh();
            updateTotal();
        }
    }
}
