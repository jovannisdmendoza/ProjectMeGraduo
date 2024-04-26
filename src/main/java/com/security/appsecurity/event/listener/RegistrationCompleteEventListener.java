package com.security.appsecurity.event.listener;

import java.io.UnsupportedEncodingException;



import com.security.appsecurity.Persistence.Entity.UseEntity;
import com.security.appsecurity.Persistence.Services.UserServices;
import com.security.appsecurity.event.RegistrationCompleteEvent;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>  {
    
    private final UserServices userService;
    private final JavaMailSender mailSender;

    
    private UseEntity theUser;

    public void sendPasswordResetVerificationEmail(String url, UseEntity theUsers) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, "+ theUsers.getUsername()+ ", </p>"+
                "<p><b>You recently requested to reset your password,</b>"+"" +
                "Please, follow the link below to complete the action.</p>"+
                "<a href=\"" +url+ "\">Reset password</a>"+
                "<p> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("gamestuto2013@gmail.com", senderName);
        messageHelper.setTo(theUsers.getUsername());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
         // 1. Get the newly registered user
         theUser = event.getUser();
        //2. Create a verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        //3. Save the verification token for the user
        userService.saveUserVerificationToken(theUser, verificationToken);
        //4 Build the verification url to be sent to the user
        String url = event.getApplicationUrl()+"/auth/verifyEmail?token="+verificationToken;
        //5. Send the email.
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration :  {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, "+ theUser.getUsername()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("gamestuto2013@gmail.com", senderName);
        messageHelper.setTo(theUser.getUsername());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
