package com.example.login_app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private static final String TAG = "MailSender";
    private static String smtpHost = "smtp.qq.com";
    private static int smtpPort = 465;
    private static String senderEmail = "";
    private static String senderPassword = "";
    private static boolean loaded = false;

    private static void loadConfig() {
        if (loaded) return;
        try {
            // 优先读取外部配置文件(不在git中,包含真实凭据)
            Properties props = new Properties();
            // 从assets读取(仅本地存在,gitignore排除)
            java.io.File f = new java.io.File("app/src/main/assets/mail.properties");
            if (f.exists()) {
                try (InputStream is = new java.io.FileInputStream(f)) {
                    props.load(is);
                    Log.d(TAG, "加载本地mail.properties");
                }
            }
            smtpHost = props.getProperty("mail.smtp.host", "smtp.qq.com");
            smtpPort = Integer.parseInt(props.getProperty("mail.smtp.port", "465"));
            senderEmail = props.getProperty("mail.sender.email", "");
            senderPassword = props.getProperty("mail.sender.password", "");
            loaded = true;
        } catch (Exception e) {
            Log.e(TAG, "加载邮件配置失败: " + e.getMessage());
        }
    }

    public interface Callback {
        void onSuccess();
        void onError(String msg);
    }

    public static void sendCode(String recipientEmail, String code, Callback callback) {
        loadConfig();
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                Log.d(TAG, "开始发送邮件 → " + recipientEmail);

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", String.valueOf(smtpPort));
                props.put("mail.smtp.socketFactory.port", String.valueOf(smtpPort));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");

                final String email = senderEmail;
                final String pass = senderPassword;

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(email, pass);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject("验证码 - 喜马拉雅");
                message.setText("您好！\n\n您的验证码是：" + code
                        + "\n\n该验证码10分钟内有效。\n\n喜马拉雅团队");

                Transport.send(message);
                Log.d(TAG, "邮件发送成功");
                handler.post(callback::onSuccess);

            } catch (Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String trace = sw.toString();
                Log.e(TAG, "邮件异常:\n" + trace);

                String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
                if (msg == null || msg.isEmpty()) msg = e.getClass().getName();
                if (msg.length() > 200) msg = msg.substring(0, 200);
                final String finalMsg = msg;

                try {
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(
                            "/data/data/com.example.login_app/files/smtp_crash.log", true);
                    fos.write(("=== SMTP Crash ===\n" + trace + "\n").getBytes());
                    fos.close();
                } catch (Exception ignored) {}

                handler.post(() -> callback.onError(finalMsg));
            }
        }).start();
    }
}
