package com.example.guardcheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Session session;
    private String email;
    private String subject;
    private String message;
    private ProgressDialog progressDialog;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public JavaMailAPI(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            Properties props = new Properties();

            // Using Port 465
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.user", Credentials.EMAIL);

            // Using Port 587
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.user", Credentials.EMAIL);

            //mail.3slk.com
//            props.put("mail.smtp.host", "mail.3slk.com");
//            props.put("mail.smtp.ssl.trust", "*");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.user", Credentials.EMAIL);

            session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD);
                }
            });

            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(Credentials.EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setText(message);

            Transport.send(mm);

        } catch (MessagingException e) {
            Log.d("Test", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}