package com.jakdang.labs.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_gift")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_gift_index")
    private Integer eventGiftIndex;

    @Column(name = "event_gift_name", length = 50, nullable = false, unique = true)
    private String eventGiftName;

    @Column(name = "event_gift_value", nullable = false)
    private Integer eventGiftValue;

    @Column(name = "event_gift_date")
    private LocalDateTime eventGiftDate;

    @Column(name = "event_gift_store_name", columnDefinition = "TEXT")
    private String eventGiftStoreName;
} 