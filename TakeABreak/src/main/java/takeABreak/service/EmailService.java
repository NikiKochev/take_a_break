package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import takeABreak.controller.AbstractController;
import takeABreak.model.pojo.User;

@Service
public class EmailService extends AbstractController {
    private final static String FROM = "nikolaykochev@gmail.com";

    private JavaMailSender emailSender;

    @Autowired
    public EmailService (JavaMailSender javaMailSender){
        this.emailSender = javaMailSender;
    }

    public void sendSimpleMessage(User user) {
        String to = user.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(FROM);
        message.setSubject("Please verify your registration");
        String verifyURL = "http://localhost:8220/users/verify?code=" + user.getVerification();
        String content ="Please click the link below to verify your registration: \n "
                +  verifyURL
                + "\nThank you,\n"
                + "Take a Break";

        message.setText(content);
        emailSender.send(message);
    }
}
