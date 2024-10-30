package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.CourseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findAllByIsPublishTrueAndOriginalPrice(double originalPrice, Pageable pageable);
}
