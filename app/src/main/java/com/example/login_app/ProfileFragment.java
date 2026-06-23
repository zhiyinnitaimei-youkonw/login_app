package com.example.login_app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * 个人中心Tab — 头像/昵称/地址/退出
 * 头像选择改为页面内嵌区域，不弹Dialog
 */
public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private EditText etNickname;
    private TextView tvNickname;
    private ViewGroup avatarPickerContainer;
    private int currentAvatar = R.drawable.avatar_default;

    private static final int[] AVATARS = {
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
            R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.iv_avatar);
        etNickname = view.findViewById(R.id.et_nickname);
        tvNickname = view.findViewById(R.id.tv_nickname_label);

        // 从SQLite加载资料
        loadProfile();

        // 点击头像 → 显示/隐藏头像选择区域
        ivAvatar.setOnClickListener(v -> toggleAvatarPicker(view));

        // 保存昵称
        view.findViewById(R.id.btn_save_nickname).setOnClickListener(v -> {
            String name = etNickname.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getActivity(), "请输入昵称", Toast.LENGTH_SHORT).show();
                return;
            }
            // XSS/注入防护：过滤特殊字符
            name = name.replaceAll("[<>\"'&]", "");
            if (name.isEmpty()) {
                Toast.makeText(getActivity(), "昵称包含无效字符", Toast.LENGTH_SHORT).show();
                return;
            }
            tvNickname.setText(name);
            saveProfile(name, currentAvatar);
            Toast.makeText(getActivity(), "昵称已更新", Toast.LENGTH_SHORT).show();
        });

        // 收货地址
        view.findViewById(R.id.btn_address).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddressListActivity.class)));

        // 退出登录
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        CartManager.getInstance().clear();
                        CartManager.getInstance().saveToDb(requireContext());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        if (getActivity() != null) getActivity().finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        return view;
    }

    /** 切换头像选择区域的显示/隐藏 — 页面内嵌，不弹窗 */
    private void toggleAvatarPicker(View root) {
        View existing = root.findViewById(R.id.avatar_picker_container);
        if (existing == null) {
            // 首次使用：从dialog布局inflate并嵌入到内容容器中
            ViewGroup container = root.findViewById(R.id.profile_container);
            final View picker = LayoutInflater.from(getContext()).inflate(
                    R.layout.dialog_avatar_picker, container, false);
            picker.setId(R.id.avatar_picker_container);
            // 在头像卡片之后插入选择器（avatar卡片是container的第0个子View）
            container.addView(picker, 1);

            // 绑定6个头像图标的点击事件
            int[] ids = {R.id.avatar_1, R.id.avatar_2, R.id.avatar_3,
                    R.id.avatar_4, R.id.avatar_5, R.id.avatar_6};
            for (int i = 0; i < ids.length; i++) {
                final int res = AVATARS[i];
                picker.findViewById(ids[i]).setOnClickListener(v -> {
                    currentAvatar = res;
                    ivAvatar.setImageResource(currentAvatar);
                    saveProfile(tvNickname.getText().toString(), currentAvatar);
                    picker.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "头像已更新", Toast.LENGTH_SHORT).show();
                });
            }
            picker.setVisibility(View.VISIBLE);
        } else {
            // 切换显示/隐藏
            existing.setVisibility(existing.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    // ============== SQLite 持久化 ==============

    private void loadProfile() {
        SQLiteDatabase db = DatabaseHelper.getInstance(requireContext()).getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROFILE,
                null, null, null, null, null, null, "1");
        if (cursor != null && cursor.moveToFirst()) {
            String nickname = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_NICKNAME));
            int avatar = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_AVATAR));
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
        if (tvNickname.getText().toString().isEmpty()) {
            tvNickname.setText("用户123456");
            etNickname.setText("用户123456");
        }
    }

    private void saveProfile(String nickname, int avatarResId) {
        SQLiteDatabase db = DatabaseHelper.getInstance(requireContext()).getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_PROFILE, null, null);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_PROFILE_NICKNAME, nickname);
        cv.put(DatabaseHelper.COL_PROFILE_AVATAR, avatarResId);
        db.insert(DatabaseHelper.TABLE_PROFILE, null, cv);
    }
}
