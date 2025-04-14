package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.CertificateEntity;
import com.hcmute.prse_be.entity.CourseEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;

import java.io.IOException;

public interface CertificateService {

    CertificateEntity createCertificateAndUploadCloudinary(StudentEntity student, CourseEntity course, InstructorEntity instructor) throws IOException;

    CertificateEntity getCertificateByStudentAndCourse(Long studentId, Long courseId);

    CertificateEntity getCertificateByPublicCode(String publicCode);
}
