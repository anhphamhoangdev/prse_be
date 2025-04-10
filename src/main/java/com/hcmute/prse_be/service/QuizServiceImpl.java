package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.AnswerEntity;
import com.hcmute.prse_be.entity.QuestionEntity;
import com.hcmute.prse_be.entity.QuizHistoryEntity;
import com.hcmute.prse_be.entity.StudentEntity;
import com.hcmute.prse_be.repository.AnswerRepository;
import com.hcmute.prse_be.repository.QuestionRepository;
import com.hcmute.prse_be.repository.QuizHistoryRepository;
import com.hcmute.prse_be.request.QuizSubmitRequest;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService{

    private final QuizHistoryRepository quizHistoryRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public QuizServiceImpl(QuizHistoryRepository quizHistoryRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.quizHistoryRepository = quizHistoryRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public JSONArray getQuizContent(Long lessonId) {
        List<QuestionEntity> questions = questionRepository.findByLessonId(lessonId);
        JSONArray quizArray = new JSONArray();

        for (QuestionEntity question : questions) {
            JSONObject questionObj = new JSONObject();
            questionObj.put("id", question.getId());
            questionObj.put("text", question.getText());

            List<AnswerEntity> answers = answerRepository.findByQuestionId(question.getId());
            JSONArray answersArray = new JSONArray();
            for (AnswerEntity answer : answers) {
                JSONObject answerObj = new JSONObject();
                answerObj.put("id", answer.getId());
                answerObj.put("text", answer.getText());
                answerObj.put("isCorrect", answer.getIsCorrect());
                answersArray.add(answerObj);
            }
            questionObj.put("answers", answersArray);
            quizArray.add(questionObj);
        }

        return quizArray;
    }

    @Override
    public void saveQuizHistory(StudentEntity student, QuizSubmitRequest quizSubmitRequest) {
        QuizHistoryEntity quizHistory = new QuizHistoryEntity();
        quizHistory.setStudentId(student.getId());
        quizHistory.setLessonId(quizSubmitRequest.getLessonId());
        quizHistory.setScore(quizSubmitRequest.getScore());
        quizHistory.setTotalQuestions(quizSubmitRequest.getTotalQuestions());
        quizHistory.setCorrectAnswers(quizSubmitRequest.getCorrectAnswers());
        quizHistoryRepository.save(quizHistory);
    }


    @Override
    public List<QuizHistoryEntity> getQuizHistory(Long studentId, Long lessonId) {
        List<QuizHistoryEntity> quizHistories = quizHistoryRepository.findByStudentIdAndLessonIdOrderByIdDesc(studentId, lessonId);
        if (quizHistories != null) {
            return quizHistories;
        }
        return List.of();
    }
}
