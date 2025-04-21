package com.hcmute.prse_be.response;

import com.hcmute.prse_be.entity.InstructorEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
public class InstructorDataResponse {
    private Long id;
    private Long studentId;
    private String fullName;
    private Double money;
    private Double totalRevenue;
    private Double fee;
    private String avatarUrl;
    private String title;
    private Integer totalStudent;
    private Integer totalCourse;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InstructorDataResponse(InstructorEntity instructor) {
        this.id = instructor.getId();
        this.studentId = instructor.getStudentId();
        this.fullName = instructor.getFullName();
        this.money = instructor.getMoney();
        this.fee = instructor.getFee();
        this.avatarUrl = instructor.getAvatarUrl();
        this.title = instructor.getTitle();
        this.totalStudent = instructor.getTotalStudent();
        this.totalCourse = instructor.getTotalCourse();
        this.isActive = instructor.getIsActive();
        this.createdAt = instructor.getCreatedAt();
        this.updatedAt = instructor.getUpdatedAt();
    }
}
