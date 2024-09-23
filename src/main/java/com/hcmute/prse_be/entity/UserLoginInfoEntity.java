package com.hcmute.prse_be.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_login_info")
public class UserLoginInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    @Column(name = "ip_addr")
    private String ipAddress;

    private String country;
    private String city;

    private String userAgent;

}
