package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.UserSubmissionEntity;
import com.hcmute.prse_be.repository.UserSubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSubmissionServiceImpl implements UserSubmissionService {

    @Autowired
    private UserSubmissionRepository userSubmissionRepository;

    @Override
    @Transactional
    public UserSubmissionEntity save(UserSubmissionEntity submission) {
        UserSubmissionEntity saved = userSubmissionRepository.save(submission);
        return saved;
    }

    @Override
    @Transactional
    public UserSubmissionEntity createSubmission(Long studentId, Long courseId, Long chapterId, Long lessonId,
                                                 String code, String language, Boolean isCorrect,
                                                 Long executionTime, Long memoryUsed) {
        UserSubmissionEntity submission = new UserSubmissionEntity();
        submission.setStudentId(studentId);
        submission.setCourseId(courseId);
        submission.setChapterId(chapterId);
        submission.setLessonId(lessonId);
        submission.setSubmittedCode(code);
        submission.setLanguage(language);
        submission.setIsCorrect(isCorrect);
        submission.setExecutionTime(executionTime);
        submission.setMemoryUsed(memoryUsed);

        // Set score and status based on correctness
        if (isCorrect != null && isCorrect) {
            submission.setScore(100);
            submission.setStatus("PASSED");
        } else {
            submission.setScore(0);
            submission.setStatus("FAILED");
        }

        return save(submission);
    }

    @Override
    public Optional<UserSubmissionEntity> getLatestSubmission(Long studentId, Long lessonId) {
        try {
            return userSubmissionRepository.findTopByStudentIdAndLessonIdAndStatusOrderBySubmittedAtDesc(
                    studentId,
                    lessonId,
                    "PASSED"
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
