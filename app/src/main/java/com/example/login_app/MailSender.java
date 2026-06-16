package com.example.login_app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * SMTP邮件发送 — Thread + Handler（不依赖AsyncTask）
 */
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
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
                props.put("mail.smtp.socketFactory.port", String.valueOf(SMTP_PORT));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.connectiontimeout", "10000");
                props.put("mail.smtp.timeout", "10000");

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
                Log.d(TAG, "邮件发送成功 → " + recipientEmail);
                handler.post(callback::onSuccess);

            } catch (MessagingException e) {
                Log.e(TAG, "邮件发送失败: " + e.getMessage(), e);
                handler.post(() -> callback.onError(e.getMessage() != null ? e.getMessage() : "连接失败"));
            }
        }).start();
    }
}
