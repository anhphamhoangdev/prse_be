package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CategoryStatisticDTO;
import com.hcmute.prse_be.dtos.RevenueStatisticsDTO;
import com.hcmute.prse_be.entity.AdminEntity;

import java.util.List;

public interface AdminService {
    AdminEntity findById(Long id);
    AdminEntity findByEmail(String email);
    boolean existsByEmail(String email);
    AdminEntity save(AdminEntity adminEntity);

    Double getTotalRevenue();
    Double getTotalRevenueByMonth(int month, int year);

    List<RevenueStatisticsDTO> getRevenueStatistics(int monthsCount);

    List<CategoryStatisticDTO> getCourseDistribution();
}
