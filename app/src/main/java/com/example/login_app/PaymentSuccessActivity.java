package com.example.login_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        String name = getIntent().getStringExtra("product_name");
        int qty = getIntent().getIntExtra("quantity", 1);
        double total = getIntent().getDoubleExtra("total", 0);

        TextView tvInfo = findViewById(R.id.tv_payment_info);
        tvInfo.setText("商品: " + name + "\n数量: " + qty + "\n合计: ¥" + String.format("%.2f", total));

        findViewById(R.id.btn_back_shop).setOnClickListener(v -> finish());
    }
}
