package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_post_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorePostMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_post_master_index")
    private Integer storePostMasterIndex;

    @Column(name = "store_post_master_name", length = 100)
    private String storePostMasterName;

    @Column(name = "store_post_master_number", length = 100)
    private String storePostMasterNumber;
} 