package com.example.login_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片缓存管理器 — 二级缓存（存储卡 → 网络下载）
 *
 * 缓存策略:
 *   ① 先判断是否为首次访问网络图片（SharedPreferences标志位）
 *   ② 若是首次，从网络服务器下载图片
 *   ③ 把下载的图片保存到手机存储卡
 *   ④ 写入商品本地图片路径到数据库
 *   ⑤ 更新SharedPreferences中的首次访问标志
 *
 * 教学要点: 多级缓存是移动端性能优化的核心手段
 */
public class ImageCacheManager {

    private static final String TAG = "ImageCache";
    private static final String CACHE_DIR = "shopping_images";

    private static ImageCacheManager instance;

    private ImageCacheManager() {}

    public static synchronized ImageCacheManager getInstance() {
        if (instance == null) {
            instance = new ImageCacheManager();
        }
        return instance;
    }

    /**
     * 加载商品图片（二级缓存）
     * @param context 上下文
     * @param imageUrl 网络图片URL
     * @param localPath 本地存储路径（可为null）
     * @param imageView 目标ImageView
     * @param defaultResId 默认占位图资源ID
     */
    public void loadImage(final Context context, final String imageUrl, String localPath,
                          final ImageView imageView, final int defaultResId) {
        // 先显示默认图
        imageView.setImageResource(defaultResId);

        if (imageUrl == null || imageUrl.isEmpty()) {
            return; // 无网络URL，使用默认图
        }

        // ① 检查存储卡缓存
        if (localPath != null && !localPath.isEmpty()) {
            File localFile = new File(localPath);
            if (localFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(localPath);
                imageView.setImageBitmap(bitmap);
                Log.d(TAG, "从存储卡加载: " + localPath);
                return;
            }
        }

        // ② 首次访问 → 从网络下载
        final SharedUtil sharedUtil = SharedUtil.getInstance(context);
        Log.d(TAG, "首次访问网络图片，开始下载: " + imageUrl);

        new Thread(() -> {
            try {
                // 下载图片
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();

                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();

                    if (bitmap != null) {
                        // ③ 保存到存储卡
                        String savedPath = saveToStorage(context, bitmap, imageUrl);
                        Log.d(TAG, "保存到存储卡: " + savedPath);

                        // ④ 更新SharedPreferences标志
                        sharedUtil.setFirstVisitImage(false);
                        sharedUtil.putString("last_image_path", savedPath);

                        // ⑤ 更新UI
                        imageView.post(() -> imageView.setImageBitmap(bitmap));
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "图片下载失败: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * 将Bitmap保存到存储卡
     * @return 保存后的文件绝对路径
     */
    private String saveToStorage(Context context, Bitmap bitmap, String imageUrl) {
        try {
            // 获取存储目录
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CACHE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 用URL的hash作为文件名
            String fileName = "img_" + Math.abs(imageUrl.hashCode()) + ".jpg";
            File file = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "保存到存储卡失败: " + e.getMessage(), e);
            return null;
        }
    }

    /** 清除图片缓存 */
    public void clearCache(Context context) {
        try {
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CACHE_DIR);
            if (dir.exists()) {
                for (File f : dir.listFiles()) {
                    f.delete();
                }
            }
            SharedUtil.getInstance(context).setFirstVisitImage(true);
            Log.d(TAG, "图片缓存已清除");
        } catch (Exception e) {
            Log.e(TAG, "清除缓存失败: " + e.getMessage(), e);
        }
    }
}
