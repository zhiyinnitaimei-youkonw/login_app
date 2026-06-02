package com.example.login_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private RadioGroup rgLoginMode;
    private RadioButton rbPassword, rbSms;
    private EditText etPhone, etPassword, etSmsCode;
    private TextView tvPasswordLabel, tvSmsLabel, tvForgotPassword;
    private CheckBox cbRemember;
    private Button btnLogin, btnGetSms;
    private View passwordArea, smsCodeArea, rememberArea;

    private UserDao userDao;
    private String generatedSmsCode;

    private static final int REQ_FORGOT_PASSWORD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SQLite 数据持久化
        userDao = new UserDao(this);

        rgLoginMode = findViewById(R.id.rg_login_mode);
        rbPassword = findViewById(R.id.rb_password);
        rbSms = findViewById(R.id.rb_sms);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etSmsCode = findViewById(R.id.et_sms_code);
        tvPasswordLabel = findViewById(R.id.tv_password_label);
        tvSmsLabel = findViewById(R.id.tv_sms_label);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRemember = findViewById(R.id.cb_remember);
        btnLogin = findViewById(R.id.btn_login);
        btnGetSms = findViewById(R.id.btn_get_sms);
        passwordArea = findViewById(R.id.area_password);
        smsCodeArea = findViewById(R.id.area_sms_code);
        rememberArea = findViewById(R.id.area_remember);

        // 切换登录模式
        rgLoginMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_password) {
                tvPasswordLabel.setVisibility(View.VISIBLE);
                passwordArea.setVisibility(View.VISIBLE);
                tvForgotPassword.setVisibility(View.VISIBLE);
                rememberArea.setVisibility(View.VISIBLE);
                tvSmsLabel.setVisibility(View.GONE);
                smsCodeArea.setVisibility(View.GONE);
            } else {
                tvPasswordLabel.setVisibility(View.GONE);
                passwordArea.setVisibility(View.GONE);
                tvForgotPassword.setVisibility(View.GONE);
                rememberArea.setVisibility(View.GONE);
                tvSmsLabel.setVisibility(View.VISIBLE);
                smsCodeArea.setVisibility(View.VISIBLE);
            }
        });

        // SQLite 恢复记住密码的用户
        loadRememberedFromDb();

        btnLogin.setOnClickListener(v -> doLogin());

        tvForgotPassword.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "请先输入手机号码", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            intent.putExtra("phone", phone);
            startActivityForResult(intent, REQ_FORGOT_PASSWORD);
        });

        btnGetSms.setOnClickListener(v -> sendSmsCode());
    }

    private void doLogin() {
        String phone = etPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            showAlert("提示", "请输入手机号码");
            return;
        }

        if (rbPassword.isChecked()) {
            // 密码登录
            String password = etPassword.getText().toString().trim();
            if (password.isEmpty()) {
                showAlert("提示", "请输入密码");
                return;
            }

            // DML: INSERT/UPDATE 持久化到 SQLite
            boolean remember = cbRemember.isChecked();
            userDao.insertOrUpdate(phone, password, remember);
            int userCount = userDao.getUserCount();

            showAlert("登录成功",
                    "密码登录验证通过\n欢迎回来！\n手机号：" + phone
                            + "\n\n[SQLite] 记住密码: " + (remember ? "是" : "否")
                            + "\n[SQLite] 用户总数: " + userCount);
            goToMain();
        } else {
            // 验证码登录
            String code = etSmsCode.getText().toString().trim();
            if (code.isEmpty()) {
                showAlert("提示", "请输入验证码");
                return;
            }
            if (generatedSmsCode == null || !generatedSmsCode.equals(code)) {
                showAlert("登录失败", "验证码错误，请重新输入");
                return;
            }

            int userCount = userDao.getUserCount();
            showAlert("登录成功",
                    "验证码登录验证通过\n欢迎！\n手机号：" + phone
                            + "\n\n[SQLite] 用户总数: " + userCount);
            goToMain();
        }
    }

    private void sendSmsCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "请先输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        generatedSmsCode = String.format("%06d", new Random().nextInt(999999));
        btnGetSms.setEnabled(false);

        new AlertDialog.Builder(this)
                .setTitle("短信已发送")
                .setMessage("验证码已发送至 " + phone + "\n\n(模拟)验证码：" + generatedSmsCode + "\n\n请在60秒内输入验证码")
                .setPositiveButton("确定", (d, w) -> {
                    etSmsCode.requestFocus();
                    btnGetSms.postDelayed(() -> btnGetSms.setEnabled(true), 60000);
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_FORGOT_PASSWORD && resultCode == RESULT_OK && data != null) {
            String newPassword = data.getStringExtra("new_password");
            String phone = etPhone.getText().toString().trim();
            if (newPassword != null) {
                etPassword.setText(newPassword);
                // DML: 同步更新 SQLite 中的密码
                if (!phone.isEmpty()) {
                    UserDao.UserInfo user = userDao.getByPhone(phone);
                    if (user != null) {
                        userDao.updatePassword(phone, newPassword);
                    } else {
                        userDao.insertOrUpdate(phone, newPassword, false);
                    }
                }
                rbPassword.setChecked(true);
                showAlert("密码已重置",
                        "新密码已填入，请使用新密码登录"
                                + "\n\n[SQLite] 数据库已同步更新");
            }
        }
    }

    /**
     * 从 SQLite 数据库恢复记住密码的用户
     * SELECT * FROM user WHERE remember=1 LIMIT 1;
     */
    private void loadRememberedFromDb() {
        UserDao.UserInfo remembered = userDao.getRememberedUser();
        if (remembered != null) {
            etPhone.setText(remembered.phone);
            etPassword.setText(remembered.password);
            cbRemember.setChecked(true);
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    private void goToMain() {
        startActivity(new Intent(this, ProductListActivity.class));
    }
}
