package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.LessonDraftEntity;
import com.hcmute.prse_be.repository.LessonDraftRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LessonDraftServiceImpl implements LessonDraftService{

    private final LessonDraftRepository lessonDraftRepository;

    public LessonDraftServiceImpl(LessonDraftRepository lessonDraftRepository) {
        this.lessonDraftRepository = lessonDraftRepository;
    }

    @Override
    public LessonDraftEntity save(LessonDraftEntity lessonDraftEntity) {
        return lessonDraftRepository.save(lessonDraftEntity);
    }

    @Override
    public List<Map<String, Object>> findAllWithStatus(String status) {
        List<Object[]> results;

        if ("ALL".equals(status)) {
            results = lessonDraftRepository.findAllWithInstructorInfo();
        } else {
            results = lessonDraftRepository.findByStatusWithInstructorInfo(status);
        }

        return results.stream().map(this::mapToLessonDraftWithInstructor).collect(Collectors.toList());
    }

    private Map<String, Object> mapToLessonDraftWithInstructor(Object[] row) {
        Map<String, Object> lesson = new HashMap<>();

        // Mapping theo thứ tự thực tế từ debug
        lesson.put("id", row[0]);                    // ID LESSON DRAFT
        lesson.put("chapterId", row[1]);             // ID CHAPTER
        lesson.put("createdAt", row[2]);             // CREATE AT
        lesson.put("isPublish", row[3]);             // isPublish
        lesson.put("orderIndex", row[4]);            // ORDERINDEX
        lesson.put("rejectedReason", row[5]);        // REJECTED REASON
        lesson.put("status", row[6]);                // STATUS
        lesson.put("title", row[7]);                 // TITLE
        lesson.put("type", row[8]);                  // TYPE
        lesson.put("updatedAt", row[9]);             // UPDATE AT
        // row[10] = Chapter TITLE
        // row[11] = Course TITLE

        // Instructor info
        Map<String, Object> instructor = new HashMap<>();
        instructor.put("name", row[12]);             // INSTRUCTOR NAME
        instructor.put("email", row[13]);            // INSTRUCTOR EMAIL

        lesson.put("instructor", instructor);

        return lesson;
    }
}
