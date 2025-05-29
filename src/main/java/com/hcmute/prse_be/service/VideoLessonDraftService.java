package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.VideoLessonDraftEntity;

public interface VideoLessonDraftService {
    VideoLessonDraftEntity findByLessonDraftId(Long lessonDraftId);
}
