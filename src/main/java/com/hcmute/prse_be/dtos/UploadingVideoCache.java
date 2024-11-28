package com.hcmute.prse_be.dtos;

import java.util.HashMap;
import java.util.Map;

public class UploadingVideoCache {
    private static final UploadingVideoCache INSTANCE = new UploadingVideoCache();
    private final Map<String, UploadingVideoDetail> uploadingVideo = new HashMap<>();

    // Private constructor to prevent instantiation
    private UploadingVideoCache() {}

    // Method to get the singleton instance
    public static UploadingVideoCache getInstance() {
        return INSTANCE;
    }

    // Method to get the map
    public Map<String, UploadingVideoDetail> getUploadingVideo() {
        return uploadingVideo;
    }
}
