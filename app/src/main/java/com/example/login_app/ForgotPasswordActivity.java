package com.example.login_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendEmail;

    private static final String SENDER_EMAIL = "***@qq.com";
    private static final String SENDER_PASSWORD = "***REMOVED***";
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final int SMTP_PORT = 465;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_email);
        btnSendEmail = findViewById(R.id.btn_send_email);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SendEmailTask().execute(email);
            }
        });
    }

    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnSendEmail.setEnabled(false);
            Toast.makeText(ForgotPasswordActivity.this, "正在发送邮件...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String recipientEmail = params[0];

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
                message.setSubject("找回密码 - 喜马拉雅");

                String verificationCode = generateVerificationCode();
                String emailContent = "亲爱的用户，您好！\n\n" +
                        "您正在尝试找回密码。您的验证码是：" + verificationCode + "\n\n" +
                        "该验证码10分钟内有效，请尽快完成验证。\n\n" +
                        "如果您没有发起此请求，请忽略此邮件。\n\n" +
                        "感谢您的使用！\n" +
                        "喜马拉雅团队";

                message.setText(emailContent);
                Transport.send(message);
                return true;

            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            btnSendEmail.setEnabled(true);

            if (success) {
                Toast.makeText(ForgotPasswordActivity.this, "邮件已发送，请查收", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "邮件发送失败，请检查网络或邮箱设置", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }
}
