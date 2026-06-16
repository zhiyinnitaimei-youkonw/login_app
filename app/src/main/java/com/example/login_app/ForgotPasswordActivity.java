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

    private TextView tvEmail;
    private EditText etNewPassword, etConfirmPassword, etSmsCode;
    private Button btnGetCode, btnConfirm;

    private String email;
    private String generatedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) email = "未提供邮箱";

        tvEmail = findViewById(R.id.tv_email_display);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etSmsCode = findViewById(R.id.et_sms_code);
        btnGetCode = findViewById(R.id.btn_get_code);
        btnConfirm = findViewById(R.id.btn_confirm);

        tvEmail.setText("邮箱：" + email);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        btnGetCode.setOnClickListener(v -> sendCode());
        btnConfirm.setOnClickListener(v -> confirmReset());
    }

    private void sendCode() {
        generatedCode = String.format("%06d", new Random().nextInt(999999));
        btnGetCode.setEnabled(false);
        Toast.makeText(this, "正在发送验证码...", Toast.LENGTH_SHORT).show();

        MailSender.sendCode(email, generatedCode, new MailSender.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ForgotPasswordActivity.this, "验证码已发送至邮箱", Toast.LENGTH_LONG).show();
                etSmsCode.requestFocus();
                btnGetCode.postDelayed(() -> btnGetCode.setEnabled(true), 60000);
            }
            @Override
            public void onError(String msg) {
                btnGetCode.setEnabled(true);
                new AlertDialog.Builder(ForgotPasswordActivity.this)
                        .setTitle("邮件发送失败")
                        .setMessage(msg + "\n\n(模拟)验证码: " + generatedCode)
                        .setPositiveButton("确定", null).show();
                etSmsCode.requestFocus();
            }
        });
    }

    private void confirmReset() {
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();
        String code = etSmsCode.getText().toString().trim();

        if (newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showAlert("提示", "请输入新密码和确认密码"); return;
        }
        if (newPwd.length() < 8) {
            showAlert("提示", "密码长度不能少于8位"); return;
        }
        if (!newPwd.matches(".*\\d.*") || !newPwd.matches(".*[a-zA-Z].*")) {
            showAlert("提示", "密码必须包含数字和字母"); return;
        }
        if (!newPwd.equals(confirmPwd)) {
            showAlert("提示", "两次输入的密码不一致"); return;
        }
        if (code.isEmpty()) {
            showAlert("提示", "请输入验证码"); return;
        }
        if (generatedCode == null || !generatedCode.equals(code)) {
            showAlert("验证失败", "验证码错误"); return;
        }

        // 同步更新SQLite
        new UserDao(this).updatePassword(email, newPwd);

        new AlertDialog.Builder(this)
                .setTitle("修改成功")
                .setMessage("密码已重置，请使用新密码登录")
                .setPositiveButton("确定", (d, w) -> {
                    Intent result = new Intent();
                    result.putExtra("new_password", newPwd);
                    setResult(RESULT_OK, result);
                    finish();
                }).show();
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("确定", null).show();
    }
}
