package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_gift_flag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGiftFlag {

    @Id
    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "gift_time")
    private LocalDateTime giftTime;

    @Column(name = "event_gift_name", length = 50)
    private String eventGiftName;
} 