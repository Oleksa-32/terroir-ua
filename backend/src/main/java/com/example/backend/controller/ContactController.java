package com.example.backend.controller;

import com.example.backend.dto.ContactForm;
import com.example.backend.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<String> sendContact(@RequestBody ContactForm form) {
        contactService.sendEmail(form);
        return ResponseEntity.ok("Ваше повідомлення надіслано успішно!");
    }
}
