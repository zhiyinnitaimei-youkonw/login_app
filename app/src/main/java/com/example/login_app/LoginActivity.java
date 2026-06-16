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
    private RadioButton rbPassword, rbRegister;
    private EditText etEmail, etPassword, etRegCode, etRegPassword;
    private TextView tvPasswordLabel, tvRegLabel, tvForgotPassword;
    private CheckBox cbRemember;
    private Button btnLogin, btnGetRegCode;
    private View passwordArea, registerArea, rememberArea;

    private UserDao userDao;
    private String generatedCode;

    private static final int REQ_FORGOT_PASSWORD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = new UserDao(this);

        rgLoginMode = findViewById(R.id.rg_login_mode);
        rbPassword = findViewById(R.id.rb_password);
        rbRegister = findViewById(R.id.rb_register);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRegCode = findViewById(R.id.et_reg_code);
        etRegPassword = findViewById(R.id.et_reg_password);
        tvPasswordLabel = findViewById(R.id.tv_password_label);
        tvRegLabel = findViewById(R.id.tv_reg_label);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRemember = findViewById(R.id.cb_remember);
        btnLogin = findViewById(R.id.btn_login);
        btnGetRegCode = findViewById(R.id.btn_get_reg_code);
        passwordArea = findViewById(R.id.area_password);
        registerArea = findViewById(R.id.area_register);
        rememberArea = findViewById(R.id.area_remember);

        // 模式切换：密码登录 / 注册
        rgLoginMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_password) {
                tvPasswordLabel.setVisibility(View.VISIBLE);
                passwordArea.setVisibility(View.VISIBLE);
                tvForgotPassword.setVisibility(View.VISIBLE);
                rememberArea.setVisibility(View.VISIBLE);
                tvRegLabel.setVisibility(View.GONE);
                registerArea.setVisibility(View.GONE);
                btnLogin.setText("登录");
            } else {
                tvPasswordLabel.setVisibility(View.GONE);
                passwordArea.setVisibility(View.GONE);
                tvForgotPassword.setVisibility(View.GONE);
                rememberArea.setVisibility(View.GONE);
                tvRegLabel.setVisibility(View.VISIBLE);
                registerArea.setVisibility(View.VISIBLE);
                btnLogin.setText("注册");
            }
        });

        loadRememberedFromDb();

        btnLogin.setOnClickListener(v -> {
            if (rbRegister.isChecked()) {
                doRegister();
            } else {
                doLogin();
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "请先输入邮箱地址", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            intent.putExtra("email", email);
            startActivityForResult(intent, REQ_FORGOT_PASSWORD);
        });

        btnGetRegCode.setOnClickListener(v -> sendRegCode());
    }

    // ==================== 验证 ====================

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            showAlert("提示", "请输入邮箱地址");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlert("提示", "请输入正确的邮箱格式\n例如: example@qq.com");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            showAlert("提示", "请输入密码");
            return false;
        }
        if (password.length() < 8) {
            showAlert("提示", "密码长度不能少于8位");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            showAlert("提示", "密码必须包含至少一个数字");
            return false;
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            showAlert("提示", "密码必须包含至少一个字母");
            return false;
        }
        return true;
    }

    // ==================== 注册 ====================

    private void sendRegCode() {
        String email = etEmail.getText().toString().trim();
        if (!validateEmail(email)) return;

        generatedCode = String.format("%06d", new Random().nextInt(999999));
        btnGetRegCode.setEnabled(false);
        Toast.makeText(this, "正在发送验证码...", Toast.LENGTH_SHORT).show();

        MailSender.sendCode(email, generatedCode, new MailSender.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(LoginActivity.this, "验证码已发送至邮箱，请查收", Toast.LENGTH_LONG).show();
                etRegCode.requestFocus();
                btnGetRegCode.postDelayed(() -> btnGetRegCode.setEnabled(true), 60000);
            }

            @Override
            public void onError(String msg) {
                btnGetRegCode.setEnabled(true);
                showAlert("发送失败", "邮件发送失败\n" + msg + "\n\n(模拟)验证码: " + generatedCode);
                etRegCode.requestFocus();
            }
        });
    }

    private void doRegister() {
        String email = etEmail.getText().toString().trim();
        if (!validateEmail(email)) return;

        String code = etRegCode.getText().toString().trim();
        if (code.isEmpty()) {
            showAlert("提示", "请输入邮箱验证码");
            return;
        }
        if (generatedCode == null || !generatedCode.equals(code)) {
            showAlert("注册失败", "验证码错误，请重新获取");
            return;
        }

        String password = etRegPassword.getText().toString().trim();
        if (!validatePassword(password)) return;

        // 检查是否已注册
        UserDao.UserInfo existing = userDao.getByEmail(email);
        if (existing != null) {
            showAlert("提示", "该邮箱已注册，请直接登录");
            rbPassword.setChecked(true);
            return;
        }

        // 写入 SQLite
        userDao.insertUser(email, password);
        Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_LONG).show();
        rbPassword.setChecked(true);
        etPassword.setText(password);
    }

    // ==================== 登录 ====================

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        if (!validateEmail(email)) return;

        String password = etPassword.getText().toString().trim();
        if (!validatePassword(password)) return;

        // 验证账号是否存在
        UserDao.UserInfo user = userDao.getByEmail(email);
        if (user == null) {
            showAlert("登录失败", "该邮箱尚未注册，请先注册");
            return;
        }
        if (!password.equals(user.password)) {
            showAlert("登录失败", "密码错误，请重新输入");
            return;
        }

        boolean remember = cbRemember.isChecked();
        userDao.setRemember(email, remember);

        // Bug 7: 不弹AlertDialog，直接Toast后跳转
        Toast.makeText(this, "登录成功，欢迎回来！", Toast.LENGTH_SHORT).show();
        goToMain();
    }

    // ==================== 回调 ====================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_FORGOT_PASSWORD && resultCode == RESULT_OK && data != null) {
            String newPassword = data.getStringExtra("new_password");
            if (newPassword != null) {
                etPassword.setText(newPassword);
                rbPassword.setChecked(true);
                Toast.makeText(this, "密码已重置，请使用新密码登录", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadRememberedFromDb() {
        UserDao.UserInfo remembered = userDao.getRememberedUser();
        if (remembered != null) {
            etEmail.setText(remembered.email);
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
