package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findByQuestionId(Long questionId);

    void deleteByQuestionId(Long questionId);
}
