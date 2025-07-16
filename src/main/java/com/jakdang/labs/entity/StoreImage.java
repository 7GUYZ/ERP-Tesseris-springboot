package com.jakdang.labs.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_image_index")
    private Integer storeImageIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_user_index", nullable = false)
    private Store storeUserIndex;

    @Column(name = "store_image", length = 200, nullable = false)
    private String storeImage;

    @Column(name = "store_main_image_status", length = 50)
    private String storeMainImageStatus;
} 