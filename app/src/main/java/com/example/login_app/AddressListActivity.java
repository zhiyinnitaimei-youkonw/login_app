package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddressListActivity extends AppCompatActivity {

    private ListView listView;
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 从SQLite加载地址
        AddressManager.getInstance().loadFromDb(this);

        listView = findViewById(R.id.list_address);
        adapter = new AddressAdapter();
        listView.setAdapter(adapter);

        findViewById(R.id.btn_add_address).setOnClickListener(v ->
                startActivity(new Intent(this, AddressEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class AddressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AddressManager.getInstance().getAddresses().size();
        }

        @Override
        public Address getItem(int position) {
            return AddressManager.getInstance().getAddresses().get(position);
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AddressListActivity.this)
                        .inflate(R.layout.item_address, parent, false);
            }

            Address addr = getItem(position);

            TextView tvName = convertView.findViewById(R.id.tv_name);
            tvName.setText(addr.getName() + "    " + addr.getPhone());

            TextView tvAddr = convertView.findViewById(R.id.tv_address);
            tvAddr.setText(addr.getFullAddress());

            TextView tvTag = convertView.findViewById(R.id.tv_tag);
            tvTag.setVisibility(addr.isDefault() ? View.VISIBLE : View.GONE);

            ImageView ivEdit = convertView.findViewById(R.id.iv_edit);
            ImageView ivDelete = convertView.findViewById(R.id.iv_delete);

            ivEdit.setOnClickListener(v -> {
                Intent intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
                intent.putExtra("address_id", addr.getId());
                startActivity(intent);
            });

            ivDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(AddressListActivity.this)
                        .setTitle("删除地址")
                        .setMessage("确定要删除该地址吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            AddressManager.getInstance().remove(addr.getId());
                            AddressManager.getInstance().saveToDb(AddressListActivity.this);
                            notifyDataSetChanged();
                            Toast.makeText(AddressListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            });

            convertView.findViewById(R.id.lyt_default).setOnClickListener(v -> {
                AddressManager.getInstance().setDefault(addr.getId());
                AddressManager.getInstance().saveToDb(AddressListActivity.this);
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}
