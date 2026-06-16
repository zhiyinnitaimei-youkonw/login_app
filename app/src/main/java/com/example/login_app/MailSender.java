package com.example.login_app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    private static final String TAG = "MailSender";
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final int SMTP_PORT = 465;
    private static final String SENDER_EMAIL = "***@qq.com";
    private static final String SENDER_PASSWORD = "***REMOVED***";

    public interface Callback {
        void onSuccess();
        void onError(String msg);
    }

    public static void sendCode(String recipientEmail, String code, Callback callback) {
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            try {
                Log.d(TAG, "开始发送邮件 → " + recipientEmail);

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
                props.put("mail.smtp.socketFactory.port", String.valueOf(SMTP_PORT));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");
                props.put("mail.debug", "true");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject("验证码 - 喜马拉雅");
                message.setText("您好！\n\n您的验证码是：" + code
                        + "\n\n该验证码10分钟内有效。\n\n喜马拉雅团队");

                Transport.send(message);
                Log.d(TAG, "邮件发送成功");
                handler.post(callback::onSuccess);

            } catch (Throwable e) {
                // 捕获Throwable(含Exception+Error: NoClassDefFoundError等)
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String trace = sw.toString();
                Log.e(TAG, "邮件异常:\n" + trace);

                String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
                if (msg == null || msg.isEmpty()) msg = e.getClass().getName();
                if (msg.length() > 200) msg = msg.substring(0, 200);
                final String finalMsg = msg;

                // 写文件兜底(防止UI线程也崩)
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
