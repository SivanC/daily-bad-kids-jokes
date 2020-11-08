package com.badkidsjokes.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Sends emails via a Sendinblue SMTP mail server to a given recipient
 *
 * @version 0.1.0
 * @author Sivan Cooperman
 */
public class JokeSender {


    private String recipients;

    private String sender = "dailybadkidsjokes@gmail.com";
    private String username = "";
    private String password = "";

    private String host;

    private Properties properties;
    private Authenticator authenticator;

    public JokeSender(String filename) {
        // Reading in confidential info
        try {
            File options = new File(filename);
            Scanner scn = new Scanner(options);

            this.username = scn.nextLine().split(":")[1];
            this.password = scn.nextLine().split(":")[1];
            this.host = scn.nextLine().split(":")[1];
            // InternetAddress.parse() takes a comma-separated list of addresses
            this.recipients = scn.nextLine().split(":")[1];
            scn.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

        // Setting options
        this.properties = new Properties();
        this.properties.put("mail.smtp.auth", "true");
        this.properties.put("mail.smtp.starttls.enable", "true");
        this.properties.put("mail.smtp.host", host);
        this.properties.put("mail.smtp.port", "587");

        // Instantiating authenticator
        this.authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }

    public void sendMessage(String body) throws MessagingException {
        Session session = Session.getInstance(properties, authenticator);

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(this.sender));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(this.recipients));

            message.setText(body);

            Transport.send(message);
        } catch(MessagingException e) {
            System.out.println(e);
        }
    }
}