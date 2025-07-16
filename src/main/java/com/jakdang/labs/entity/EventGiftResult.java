package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_gift_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGiftResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "gift_user_index")
    private Integer giftUserIndex;

    @Column(name = "store_user_index")
    private Integer storeUserIndex;

    @Column(name = "join_time")
    private LocalDateTime joinTime;

    @Column(name = "event_gift_value")
    private Integer eventGiftValue;
} 