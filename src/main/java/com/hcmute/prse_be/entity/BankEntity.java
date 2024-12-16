package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banks")
public class BankEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private String shortName;
    private String logo;
    private Integer orderIndex;
}

