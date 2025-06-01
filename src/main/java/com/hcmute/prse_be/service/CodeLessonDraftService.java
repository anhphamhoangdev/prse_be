package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.CodeLessonDraftEntity;

public interface CodeLessonDraftService {

    CodeLessonDraftEntity findByLessonDraftId(Long lessonDraftId);
}
