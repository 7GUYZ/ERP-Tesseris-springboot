package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cms_access_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmsAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cms_access_log_index")
    private Integer cmsAccessLogIndex;

    @Column(name = "cms_access_user_index", nullable = false)
    private Integer cmsAccessUserIndex;

    @Column(name = "cms_access_user_value", length = 100)
    private String cmsAccessUserValue;

    @Column(name = "cms_access_user_ip", length = 100)
    private String cmsAccessUserIp;

    @Column(name = "cms_access_user_time")
    private LocalDateTime cmsAccessUserTime;
} 