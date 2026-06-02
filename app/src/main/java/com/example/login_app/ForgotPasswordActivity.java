package com.example.login_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvPhone;
    private EditText etNewPassword, etConfirmPassword, etSmsCode;
    private Button btnGetCode, btnConfirm;

    private String phone;
    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 接收登录页传来的手机号
        phone = getIntent().getStringExtra("phone");
        if (phone == null || phone.isEmpty()) {
            phone = "未提供手机号";
        }

        tvPhone = findViewById(R.id.tv_phone_display);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etSmsCode = findViewById(R.id.et_sms_code);
        btnGetCode = findViewById(R.id.btn_get_code);
        btnConfirm = findViewById(R.id.btn_confirm);

        tvPhone.setText("手机号：" + phone);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 获取验证码
        btnGetCode.setOnClickListener(v -> sendVerificationCode());

        // 确认修改
        btnConfirm.setOnClickListener(v -> confirmReset());
    }

    private void sendVerificationCode() {
        // 模拟发送短信验证码
        generatedCode = String.format("%06d", new Random().nextInt(999999));
        btnGetCode.setEnabled(false);

        new AlertDialog.Builder(this)
                .setTitle("短信已发送")
                .setMessage("验证码已发送至 " + phone + "\n\n(模拟)验证码：" + generatedCode + "\n\n请在60秒内完成验证")
                .setPositiveButton("确定", (d, w) -> {
                    etSmsCode.requestFocus();
                    btnGetCode.postDelayed(() -> btnGetCode.setEnabled(true), 60000);
                })
                .show();
    }

    private void confirmReset() {
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();
        String code = etSmsCode.getText().toString().trim();

        if (newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showAlert("提示", "请输入新密码和确认密码");
            return;
        }
        if (newPwd.length() < 6) {
            showAlert("提示", "密码长度不能少于6位");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            showAlert("提示", "两次输入的密码不一致");
            return;
        }
        if (code.isEmpty()) {
            showAlert("提示", "请输入短信验证码");
            return;
        }
        if (generatedCode == null || !generatedCode.equals(code)) {
            showAlert("验证失败", "验证码错误，请重新输入");
            return;
        }

        // 验证通过，通过setResult将新密码传回登录页
        showAlert("修改成功", "密码已重置\n\n请使用新密码登录");
        Intent result = new Intent();
        result.putExtra("new_password", newPwd);
        setResult(RESULT_OK, result);
        finish();
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (d, w) -> {
                    if (title.equals("修改成功")) {
                        // 已经在上面 setResult 和 finish 了
                        // 但AlertDialog dismiss后需要finish，这里做个判断
                    }
                })
                .setOnDismissListener(d -> {
                    if (title.equals("修改成功")) {
                        // 防止重复finish
                    }
                })
                .show();
    }
}
