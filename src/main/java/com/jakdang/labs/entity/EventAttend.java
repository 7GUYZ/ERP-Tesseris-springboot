package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_attend")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAttend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_attend_index")
    private Integer eventAttendIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_master_index")
    private EventMaster eventMaster;

    @Column(name = "event_attend_user")
    private Integer eventAttendUser;
} 