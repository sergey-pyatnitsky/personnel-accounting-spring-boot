package com.personnel_accounting.service.email;

public interface EmailService {

    void sendSimpleEmail(String toAddress, String subject, String message);
}
