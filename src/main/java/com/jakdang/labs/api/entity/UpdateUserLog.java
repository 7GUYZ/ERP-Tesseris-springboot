package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "update_user_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "update_user_log_index")
    private Integer updateUserLogIndex;

    @Column(name = "update_user_index")
    private Integer updateUserIndex;

    @Column(name = "inflict_user_index")
    private Integer inflictUserIndex;

    @Column(name = "update_before_data", columnDefinition = "TEXT")
    private String updateBeforeData;

    @Column(name = "update_after_data", columnDefinition = "TEXT")
    private String updateAfterData;

    @Column(name = "update_user_log_update_time")
    private LocalDateTime updateUserLogUpdateTime;

    @Column(name = "update_data_value", columnDefinition = "TEXT")
    private String updateDataValue;
} 