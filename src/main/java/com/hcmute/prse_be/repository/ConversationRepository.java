package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

    List<ConversationEntity> findByInstructorId(Long instructorId);

    List<ConversationEntity> findByStudentId(Long studentId);

    Optional<ConversationEntity> findByStudentIdAndInstructorId(Long studentId, Long instructorId);

    @Query(value = "SELECT c.id, c.instructor_id, c.student_id, c.created_at, c.updated_at, " +
            "cm.content AS latest_message, cm.created_at AS latest_timestamp, " +
            "s.full_name AS participant_name, s.avatar_url AS avatar " +
            "FROM conversations c " +
            "LEFT JOIN students s ON c.student_id = s.id " +
            "LEFT JOIN (SELECT cm.conversation_id, cm.content, cm.created_at " +
            "           FROM chat_messages cm " +
            "           WHERE cm.created_at = (SELECT MAX(created_at) " +
            "                                 FROM chat_messages cm2 " +
            "                                 WHERE cm2.conversation_id = cm.conversation_id)) cm " +
            "ON c.id = cm.conversation_id " +
            "WHERE c.instructor_id = :instructorId", nativeQuery = true)
    List<Object[]> findConversationsWithLatestMessageByInstructorId(@Param("instructorId") Long instructorId);

    @Query(value = "SELECT c.id, c.instructor_id, c.student_id, c.created_at, c.updated_at, " +
            "cm.content AS latest_message, cm.created_at AS latest_timestamp, " +
            "i.full_name AS participant_name, i.avatar_url AS avatar " +
            "FROM conversations c " +
            "LEFT JOIN instructors i ON c.instructor_id = i.id " +
            "LEFT JOIN (SELECT cm.conversation_id, cm.content, cm.created_at " +
            "           FROM chat_messages cm " +
            "           WHERE cm.created_at = (SELECT MAX(created_at) " +
            "                                 FROM chat_messages cm2 " +
            "                                 WHERE cm2.conversation_id = cm.conversation_id)) cm " +
            "ON c.id = cm.conversation_id " +
            "WHERE c.student_id = :studentId", nativeQuery = true)
    List<Object[]> findConversationsWithLatestMessageByStudentId(@Param("studentId") Long studentId);
}