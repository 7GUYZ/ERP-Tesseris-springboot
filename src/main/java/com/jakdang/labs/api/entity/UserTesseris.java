package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "`user_tesseris`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTesseris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "users_id")
    private String usersId;

    // 다른 필드 생략 
}
