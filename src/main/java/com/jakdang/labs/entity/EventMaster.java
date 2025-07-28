package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_master_index")
    private Integer eventMasterIndex;

    @Column(name = "evnet_master_name", length = 500, nullable = false, unique = true)
    private String eventMasterName;

    @Column(name = "event_master_contidion", length = 500, nullable = false)
    private String eventMasterCondition;

    @Column(name = "event_master_limit", nullable = false)
    private Integer eventMasterLimit;

    @Column(name = "event_master_count")
    private Integer eventMasterCount;

    @Column(name = "event_master_userIndex")
    private Integer eventMasterUserIndex;

    @Column(name = "event_master_num")
    private Integer eventMasterNum;
} 