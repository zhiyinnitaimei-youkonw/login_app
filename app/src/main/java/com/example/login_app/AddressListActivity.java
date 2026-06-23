package com.example.login_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
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

            // 从SQLite加载数据
            Log.d(TAG, "loading from db...");
            AddressManager.getInstance().loadFromDb(this);
            List<Address> list = AddressManager.getInstance().getAddresses();
            Log.d(TAG, "loaded " + list.size() + " addresses");

            listView = findViewById(R.id.list_address);
            Log.d(TAG, "listView=" + listView);

            // 使用独立的AddressAdapter（ViewHolder模式）
            adapter = new AddressAdapter(this);
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
        Log.d(TAG, "onResume - reloading from DB");
        AddressManager.getInstance().loadFromDb(this);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        Log.d(TAG, "onResume - adapter count=" + (adapter != null ? adapter.getCount() : 0));
    }
}
