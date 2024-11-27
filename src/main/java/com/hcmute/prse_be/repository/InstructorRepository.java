package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.InstructorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {
    InstructorEntity findByStudentId(Long studentId);
}
