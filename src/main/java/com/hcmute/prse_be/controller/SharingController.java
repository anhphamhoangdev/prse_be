package com.hcmute.prse_be.controller;

import com.hcmute.prse_be.config.Config;
import com.hcmute.prse_be.entity.CertificateEntity;
import com.hcmute.prse_be.service.CertificateService;
import com.hcmute.prse_be.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SharingController {

    private static final String INFO_IP_URL = Config.getParam("info_ip", "base_url");


    private final CertificateService certificateService;
    @Autowired
    public SharingController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }



    // Return certificate page
    @GetMapping("/sharing/certificate/{publicCode}")
    public String getCertificateByPublicCode(@PathVariable String publicCode, Model model) {
        LogService.getgI().info("[LoginController] getCertificateByPublicCode: " + publicCode);

        CertificateEntity certificate = certificateService.getCertificateByPublicCode(publicCode);
        if (certificate == null) {
            // If certificate is not found, add an error message to the model
            model.addAttribute("error", "Certificate not found for public code: " + publicCode);
            return "error"; // Assumes you have an error.html template
        }

        // Add certificate details to the model for rendering in the template
        model.addAttribute("certificate", certificate);
        model.addAttribute("nameInCertificate", certificate.getNameInCertificate());
        model.addAttribute("courseName", certificate.getCourseName());
        model.addAttribute("certificateUrl", certificate.getCertificateUrl());
        model.addAttribute("createdAt", certificate.getCreatedAt());

        return "index"; // Refers to index.html in the templates folder
    }
}