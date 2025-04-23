package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.InstructorCommonTitleEntity;
import com.hcmute.prse_be.entity.InstructorEntity;

import java.util.List;

public interface InstructorService {

    InstructorEntity getInstructorById(Long instructorId);

    long getTotalStudentOfInstructor(Long instructorId);

    long getTotalCourseOfInstructor(Long instructorId);

    Double getTotalRevenueOfInstructor(Long instructorId);

    InstructorEntity getInstructorByStudentId(Long studentId);

    List<RevenueStatisticsDTO> getRevenueStatistics(Long instructorId, int monthsCount);

    List<RecentEnrollmentDTO> getRecentEnrollments(Long instructorId);

    long getCountInstructor();

    void saveInstructor(InstructorEntity instructorEntity);

    long countByYearAndMonth(int currentYear, int currentMonth);
    List<InstructorCommonTitleEntity> getAllTitles();


    void saveAvatarInstructor(String imageUrl, String authenticationName);
}
