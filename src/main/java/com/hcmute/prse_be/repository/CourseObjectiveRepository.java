package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CourseObjectiveDTO;
import com.hcmute.prse_be.entity.CourseObjectiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseObjectiveRepository extends JpaRepository<CourseObjectiveEntity, Long> {
    List<CourseObjectiveEntity> findByCourseId(Long courseId);


    @Query("""
    SELECT NEW com.hcmute.prse_be.dtos.CourseObjectiveDTO(
        co.id, 
        co.objective
    )
    FROM CourseObjectiveEntity co
    WHERE co.courseId = :courseId
""")
    List<CourseObjectiveDTO> findDTOsByCourseId(@Param("courseId") Long courseId);
}
