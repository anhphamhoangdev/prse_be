package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.VideoLessonDraftEntity;
import com.hcmute.prse_be.repository.VideoLessonDraftRepository;
import org.springframework.stereotype.Service;

@Service
public class VideoLessonDraftServiceImpl implements VideoLessonDraftService {

    private final VideoLessonDraftRepository videoLessonDraftRepository;

    public VideoLessonDraftServiceImpl(VideoLessonDraftRepository videoLessonDraftRepository) {
        this.videoLessonDraftRepository = videoLessonDraftRepository;
    }

    @Override
    public VideoLessonDraftEntity findByLessonDraftId(Long lessonDraftId) {
        return videoLessonDraftRepository.findByLessonDraftId(lessonDraftId).orElse(null);
    }
}
