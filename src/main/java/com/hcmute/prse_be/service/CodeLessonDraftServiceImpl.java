package com.hcmute.prse_be.service;

import com.hcmute.prse_be.entity.CodeLessonDraftEntity;
import com.hcmute.prse_be.repository.CodeLessonDraftRepository;
import org.springframework.stereotype.Service;

@Service
public class CodeLessonDraftServiceImpl implements CodeLessonDraftService{

    private final CodeLessonDraftRepository codeLessonDraftRepository;

    public CodeLessonDraftServiceImpl(CodeLessonDraftRepository codeLessonDraftRepository) {
        this.codeLessonDraftRepository = codeLessonDraftRepository;
    }


    @Override
    public CodeLessonDraftEntity findByLessonDraftId(Long lessonDraftId) {
        return codeLessonDraftRepository.findByLessonDraftId(lessonDraftId).orElse(null);
    }
}
