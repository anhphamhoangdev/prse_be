package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CoursePrerequisiteDTO;
import com.hcmute.prse_be.entity.CoursePrerequisiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisiteEntity, Long> {


    @Query("""
    SELECT NEW com.hcmute.prse_be.dtos.CoursePrerequisiteDTO(
        cp.id, 
        cp.prerequisite
    )
    FROM CoursePrerequisiteEntity cp
    WHERE cp.courseId = :courseId
""")
    List<CoursePrerequisiteDTO> findDTOsByCourseId(@Param("courseId") Long courseId);

}
