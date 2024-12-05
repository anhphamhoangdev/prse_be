package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.InstructorEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InstructorService {
    InstructorEntity getInstructorByStudentId(Long studentId);

    List<RevenueStatisticsDTO> getRevenueStatistics(Long instructorId, int monthsCount);

    List<RecentEnrollmentDTO> getRecentEnrollments(Long instructorId);

    long getCountInstructor();


    long countByYearAndMonth(int currentYear, int currentMonth);
}
