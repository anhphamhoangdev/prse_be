package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.InstructorEntity;

import java.util.List;

public interface InstructorService {

    long getTotalStudentOfInstructor(Long instructorId);

    long getTotalCourseOfInstructor(Long instructorId);

    InstructorEntity getInstructorByStudentId(Long studentId);

    List<RevenueStatisticsDTO> getRevenueStatistics(Long instructorId, int monthsCount);

    List<RecentEnrollmentDTO> getRecentEnrollments(Long instructorId);

    long getCountInstructor();

    void saveInstructor(InstructorEntity instructorEntity);

    long countByYearAndMonth(int currentYear, int currentMonth);
}
