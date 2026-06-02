package com.example.login_app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private EditText etNickname;
    private TextView tvNickname;
    private int currentAvatar = R.drawable.avatar_default;

    private static final int[] AVATARS = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.iv_avatar);
        etNickname = findViewById(R.id.et_nickname);
        tvNickname = findViewById(R.id.tv_nickname_label);

        // 初始化默认昵称
        etNickname.setText("用户123456");
        tvNickname.setText("用户123456");

        // 点击头像弹出选择对话框
        ivAvatar.setOnClickListener(v -> showAvatarPickerDialog());

        // 保存昵称
        findViewById(R.id.btn_save_nickname).setOnClickListener(v -> {
            String name = etNickname.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
                return;
            }
            tvNickname.setText(name);
            Toast.makeText(this, "昵称已更新", Toast.LENGTH_SHORT).show();
        });

        // 跳转地址管理
        findViewById(R.id.btn_address).setOnClickListener(v ->
                startActivity(new Intent(this, AddressListActivity.class)));

        // 退出登录
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        CartManager.getInstance().clear();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void showAvatarPickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_avatar_picker);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        int[] avatarIds = {
                R.id.avatar_1, R.id.avatar_2, R.id.avatar_3,
                R.id.avatar_4, R.id.avatar_5, R.id.avatar_6
        };

        for (int i = 0; i < avatarIds.length; i++) {
            final int avatarRes = AVATARS[i];
            dialog.findViewById(avatarIds[i]).setOnClickListener(v -> {
                currentAvatar = avatarRes;
                ivAvatar.setImageResource(currentAvatar);
                dialog.dismiss();
                Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show();
            });
        }

        dialog.show();
    }
}
