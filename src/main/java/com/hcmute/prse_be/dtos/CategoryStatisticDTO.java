package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryStatisticDTO {
    private String name;
    private Long value;
}
