package com.example.login_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddressEditActivity extends AppCompatActivity {
    private static final String TAG = "AddrEdit";

    private EditText etName, etPhone, etProvince, etCity, etDistrict, etDetail;
    private int editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        try {
            setContentView(R.layout.activity_address_edit);

            findViewById(R.id.iv_back).setOnClickListener(v -> finish());

            etName = findViewById(R.id.et_name);
            etPhone = findViewById(R.id.et_phone);
            etProvince = findViewById(R.id.et_province);
            etCity = findViewById(R.id.et_city);
            etDistrict = findViewById(R.id.et_district);
            etDetail = findViewById(R.id.et_detail);
            Log.d(TAG, "all EditText found ok");

            editId = getIntent().getIntExtra("address_id", -1);
            Log.d(TAG, "editId=" + editId);

            if (editId > 0) {
                ((TextView) findViewById(R.id.tv_title)).setText("编辑地址");
                Address addr = AddressManager.getInstance().getById(editId);
                Log.d(TAG, "addr=" + (addr != null ? addr.getName() : "null"));
                if (addr != null) {
                    etName.setText(addr.getName());
                    etPhone.setText(addr.getPhone());
                    etProvince.setText(addr.getProvince());
                    etCity.setText(addr.getCity());
                    etDistrict.setText(addr.getDistrict());
                    etDetail.setText(addr.getDetail());
                }
            }

            findViewById(R.id.btn_save).setOnClickListener(v -> saveAddress());
            Log.d(TAG, "onCreate done");
        } catch (Exception e) {
            Log.e(TAG, "CRASH", e);
            Toast.makeText(this, "页面加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || province.isEmpty()
                || city.isEmpty() || district.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "saving: " + name + " editId=" + editId);
        if (editId > 0) {
            AddressManager.getInstance().update(editId, name, phone, province, city, district, detail);
        } else {
            int newId = AddressManager.getInstance().nextId();
            AddressManager.getInstance().add(new Address(newId, name, phone,
                    province, city, district, detail, false));
            Log.d(TAG, "new id=" + newId);
        }
        AddressManager.getInstance().saveToDb(this);
        Log.d(TAG, "saved, finishing");
        Toast.makeText(this, editId > 0 ? "地址已更新" : "地址已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}
