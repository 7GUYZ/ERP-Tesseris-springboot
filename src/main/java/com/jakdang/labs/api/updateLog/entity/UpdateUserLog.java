package com.jakdang.labs.api.updateLog.entity;

import java.time.LocalDateTime;

import com.jakdang.labs.api.notice.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Update_User_Log")
public class UpdateUserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "update_user_log_index")
    private Integer updateUserLogIndex;

    @Column(name = "update_user_index")
    private Integer updateUserIndex;

    @Column(name = "inflict_user_index")
    private Integer inflictUserIndex;

    @Column(name = "update_before_data")
    private String updateBeforeData;

    @Column(name = "update_after_data")
    private String updateAfterData;

    @Column(name = "update_data_value")
    private String updateDataValue;

    @Column(name = "update_user_log_update_time")
    private LocalDateTime updateUserLogUpdateTime;

    // 연관관계 추가
    @ManyToOne
    @JoinColumn(name = "update_user_index", insertable = false, updatable = false)
    private User updateUser;

    @ManyToOne
    @JoinColumn(name = "inflict_user_index", insertable = false, updatable = false)
    private User inflictUser;
}