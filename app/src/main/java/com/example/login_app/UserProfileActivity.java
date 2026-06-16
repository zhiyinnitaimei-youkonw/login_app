package com.example.login_app;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

        // 从SQLite加载
        loadProfile();

        ivAvatar.setOnClickListener(v -> showAvatarPickerDialog());

        findViewById(R.id.btn_save_nickname).setOnClickListener(v -> {
            String name = etNickname.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
                return;
            }
            tvNickname.setText(name);
            saveProfile(name, currentAvatar);
            Toast.makeText(this, "昵称已更新", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_address).setOnClickListener(v ->
                startActivity(new Intent(this, AddressListActivity.class)));

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        CartManager.getInstance().clear();
                        CartManager.getInstance().saveToDb(this);
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        int[] ids = {R.id.avatar_1, R.id.avatar_2, R.id.avatar_3, R.id.avatar_4, R.id.avatar_5, R.id.avatar_6};
        for (int i = 0; i < ids.length; i++) {
            final int res = AVATARS[i];
            dialog.findViewById(ids[i]).setOnClickListener(v -> {
                currentAvatar = res;
                ivAvatar.setImageResource(currentAvatar);
                saveProfile(tvNickname.getText().toString(), currentAvatar);
                dialog.dismiss();
                Toast.makeText(this, "头像已更新", Toast.LENGTH_SHORT).show();
            });
        }
        dialog.show();
    }

    // ============== SQLite 持久化 ==============

    private void loadProfile() {
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROFILE, null, null, null, null, null, null, "1");
        if (cursor != null && cursor.moveToFirst()) {
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_NICKNAME));
            int avatar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_AVATAR));
            if (nickname != null && !nickname.isEmpty()) {
                etNickname.setText(nickname);
                tvNickname.setText(nickname);
            }
            if (avatar != 0) {
                currentAvatar = avatar;
                ivAvatar.setImageResource(avatar);
            }
            cursor.close();
        }
        // 默认值
        if (tvNickname.getText().toString().isEmpty()) {
            tvNickname.setText("用户123456");
            etNickname.setText("用户123456");
        }
    }

    private void saveProfile(String nickname, int avatarResId) {
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PROFILE, null, null);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_PROFILE_NICKNAME, nickname);
        cv.put(DatabaseHelper.COL_PROFILE_AVATAR, avatarResId);
        db.insert(DatabaseHelper.TABLE_PROFILE, null, cv);
    }
}
