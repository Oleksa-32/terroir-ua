package com.example.backend.service.contact;

import com.example.backend.dto.ContactForm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String appEmail;

    @Override
    public void sendEmail(ContactForm form) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(appEmail);
        msg.setTo(appEmail);
        msg.setSubject("New contact‐form message");
        msg.setText(
                "From: " + form.getEmail() + "\n\n"
                        + "Message:\n" + form.getMessage()
        );
        mailSender.send(msg);
    }
}
