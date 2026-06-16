package com.example.login_app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddressEditActivity extends AppCompatActivity {

    private EditText etName, etPhone, etProvince, etCity, etDistrict, etDetail;
    private TextView tvTitle;
    private int editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etProvince = findViewById(R.id.et_province);
        etCity = findViewById(R.id.et_city);
        etDistrict = findViewById(R.id.et_district);
        etDetail = findViewById(R.id.et_detail);

        editId = getIntent().getIntExtra("address_id", -1);
        if (editId > 0) {
            tvTitle.setText("编辑地址");
            Address addr = AddressManager.getInstance().getById(editId);
            if (addr != null) {
                etName.setText(addr.getName());
                etPhone.setText(addr.getPhone());
                etProvince.setText(addr.getProvince());
                etCity.setText(addr.getCity());
                etDistrict.setText(addr.getDistrict());
                etDetail.setText(addr.getDetail());
            }
        } else {
            tvTitle.setText("新增地址");
        }

        findViewById(R.id.btn_save).setOnClickListener(v -> saveAddress());
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

        if (editId > 0) {
            AddressManager.getInstance().update(editId, name, phone, province, city, district, detail);
            Toast.makeText(this, "地址已更新", Toast.LENGTH_SHORT).show();
        } else {
            int newId = AddressManager.getInstance().nextId();
            AddressManager.getInstance().add(new Address(newId, name, phone,
                    province, city, district, detail, false));
            Toast.makeText(this, "地址已保存", Toast.LENGTH_SHORT).show();
        }
        AddressManager.getInstance().saveToDb(this);
        finish();
    }
}
