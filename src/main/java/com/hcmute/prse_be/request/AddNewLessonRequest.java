package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class AddNewLessonRequest {
    private NewLessonInfo lesson;
    @Data
    public static class NewLessonInfo {
        Long id;
        String title;
        String type;
        boolean publish;
        int orderIndex;
    }
}
