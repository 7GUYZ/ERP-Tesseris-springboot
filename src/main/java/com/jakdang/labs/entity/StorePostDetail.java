package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_post_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorePostDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_post_detail_index")
    private Integer storePostDetailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_post_master_index")
    private StorePostMaster storePostMaster;

    @Column(name = "store_post_detail_name", length = 100)
    private String storePostDetailName;

    @Column(name = "store_post_detail_number", length = 100)
    private String storePostDetailNumber;
} 