package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.QuizHistoryEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.request.QuizSubmitRequest;
import net.minidev.json.JSONArray;

import java.util.List;

public interface QuizService {

    JSONArray getQuizContent(Long lessonId);

    void saveQuizHistory(StudentEntity student, QuizSubmitRequest quizSubmitRequest);

    List<QuizHistoryEntity> getQuizHistory(Long studentId, Long lessonId);

}
