package com.example.login_app;

import android.os.AsyncTask;
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
 * SMTP邮件发送工具 — QQ邮箱SSL 465
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
        new AsyncTask<Void, Void, Boolean>() {
            private String errorMsg;

            @Override
            protected Boolean doInBackground(Void... voids) {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.socketFactory.port", SMTP_PORT);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                            }
                        });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(SENDER_EMAIL));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                    message.setSubject("验证码 - 喜马拉雅");

                    String content = "您好！\n\n"
                            + "您的验证码是：" + code + "\n\n"
                            + "该验证码10分钟内有效，请尽快完成验证。\n\n"
                            + "如果您没有发起此请求，请忽略此邮件。\n\n"
                            + "喜马拉雅团队";

                    message.setText(content);
                    Transport.send(message);
                    Log.d(TAG, "邮件已发送至 " + recipientEmail);
                    return true;
                } catch (MessagingException e) {
                    errorMsg = e.getMessage();
                    Log.e(TAG, "邮件发送失败: " + errorMsg);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onError(errorMsg != null ? errorMsg : "未知错误");
                }
            }
        }.execute();
    }
}
