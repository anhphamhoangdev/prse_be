package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.RecentEnrollmentDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.InstructorCommonTitleEntity;
import com.hcmute.prse_be.entity.InstructorEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.InstructorCommonTitleRepository;
import com.hcmute.prse_be.repository.InstructorPlatformTransactionRepository;
import com.hcmute.prse_be.repository.InstructorRepository;
import com.hcmute.prse_be.repository.StudentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorServiceImpl implements InstructorService{

    private final InstructorRepository instructorRepository;
    private final InstructorPlatformTransactionRepository transactionRepository;
    private final InstructorCommonTitleRepository instructorCommonTitleRepository;
    private final StudentRepository studentRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository, InstructorPlatformTransactionRepository transactionRepository, InstructorCommonTitleRepository instructorCommonTitleRepository, StudentRepository studentRepository) {
        this.instructorRepository = instructorRepository;
        this.transactionRepository = transactionRepository;
        this.instructorCommonTitleRepository = instructorCommonTitleRepository;
        this.studentRepository = studentRepository;
    }
    @Override
    public List<InstructorCommonTitleEntity> getAllTitles() {
        // Lấy danh sách vị trí từ database theo tên
        return instructorCommonTitleRepository.findAll();
    }

    @Override
    public void saveAvatarInstructor(String imageUrl, String authenticationName) {
        StudentEntity student = studentRepository.findByUsername(authenticationName);
        if(student != null){
            InstructorEntity instructor = instructorRepository.findByStudentId(student.getId());
            if(instructor != null){
                instructor.setAvatarUrl(imageUrl);
                instructorRepository.save(instructor);
            }
        } else {
            throw new RuntimeException("Instructor not found");
        }
    }

    @Override
    public InstructorEntity getInstructorById(Long instructorId) {
        return instructorRepository.findById(instructorId).orElse(null);
    }

    @Override
    public long getTotalStudentOfInstructor(Long instructorId) {
        return instructorRepository.countUniqueStudentsByInstructorId(instructorId);
    }

    @Override
    public long getTotalCourseOfInstructor(Long instructorId) {
        return instructorRepository.countCoursesByInstructorId(instructorId);
    }

    @Override
    public Double getTotalRevenueOfInstructor(Long instructorId) {
        Double totalRevenue = transactionRepository.getTotalRevenueByInstructorId(instructorId);
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    @Override
    public InstructorEntity getInstructorByStudentId(Long studentId) {
        return instructorRepository.findByStudentId(studentId);
    }

    @Override
    public List<RevenueStatisticsDTO> getRevenueStatistics(Long instructorId, int monthsCount) {
        // Lấy ngày đầu của tháng hiện tại (không phải tháng tiếp theo)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.with(TemporalAdjusters.firstDayOfMonth());

        // Lấy ngày đầu của tháng cách đây monthsCount - 1 tháng
        LocalDateTime startDate = endDate.minusMonths(monthsCount - 1);

        List<Object[]> results = transactionRepository.getRevenueStatistics(
                instructorId, startDate, endDate
        );

        return results.stream()
                .map(row -> new RevenueStatisticsDTO(
                        (String) row[0],  // month
                        ((Number) row[1]).doubleValue()  // revenue
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecentEnrollmentDTO> getRecentEnrollments(Long instructorId) {
        return transactionRepository.findRecentEnrollments(
                instructorId,
                PageRequest.of(0, 3)
        );
    }

    @Override
    public long getCountInstructor() {
        return instructorRepository.count();
    }

    @Override
    public void saveInstructor(InstructorEntity instructorEntity) {
        instructorRepository.save(instructorEntity);
    }

    @Override
    public long countByYearAndMonth(int currentYear, int currentMonth) {
        return instructorRepository.countByYearAndMonth(currentYear, currentMonth);
    }


}
