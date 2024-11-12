package com.hcmute.prse_be.service;

public interface EmailService {

    void sendMessage(String from, String to, String subject, String text);
}
