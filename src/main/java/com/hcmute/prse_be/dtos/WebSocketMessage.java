package com.hcmute.prse_be.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    private String type;
    private String message;
    private Object data;
    private String status;
    private String time;

    public static WebSocketMessage success(String message) {
        return new WebSocketMessage(
                "NOTIFICATION",
                message,
                null,
                "SUCCESS",
                LocalDateTime.now().toString()
        );
    }

    public static WebSocketMessage info(String message, Object data) {
        return new WebSocketMessage(
                "NOTIFICATION",
                message,
                data,
                "INFO",
                LocalDateTime.now().toString()
        );
    }

    // UPLOAD_COMPLETE
    public static WebSocketMessage uploadComplete(String message, Object data) {
        return new WebSocketMessage(
                "UPLOAD_COMPLETE",
                message,
                data,
                "SUCCESS",
                LocalDateTime.now().toString()
        );
    }

    public static WebSocketMessage uploadStarted(String message, Object data) {
        return new WebSocketMessage(
                "UPLOAD_STARTED",
                message, // title
                data, // data : message
                "INFO",
                LocalDateTime.now().toString()
        );
    }
}
