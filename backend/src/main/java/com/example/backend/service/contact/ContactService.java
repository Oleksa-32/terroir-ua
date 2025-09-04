package com.example.backend.service.contact;

import com.example.backend.dto.ContactForm;

public interface ContactService {
    void sendEmail(ContactForm form);
}
