package com.jakdang.labs.api.cmsAccessLog.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Cms_Access_Log")
@Getter
@Setter
public class CmsAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cms_access_log_index")
    private Integer cmsAccessLogIndex;

    @Column(name = "cms_access_user_index")
    private Integer cmsAccessUserIndex;

    @Column(name = "cms_access_user_value")
    private String cmsAccessUserValue;

    @Column(name = "cms_access_user_ip")
    private String cmsAccessUserIp;

    @Column(name = "cms_access_user_time")
    private LocalDateTime cmsAccessUserTime;
}