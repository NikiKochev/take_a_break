package takeABreak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.User;

@Component
public class EmailService {
    private final static String FROM = "nikolaykochev@gmail.com";
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(User user) {
        String to = user.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject("Please verify your registration");
        String verifyURL = "localhost:8220/verify?code=" + user.getVerification();
        String content = "Dear"+user.getEmail()+"<br>"
                + "Please click the link below to verify your registration:<br>"
                +  verifyURL
                + "Thank you,<br>"
                + "Take a Break";

        message.setText(content);
        emailSender.send(message);

    }
}
