package com.hcmute.prse_be.response;

import com.hcmute.prse_be.entity.VideoLessonEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VideoLessonInfoResponse {
    private Long id;
    private Long lessonId;
    private String videoUrl;
    private Double duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isComplete;

    public VideoLessonInfoResponse() {
    }

    public VideoLessonInfoResponse(VideoLessonEntity entity) {
        this.id = entity.getId();
        this.lessonId = entity.getLessonId();
        this.videoUrl = entity.getVideoUrl();
        this.duration = entity.getDuration();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }


}
