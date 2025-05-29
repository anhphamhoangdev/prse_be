package com.hcmute.prse_be.service;


import com.hcmute.prse_be.entity.LessonDraftEntity;

import java.util.List;
import java.util.Map;

public interface LessonDraftService {
    LessonDraftEntity save(LessonDraftEntity lessonDraftEntity);
    List<Map<String, Object>> findAllWithStatus(String status);
}
