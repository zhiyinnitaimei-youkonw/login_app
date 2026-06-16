package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class AddressListActivity extends AppCompatActivity {
    private static final String TAG = "AddrList";

    private ListView listView;
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate start");
        try {
            setContentView(R.layout.activity_address_list);
            Log.d(TAG, "setContentView ok");

            findViewById(R.id.iv_back).setOnClickListener(v -> finish());

            // 从SQLite加载
            Log.d(TAG, "loading from db...");
            AddressManager.getInstance().loadFromDb(this);
            List<Address> list = AddressManager.getInstance().getAddresses();
            Log.d(TAG, "loaded " + list.size() + " addresses");

            listView = findViewById(R.id.list_address);
            Log.d(TAG, "listView=" + listView);

            adapter = new AddressAdapter();
            listView.setAdapter(adapter);
            Log.d(TAG, "adapter set, count=" + adapter.getCount());

            findViewById(R.id.btn_add_address).setOnClickListener(v -> {
                Log.d(TAG, "add address clicked");
                startActivity(new Intent(this, AddressEditActivity.class));
            });
            Log.d(TAG, "onCreate done");
        } catch (Exception e) {
            Log.e(TAG, "CRASH in onCreate", e);
            Toast.makeText(this, "地址页加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AddressManager.getInstance().loadFromDb(this);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private class AddressAdapter extends BaseAdapter {
        @Override
        public int getCount() { return AddressManager.getInstance().getAddresses().size(); }
        @Override
        public Address getItem(int p) { return AddressManager.getInstance().getAddresses().get(p); }
        @Override
        public long getItemId(int p) { return p; }

        @Override
        public View getView(int pos, View cv, ViewGroup parent) {
            if (cv == null) {
                cv = LayoutInflater.from(AddressListActivity.this)
                        .inflate(R.layout.item_address, parent, false);
            }
            Address a = getItem(pos);

            ((TextView) cv.findViewById(R.id.tv_name)).setText(a.getName() + "    " + a.getPhone());
            ((TextView) cv.findViewById(R.id.tv_address)).setText(a.getFullAddress());
            cv.findViewById(R.id.tv_tag).setVisibility(a.isDefault() ? View.VISIBLE : View.GONE);

            cv.findViewById(R.id.lyt_default).setOnClickListener(v -> {
                AddressManager.getInstance().setDefault(a.getId());
                AddressManager.getInstance().saveToDb(AddressListActivity.this);
                notifyDataSetChanged();
            });

            cv.findViewById(R.id.iv_edit).setOnClickListener(v -> {
                Log.d(TAG, "edit address id=" + a.getId());
                Intent i = new Intent(AddressListActivity.this, AddressEditActivity.class);
                i.putExtra("address_id", a.getId());
                startActivity(i);
            });

            cv.findViewById(R.id.iv_delete).setOnClickListener(v -> {
                new AlertDialog.Builder(AddressListActivity.this)
                        .setTitle("删除").setMessage("确定删除该地址？")
                        .setPositiveButton("确定", (d, w) -> {
                            AddressManager.getInstance().remove(a.getId());
                            AddressManager.getInstance().saveToDb(AddressListActivity.this);
                            notifyDataSetChanged();
                            Toast.makeText(AddressListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton("取消", null).show();
            });
            return cv;
        }
    }
}
