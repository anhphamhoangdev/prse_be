package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.UserSubmissionEntity;

import java.util.Optional;

public interface UserSubmissionService {
    UserSubmissionEntity save(UserSubmissionEntity submission);
    UserSubmissionEntity createSubmission(Long studentId, Long courseId, Long chapterId, Long lessonId,
                                          String code, String language, Boolean isCorrect,
                                          Long executionTime, Long memoryUsed);
    Optional<UserSubmissionEntity> getLatestSubmission(Long studentId, Long lessonId);
}
