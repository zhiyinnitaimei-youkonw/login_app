package com.example.login_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.list_cart);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        adapter = new CartAdapter();
        listView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCount() == 0) {
                Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "合计: ¥" +
                        String.format("%.2f", CartManager.getInstance().getTotal()) +
                        "，结算功能开发中...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        double total = CartManager.getInstance().getTotal();
        tvTotal.setText("合计: ¥" + String.format("%.2f", total));
    }

    private class CartAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CartManager.getInstance().getItems().size();
        }

        @Override
        public Object getItem(int position) {
            return CartManager.getInstance().getItems().get(position);
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CartActivity.this)
                        .inflate(R.layout.item_cart, parent, false);
            }

            final CartManager.CartItem item = CartManager.getInstance().getItems().get(position);
            final Product p = item.product;

            ((ImageView) convertView.findViewById(R.id.iv_product)).setImageResource(p.getImageResId());
            ((TextView) convertView.findViewById(R.id.tv_name)).setText(p.getName());
            ((TextView) convertView.findViewById(R.id.tv_price)).setText("¥" + p.getPrice());
            TextView tvQty = convertView.findViewById(R.id.tv_qty);
            tvQty.setText(String.valueOf(item.quantity));

            convertView.findViewById(R.id.btn_minus).setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(p.getId(), item.quantity - 1);
                notifyDataSetChanged();
                tvTotal.setText("合计: ¥" + String.format("%.2f", CartManager.getInstance().getTotal()));
            });

            convertView.findViewById(R.id.btn_plus).setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(p.getId(), item.quantity + 1);
                notifyDataSetChanged();
                tvTotal.setText("合计: ¥" + String.format("%.2f", CartManager.getInstance().getTotal()));
            });

            convertView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
                CartManager.getInstance().remove(p.getId());
                notifyDataSetChanged();
                tvTotal.setText("合计: ¥" + String.format("%.2f", CartManager.getInstance().getTotal()));
            });

            return convertView;
        }
    }
}
