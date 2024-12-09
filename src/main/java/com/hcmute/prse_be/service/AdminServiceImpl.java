package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CategoryStatisticDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.AdminEntity;
import com.hcmute.prse_be.repository.AdminRepository;
import com.hcmute.prse_be.repository.CourseSubCategoryRepository;
import com.hcmute.prse_be.repository.InstructorPlatformTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final InstructorPlatformTransactionRepository instructorPlatformTransactionRepository;
    private final CourseSubCategoryRepository courseSubCategoryRepository;

    public AdminServiceImpl(AdminRepository adminRepository, InstructorPlatformTransactionRepository instructorPlatformTransactionRepository, CourseSubCategoryRepository courseSubCategoryRepository) {
        this.adminRepository = adminRepository;
        this.instructorPlatformTransactionRepository = instructorPlatformTransactionRepository;
        this.courseSubCategoryRepository = courseSubCategoryRepository;
    }

    @Override
    public AdminEntity findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Override
    public AdminEntity save(AdminEntity adminEntity) {
        return adminRepository.save(adminEntity);
    }

    @Override
    public Double getTotalRevenue() {
        return instructorPlatformTransactionRepository.getTotalPlatformMoney();
    }

    @Override
    public Double getTotalRevenueByMonth(int month, int year) {
        return instructorPlatformTransactionRepository.getTotalPlatformMoneyByYearAndMonth(year, month);
    }

    @Override
    public List<RevenueStatisticsDTO> getRevenueStatistics(int monthsCount) {
        // Lấy ngày đầu của tháng hiện tại (không phải tháng tiếp theo)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.with(TemporalAdjusters.firstDayOfMonth());

        // Lấy ngày đầu của tháng cách đây monthsCount - 1 tháng
        LocalDateTime startDate = endDate.minusMonths(monthsCount - 1);

        List<Object[]> results = instructorPlatformTransactionRepository.getPlatformRevenueStatistics(
                startDate, endDate
        );

        return results.stream()
                .map(row -> new RevenueStatisticsDTO(
                        (String) row[0],  // month
                        ((Number) row[1]).doubleValue()  // revenue
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryStatisticDTO> getCourseDistribution() {
        // Lấy tất cả categories và số lượng khóa học
        List<CategoryStatisticDTO> allCategories = courseSubCategoryRepository.getCourseDistributionByCategory();

        // Sắp xếp theo số lượng giảm dần
        allCategories.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Nếu có 6 categories hoặc ít hơn, trả về tất cả
        if (allCategories.size() <= 6) {
            return allCategories;
        }

        // Lấy 6 categories đầu tiên
        List<CategoryStatisticDTO> topCategories = new ArrayList<>(allCategories.subList(0, 6));

        // Tính tổng số lượng của các categories còn lại
        long otherValue = allCategories.subList(6, allCategories.size())
                .stream()
                .mapToLong(CategoryStatisticDTO::getValue)
                .sum();

        // Thêm category "Khác" nếu có giá trị
        if (otherValue > 0) {
            topCategories.add(new CategoryStatisticDTO("Khác", otherValue));
        }

        return topCategories;
    }
}
