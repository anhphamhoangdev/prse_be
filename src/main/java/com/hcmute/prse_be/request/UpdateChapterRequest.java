package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class UpdateChapterRequest {
    private InnerChapter chapter;
    // inner class have title and orderindex
    @Data
    public static class InnerChapter {
        private String title;
        private Integer orderIndex;
    }
}
