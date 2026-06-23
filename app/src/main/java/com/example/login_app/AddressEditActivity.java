package com.example.login_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 收货地址编辑页 — EditText输入 + 数据校验
 * 支持新增和编辑两种模式
 */
public class AddressEditActivity extends AppCompatActivity {
    private static final String TAG = "AddrEdit";

    private EditText etName, etPhone, etProvince, etCity, etDistrict, etDetail;
    private int editId = -1; // -1表示新增，正数表示编辑

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

            // 先从DB加载数据，确保编辑模式能找到地址
            editId = getIntent().getIntExtra("address_id", -1);
            Log.d(TAG, "editId=" + editId);

            if (editId > 0) {
                ((TextView) findViewById(R.id.tv_title)).setText("编辑地址");
                // 确保内存中有数据
                AddressManager.getInstance().loadFromDb(this);
                Address addr = AddressManager.getInstance().getById(editId);
                Log.d(TAG, "addr=" + (addr != null ? addr.getName() : "null"));
                if (addr != null) {
                    etName.setText(addr.getName());
                    etPhone.setText(addr.getPhone());
                    etProvince.setText(addr.getProvince());
                    etCity.setText(addr.getCity());
                    etDistrict.setText(addr.getDistrict());
                    etDetail.setText(addr.getDetail());
                } else {
                    Log.w(TAG, "地址未找到, editId=" + editId);
                    Toast.makeText(this, "地址数据不存在", Toast.LENGTH_SHORT).show();
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

    /** 保存地址，含输入校验 */
    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String province = etProvince.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        // 1. 空值校验
        if (TextUtils.isEmpty(name)) {
            etName.setError("请输入收货人姓名");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("请输入手机号码");
            etPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(province)) {
            etProvince.setError("请输入省份");
            etProvince.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(city)) {
            etCity.setError("请输入城市");
            etCity.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(district)) {
            etDistrict.setError("请输入区/县");
            etDistrict.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(detail)) {
            etDetail.setError("请输入详细地址");
            etDetail.requestFocus();
            return;
        }

        // 2. 手机号格式校验（11位中国大陆手机号）
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            etPhone.setError("请输入有效的11位手机号码");
            etPhone.requestFocus();
            return;
        }

        // 3. 姓名长度校验
        if (name.length() < 2 || name.length() > 20) {
            etName.setError("姓名应为2-20个字符");
            etName.requestFocus();
            return;
        }

        // 4. 详细地址长度校验
        if (detail.length() < 5) {
            etDetail.setError("详细地址至少5个字符");
            etDetail.requestFocus();
            return;
        }

        Log.d(TAG, "saving: " + name + " editId=" + editId);
        if (editId > 0) {
            // 编辑模式
            AddressManager.getInstance().update(editId, name, phone, province, city, district, detail);
        } else {
            // 新增模式
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
