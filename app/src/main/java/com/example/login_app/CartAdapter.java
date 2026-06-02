package com.example.login_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<CartManager.CartItem> items;
    private LayoutInflater inflater;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged(int productId, int newQty);
        void onItemRemoved(int productId);
    }

    public CartAdapter(Context context, List<CartManager.CartItem> items) {
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public void refresh() {
        this.items = CartManager.getInstance().getItems();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public CartManager.CartItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder();
            holder.ivProduct = convertView.findViewById(R.id.iv_product);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_price);
            holder.tvQty = convertView.findViewById(R.id.tv_qty);
            holder.btnMinus = convertView.findViewById(R.id.btn_minus);
            holder.btnPlus = convertView.findViewById(R.id.btn_plus);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CartManager.CartItem item = getItem(position);
        final Product product = item.product;

        holder.ivProduct.setImageResource(product.getImageResId());
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("¥" + product.getPrice());
        holder.tvQty.setText(String.valueOf(item.quantity));

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(product.getId(), item.quantity - 1);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(product.getId(), item.quantity + 1);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(product.getId());
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProduct;
        TextView tvName;
        TextView tvPrice;
        TextView tvQty;
        View btnMinus;
        View btnPlus;
        View btnDelete;
    }
}
