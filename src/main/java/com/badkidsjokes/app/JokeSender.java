package com.badkidsjokes.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.*;

import sibApi.TransactionalSmsApi;

import sibModel.SendTransacSms;
import sibModel.SendSms;

/**
 * Sends emails via a Sendinblue SMTP mail server to a given recipient.
 * Also supports SMS.
 *
 * @version 0.2.0
 * @author Sivan Cooperman
 */
public class JokeSender {


    private String emails;
    private String texts;

    private String sender = "dailybadkidsjokes@gmail.com";
    private String username = "";
    private String password = "";

    private String host;
    private String key;

    public JokeSender(String filename) {
        // Reading in confidential info
        try {
            File options = new File(filename);
            Scanner scn = new Scanner(options);

            while ( scn.hasNextLine() ) {
                String[] line = scn.nextLine().split(":");
                switch( line[0] ) {
                    case "Username":
                        this.username = line[1];
                        break;
                    case "Password":
                        this.password = line[1];
                        break;
                    case "Host":
                        this.host = line[1];
                        break;
                    case "EmailRecipients":
                        this.emails = line[1];
                        break;
                    case "TextRecipients":
                        this.texts = line[1];
                        break;
                    case "ApiKey":
                        this.key = line[1];
                        break;
                }
            }
            scn.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void sendEmail(String body) throws MessagingException {
        // Setting options
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        // Instantiating authenticator
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(properties, authenticator);

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(this.sender));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(this.emails));

            message.setText(body);

            Transport.send(message);
        } catch(MessagingException e) {
            System.out.println(e);
        }
    }

    public void sendText(String body) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(this.key);

        ApiKeyAuth partnerKey = (ApiKeyAuth) defaultClient.getAuthentication("partner-key");
        partnerKey.setApiKey(this.key);

        TransactionalSmsApi apiInstance = new TransactionalSmsApi();

        for ( String recipient : this.texts.split(",") ) {
            SendTransacSms sendTransacSms = new SendTransacSms(); // SendTransacSms | Values to send a transactional SMS
            sendTransacSms.setSender("Bad Jokes");
            sendTransacSms.setRecipient(recipient);
            sendTransacSms.setContent(body);

            try {
                SendSms result = apiInstance.sendTransacSms(sendTransacSms);
                System.out.println(result);
            } catch (ApiException e) {
                System.err.println("Exception when calling TransactionalSmsApi#sendTransacSms");
                e.printStackTrace();
            }
        }
    }
}