package com.example.login_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/**
 * 收货地址列表适配器 — ViewHolder模式
 * 从AddressListActivity内部分离，便于复用和维护
 */
public class AddressAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onDefaultChanged();
    }

    public AddressAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnAddressActionListener(OnAddressActionListener listener) {
        this.listener = listener;
    }

    private List<Address> getAddresses() {
        return AddressManager.getInstance().getAddresses();
    }

    @Override
    public int getCount() {
        return getAddresses().size();
    }

    @Override
    public Address getItem(int position) {
        return getAddresses().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_address, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvAddress = convertView.findViewById(R.id.tv_address);
            holder.tvTag = convertView.findViewById(R.id.tv_tag);
            holder.lytDefault = convertView.findViewById(R.id.lyt_default);
            holder.ivEdit = convertView.findViewById(R.id.iv_edit);
            holder.ivDelete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Address a = getItem(position);

        // 显示姓名+电话
        holder.tvName.setText(a.getName() + "    " + a.getPhone());
        // 显示完整地址
        holder.tvAddress.setText(a.getFullAddress());
        // 默认标签
        holder.tvTag.setVisibility(a.isDefault() ? View.VISIBLE : View.GONE);

        // 设为默认
        holder.lytDefault.setOnClickListener(v -> {
            AddressManager.getInstance().setDefault(a.getId());
            AddressManager.getInstance().saveToDb(context);
            notifyDataSetChanged();
            if (listener != null) listener.onDefaultChanged();
        });

        // 编辑地址
        holder.ivEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, AddressEditActivity.class);
            i.putExtra("address_id", a.getId());
            context.startActivity(i);
        });

        // 删除地址（带确认对话框）
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("删除地址")
                    .setMessage("确定要删除该收货地址吗？此操作不可撤销。")
                    .setPositiveButton("确定删除", (d, w) -> {
                        AddressManager.getInstance().remove(a.getId());
                        AddressManager.getInstance().saveToDb(context);
                        notifyDataSetChanged();
                        if (listener != null) listener.onDefaultChanged();
                        Toast.makeText(context, "地址已删除", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvTag;
        View lytDefault;
        ImageView ivEdit;
        ImageView ivDelete;
    }
}
