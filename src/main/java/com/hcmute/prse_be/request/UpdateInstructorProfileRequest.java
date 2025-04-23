package com.hcmute.prse_be.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateInstructorProfileRequest {
    private String fullName;
    private String title;
}