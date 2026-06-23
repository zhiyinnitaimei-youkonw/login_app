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
            }

            @Override
            public void onItemRemoved(int productId) {
                CartManager.getInstance().remove(productId);
                CartManager.getInstance().saveToDb(requireContext());
                adapter.refresh();
                updateTotal();
            }
        });
        listView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(getActivity(), "购物车为空", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getActivity(), PaymentSuccessActivity.class);
                intent.putExtra("product_name", "购物车结算");
                intent.putExtra("quantity", CartManager.getInstance().getCount());
                intent.putExtra("total", CartManager.getInstance().getTotal());
                startActivity(intent);
                CartManager.getInstance().clear();
                CartManager.getInstance().saveToDb(requireContext());
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

    /** 刷新购物车数据（外部调用，例如切换到该tab时） */
    public void refreshCart() {
        if (adapter != null) {
            CartManager.getInstance().loadFromDb(requireContext());
            adapter.refresh();
            updateTotal();
        }
    }
}
