package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {

    CertificateEntity findByStudentIdAndCourseId(Long studentId, Long courseId);

    CertificateEntity findByCourseId(Long courseId);

    CertificateEntity findByStudentId(Long studentId);
}
